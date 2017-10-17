package cn.rongcapital.chorus.modules.export_data;

import cn.rongcapital.chorus.modules.utils.retry.LineageTasklet;
import cn.rongcapital.chorus.modules.utils.retry.listener.LineageStepListener;
import cn.rongcapital.log.JobListenerForLog;
import com.alibaba.fastjson.JSON;
import cn.rongcapital.chorus.hive.jdbc.HiveClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by alan on 24/04/2017.
 */
@Slf4j
@org.springframework.context.annotation.Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = {"cn.rongcapital.chorus.modules.import_data","cn.rongcapital.chorus.modules.utils.retry"})
public class ExportDataJob {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    public JobExecutionListener logListener() {
        return new JobListenerForLog();
    }

    @Bean
    public LineageStepListener lineageStepListener(){
        return new LineageStepListener();
    }

    @Bean
    public Job job(@Qualifier("exportDataStep1") Step exportDataStep1,
                   @Qualifier("logListener") JobExecutionListener logListener) {
        return jobs.get("exportDataJob").listener(logListener).start(exportDataStep1).build();
    }

    @Bean
    public Step exportDataStep1(
            @Value("${projectId}") String projectId,
            @Value("${dwConnectUrl}") String dwConnectUrl,
            @Value("${dwDbName}") String dwDbName,
            @Value("${dwTableName}") String dwTableName,
            @Value("${dwUserName}") String dwUserName,
            @Value("${rdbName}") String rdbName,
            @Value("${rdbConnectUrl}") String rdbConnectUrl,
            @Value("${rdbUserName}") String rdbUserName,
            @Value("${rdbPassword}") String rdbPassword,
            @Value("${rdbTable}") String rdbTable,
            @Value("${where:}") String where,
            @Value("${columnNameMapStr}") String columnNameMapStr,
            @Value("${retryCount:}") Integer retryCount,
            @Qualifier("lineageStepListener")LineageStepListener lineageStepListener
    ) {
        return steps.get("exportDataStep1").listener(lineageStepListener).tasklet(new LineageTasklet() {
            @Override
            protected Vertex inputs(ChunkContext chunkContext) {
                InternalTableVertex internalTableVertex = new InternalTableVertex();
                internalTableVertex.add(dwDbName, dwTableName);
                return internalTableVertex;
            }

            @Override
            protected Vertex outputs(ChunkContext chunkContext) {
                ExternalTableVertex externalTableVertex = new ExternalTableVertex();
                externalTableVertex.add(projectId, rdbName, rdbUserName, rdbConnectUrl, rdbTable);
                return externalTableVertex;
            }

            @Override
            public RepeatStatus executeWithLineage(ChunkContext chunkContext, StepContribution contribution) throws Exception {

                StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
                JobParameters jobParameters = stepExecution.getJobParameters();

                log.info("---projectId: {}", projectId);
                log.info("---dwConnectUrl: {}", dwConnectUrl);
                log.info("---dwDbName: {}", dwDbName);
                log.info("---dwTableName: {}", dwTableName);
                log.info("---dwUserName: {}", dwUserName);
                log.info("---rdbConnectUrl: {}", rdbConnectUrl);
                log.info("---rdbUserName: {}", rdbUserName);
                log.info("---rdbTable: {}", rdbTable);
                log.info("---where: {}", where);
                log.info("---columnNameMapStr: {}", columnNameMapStr);
                log.info("---jobParameters: {}", jobParameters);
                log.info("---retryCount: {}", retryCount);

                stepExecution.getExecutionContext().put("projectId", projectId);
                // job初始化参数
                Map<String, String> source2TempColumnMap = new HashMap<String, String>();
                JSON.parseObject(columnNameMapStr).entrySet().forEach(
                        e -> {
                            String tempColumn = String.valueOf(e.getValue()).toLowerCase();
                            String targetColumn = e.getKey();
                            if (!StringUtils.isBlank(tempColumn)) {
                                source2TempColumnMap.put(tempColumn, targetColumn);
                            }
                        }
                );

                // 变量准备
                String sourceTableFullName = dwDbName + "." + dwTableName;
                String tempTableName = dwTableName + "_" + String.valueOf(System.currentTimeMillis());
                String tempTableFullName = dwDbName + "." + tempTableName;
                String dropTempTableSql = getDropTempTableSql(tempTableFullName, stepExecution);
                String projectName = dwDbName.substring(dwDbName.indexOf("_") + 1);

                try {
                    // 0.拼接临时表列信息SQL, 不考虑进行分区.
                    String getMetadataFromSourceTableSql = String.format(" SELECT * from %s LIMIT 1 ", sourceTableFullName);
                    stepExecution.getExecutionContext()
                            .putString("Get MetaData from source table SQL:", getMetadataFromSourceTableSql);
                    final Map<String, String> sourceTableColumnMap = HiveClient.getInstance().executeHiveSql(dwConnectUrl, dwUserName, projectName, "", dwDbName, "",
                            getMetadataFromSourceTableSql, dropTempTableSql, new HiveClient.ResultSetConsumer<Map<String, String>>() {

                                @Override
                                public Map<String, String> apply(ResultSet rs) throws SQLException {
                                    Map<String, String> sourceTableColumnMap = new LinkedHashMap<>();
                                    ResultSetMetaData sourceTableMetadata = rs.getMetaData();
                                    for (int i = 1; i <= sourceTableMetadata.getColumnCount(); i++) {
                                        String columnLabel = sourceTableMetadata.getColumnLabel(i).split("\\.")[1];
                                        String type = combineTypeAndLength(
                                                sourceTableMetadata.getColumnTypeName(i),
                                                sourceTableMetadata.getPrecision(i),
                                                sourceTableMetadata.getScale(i));
                                        sourceTableColumnMap.put(columnLabel, type);
                                    }

                                    return sourceTableColumnMap;
                                }
                            });
                    if(sourceTableColumnMap == null)throw new RuntimeException(String.format("query MetaData from source table error. table:%s", sourceTableFullName));

                    String tempTableColumnDefStr = sourceTableColumnMap.entrySet().stream()
                            .filter(e -> source2TempColumnMap.containsKey(e.getKey()))
                            .map(e -> source2TempColumnMap.get(e.getKey()) + " " + e.getValue())
                            .collect(Collectors.joining(","));
                    log.info("Temp table column def: {}", tempTableColumnDefStr);
                    stepExecution.getExecutionContext().putString("Source table's columns", tempTableColumnDefStr);

                    // 1.创建临时表
                    String createTempTableSql = String.format(" CREATE TABLE %s ( %s )  STORED AS TEXTFILE ",
                            tempTableFullName, tempTableColumnDefStr);
                    log.info("Temp table creation sql: {}", createTempTableSql);
                    stepExecution.getExecutionContext().putString("Create temp table SQL:", createTempTableSql);
                    boolean f = HiveClient.getInstance().executeHiveSqlNoRes(dwConnectUrl, dwUserName,projectName,"",dwDbName,"",createTempTableSql,dropTempTableSql);
                    if(!f)throw new SQLException(String.format("execute SQL Error. SQL:%s",createTempTableSql));

                    // 2.临时表导入目标表
                    String sourceTableColumns = sourceTableColumnMap.entrySet().stream()
                            .filter(e -> source2TempColumnMap.containsKey(e.getKey()))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.joining(","));
                    String copyToTempTableSql = String.format(
                            "INSERT OVERWRITE TABLE %s SELECT %s FROM %s ",
                            tempTableFullName, sourceTableColumns, sourceTableFullName);
                    if (StringUtils.isNotBlank(where)) {
                        String today = jobParameters.getString("$whereToday");
                        String yesterday = jobParameters.getString("$whereYesterday");
                        if (StringUtils.isBlank(today)) {
                            today = new DateTime().toString("yyyy-MM-dd");
                        }
                        if (StringUtils.isBlank(yesterday)) {
                            yesterday = new DateTime().plusDays(-1).toString("yyyy-MM-dd");
                        }
                        String var1 = jobParameters.getString("$whereVar1");
                        String var2 = jobParameters.getString("$whereVar2");
                        String var3 = jobParameters.getString("$whereVar3");
                        String var4 = jobParameters.getString("$whereVar4");
                        String var5 = jobParameters.getString("$whereVar5");
                        String finalWhere = where;
                        finalWhere = finalWhere.replaceAll("\\$whereToday", today);
                        finalWhere = finalWhere.replaceAll("\\$whereYesterday", yesterday);
                        if (!StringUtils.isBlank(var1)) finalWhere = finalWhere.replaceAll("\\$whereVar1", var1);
                        if (!StringUtils.isBlank(var2)) finalWhere = finalWhere.replaceAll("\\$whereVar2", var2);
                        if (!StringUtils.isBlank(var3)) finalWhere = finalWhere.replaceAll("\\$whereVar3", var3);
                        if (!StringUtils.isBlank(var4)) finalWhere = finalWhere.replaceAll("\\$whereVar4", var4);
                        if (!StringUtils.isBlank(var5)) finalWhere = finalWhere.replaceAll("\\$whereVar5", var5);
                        copyToTempTableSql = copyToTempTableSql + " WHERE " + finalWhere;
                    }
                    log.info("Copy to temp table sql: {}", copyToTempTableSql);
                    stepExecution.getExecutionContext().putString("Copy to temp table data SQL:", copyToTempTableSql);
                    f = HiveClient.getInstance().executeHiveSqlNoRes(dwConnectUrl, dwUserName, projectName, "", dwDbName, "", copyToTempTableSql, dropTempTableSql);
                    if(!f)throw new SQLException(String.format("execute SQL Error. SQL:%s", copyToTempTableSql));
                    // 3.Sqoop导RDB数据到临时表
                    String rdbTableColumns = sourceTableColumnMap.entrySet().stream()
                            .filter(e -> source2TempColumnMap.containsKey(e.getKey()))
                            .map(e -> source2TempColumnMap.get(e.getKey()))
                            .collect(Collectors.joining(","));
                    StringBuilder sb = new StringBuilder();
                    sb.append("sqoop export")
                            .append(" -Dmapreduce.job.queuename=").append(projectName)
                            .append(" -Dtez.queue.name=").append(projectName);
                    String finalRdbConnectUrl = rdbConnectUrl.startsWith("jdbc:mysql") ?
                            rdbConnectUrl + "?useUnicode=true&characterEncoding=utf-8" : rdbConnectUrl;
                    sb.append(" --connect '").append(finalRdbConnectUrl).append("'");
                    sb.append(" --username '").append(rdbUserName).append("'");
                    sb.append(" --password '").append(rdbPassword).append("'");
                    sb.append(" --table '").append(rdbTable).append("'");
                    sb.append(" --hcatalog-database '").append(dwDbName).append("'");
                    sb.append(" --hcatalog-table '").append(tempTableName).append("'");
                    sb.append(" --input-fields-terminated-by '").append("\\001").append("'");
                    sb.append(" --columns '").append(rdbTableColumns).append("'");
                    sb.append(" --m 1");

                    String sqoopCommand = sb.toString();
                    log.info("Sqoop export data sqoopCommand: {}", sqoopCommand);
                    stepExecution.getExecutionContext().putString("Sqoop Command", sqoopCommand);
                    LocalShellTool local = new LocalShellTool();
                    Map<String, String> sqoopResult = local.exec(sqoopCommand);
                    log.info("Sqoop export data sqoopResult: {}", sqoopResult);
                    if (sqoopResult != null) {
                        String strResult = sqoopResult.get(LocalShellTool.RESULT_KEY);
                        String strError = sqoopResult.get(LocalShellTool.ERROR_KEY);
                        int exitValue = Integer.parseInt(sqoopResult.get(LocalShellTool.EXITVALUE_KEY));
                        stepExecution.getExecutionContext().putString("Sqoop ExitValue", String.valueOf(exitValue));
//                        stepExecution.getExecutionContext().putString("Sqoop Result", strResult);
//                        stepExecution.getExecutionContext().putString("Sqoop Error", strError);
                        if (exitValue != 0) {
                            throw new RuntimeException(strError);
                        }
                    } else {
                        log.error("Execute sqoop ERROR!");
                        throw new RuntimeException("Sqoop result is null!");
                    }

                } catch (Exception e) {
                    log.error("Export data ERROR!", e);
                    log.error("Export data ERROR!", e.getCause());
                    throw e;
                } finally {
                    // 删除临时表
                    dropTempTable(dwConnectUrl, dwDbName, dwUserName, dropTempTableSql, projectName);
                }
                return RepeatStatus.FINISHED;
            }

            private String combineTypeAndLength(String columnTypeName, int precision, int scale) {
                if ("varchar".equalsIgnoreCase(columnTypeName) || "char".equalsIgnoreCase(columnTypeName)) {
                    return String.format("%s (%d)", columnTypeName, precision);
                } else if ("decimal".equalsIgnoreCase(columnTypeName)) {
                    return String.format("%s (%d,%d)", columnTypeName, precision, scale);
                } else {
                    return columnTypeName;
                }
            }
        }.retry(retryCount)).build();
    }

    private void dropTempTable(String dwConnectUrl, String dwDbName, String dwUserName,
                               String dropTempTableSql, String projectName) throws SQLException {
        boolean f = HiveClient.getInstance().executeHiveSqlNoRes(dwConnectUrl, dwUserName, projectName, "",dwDbName,"",dropTempTableSql, null);
        if(!f) throw new SQLException(String.format("execute SQL Error. SQL:%s", dropTempTableSql));
    }

    private String getDropTempTableSql(String tempTableFullName, StepExecution stepExecution) {
        String dropTempTableSql = "DROP TABLE IF EXISTS " + tempTableFullName;
        stepExecution.getExecutionContext().putString("Drop temp table SQL:", dropTempTableSql);
        return dropTempTableSql;
    }

}
