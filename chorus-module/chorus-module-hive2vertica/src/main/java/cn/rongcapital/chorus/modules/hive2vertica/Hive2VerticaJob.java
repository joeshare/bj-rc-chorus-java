package cn.rongcapital.chorus.modules.hive2vertica;

import cn.rongcapital.chorus.modules.utils.retry.SimpleTasklet;
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
 * Created by Athletics on 2017/8/22.
 */
@Slf4j
@org.springframework.context.annotation.Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "cn.rongcapital.chorus.modules.hive2vertica")
public class Hive2VerticaJob {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    public JobExecutionListener logListener() {
        return new JobListenerForLog();
    }

    @Bean
    public Job job(@Qualifier("exportDataStep1") Step exportDataStep1,
                   @Qualifier("logListener") JobExecutionListener logListener) {
        return jobs.get("exportDataJob").listener(logListener).start(exportDataStep1).build();
    }

    @Bean
    public Step exportDataStep1(
            @Value("$projectId") String projectId,
            @Value("${dwConnectUrl}") String dwConnectUrl,
            @Value("${dwDbName}") String dwDbName,
            @Value("${dwTableName}") String dwTableName,
            @Value("${dwUserName}") String dwUserName,
            @Value("${verticaConnectUrl}") String verticaConnectUrl,
            @Value("${verticaUserName}") String verticaUserName,
            @Value("${verticaPassword}") String verticaPassword,
            @Value("${verticaTable}") String verticaTable,
            @Value("${where:}") String where,
            @Value("${columnNameMapStr}") String columnNameMapStr,
            @Value("${retryCount:}") Integer retryCount
    ) {
        return steps.get("exportDataStep1").tasklet(new SimpleTasklet() {

            @Override
            public RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) throws Exception {
                StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
                JobParameters jobParameters = stepExecution.getJobParameters();

                log.info("---projectId: {}",projectId);
                log.info("---dwTableName: {}", dwTableName);
                log.info("---verticaConnectUrl: {}", verticaConnectUrl);
                log.info("---verticaUserName: {}", verticaUserName);
                log.info("---verticaPassword: {}", verticaPassword);
                log.info("---verticaTable: {}", verticaTable);
                log.info("---where: {}", where);
                log.info("---columnNameMapStr: {}", columnNameMapStr);
                log.info("---jobParameters: {}", jobParameters);
                log.info("---retryCount: {}", retryCount);

                // job初始化参数
                Map<String, String> source2TempColumnMap = JSON.parseObject(
                        columnNameMapStr)
                        .entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));

                // 变量准备
                Map<String, String> paramMap = getParams(stepExecution);

                try {
                    // 0.拼接临时表列信息SQL, 不考虑进行分区.
                    Map<String, String> sourceTableColumnMap = new LinkedHashMap<>();
                    String tempTableColumnDefStr = getTableColumns(paramMap,stepExecution,source2TempColumnMap,sourceTableColumnMap);
                    log.info("Temp table column def: {}", paramMap.get("tempTableColumnDefStr"));
                    stepExecution.getExecutionContext().putString("Source table's columns", tempTableColumnDefStr);

                    // 1.创建临时表
                    createTempTable(paramMap, stepExecution, tempTableColumnDefStr);

                    // 2.目标表数据导入临时表
                    insertTempTableData(jobParameters,stepExecution,paramMap,sourceTableColumnMap,source2TempColumnMap);

                    // 3.Sqoop导临时表数据到vertica
                    executeSqoopStr(stepExecution,paramMap,sourceTableColumnMap,source2TempColumnMap);

                    // 删除临时表
                    dropTempTable(dwConnectUrl, dwDbName, dwUserName, paramMap.get("dropTempTableSql"), paramMap.get("projectName"));
                } catch (Exception e) {
                    log.error("Export data ERROR!", e);
                    log.error("Export data ERROR!", e.getCause());
                    throw e;
                }
                return RepeatStatus.FINISHED;
            }

            private void executeSqoopStr(StepExecution stepExecution,Map<String, String> paramMap,Map<String, String> sourceTableColumnMap,Map<String, String> source2TempColumnMap) throws Exception{
                String verticaTableColumns = sourceTableColumnMap.entrySet().stream()
                        .filter(e -> source2TempColumnMap.containsKey(e.getKey()))
                        .map(e -> source2TempColumnMap.get(e.getKey()))
                        .collect(Collectors.joining(","));
                StringBuilder sb = new StringBuilder();
                sb.append("sqoop export")
                        .append(" --batch")
                        .append(" --connection-manager 'org.apache.sqoop.manager.GenericJdbcManager'")
                        .append(" --driver 'com.vertica.jdbc.Driver'");
                String finalVerticaConnectUrl = verticaConnectUrl.startsWith("jdbc:vertica") ? verticaConnectUrl + "?useUnicode=true&characterEncoding=utf-8" : verticaConnectUrl;
                sb.append(" --connect '").append(finalVerticaConnectUrl).append("'");
                sb.append(" --username '").append(verticaUserName).append("'");
                sb.append(" --password '").append(verticaPassword).append("'");
                sb.append(" --table '").append(verticaTable).append("'");
                sb.append(" --hcatalog-database '").append(dwDbName).append("'");
                sb.append(" --hcatalog-table '").append(paramMap.get("tempTableName")).append("'");
                sb.append(" --columns '").append(verticaTableColumns).append("'");
                sb.append(" --input-null-string '\\\\N'");
                sb.append(" --input-null-non-string '\\\\N'");
                sb.append(" --fields-terminated-by '\\t'");
                sb.append(" --m 1");

                String sqoopCommand = sb.toString();
                log.info("Sqoop export data sqoopCommand: {}", sqoopCommand);
                stepExecution.getExecutionContext().putString("Sqoop Command", sqoopCommand);
                LocalShellTool local = new LocalShellTool();
                Map<String, String> sqoopResult = local.exec(sqoopCommand);
                log.info("Sqoop export data sqoopResult: {}", sqoopResult);
                if (sqoopResult != null) {
                    String strError = sqoopResult.get(LocalShellTool.ERROR_KEY);
                    int exitValue = Integer.parseInt(sqoopResult.get(LocalShellTool.EXITVALUE_KEY));
                    stepExecution.getExecutionContext().putString("Sqoop ExitValue", String.valueOf(exitValue));
                    if (exitValue != 0) {
                        dropTempTable(dwConnectUrl, dwDbName, dwUserName, paramMap.get("dropTempTableSql"), paramMap.get("projectName"));
                        throw new RuntimeException(strError);
                    }
                } else {
                    dropTempTable(dwConnectUrl, dwDbName, dwUserName, paramMap.get("dropTempTableSql"), paramMap.get("projectName"));
                    log.error("Execute sqoop ERROR!");
                    throw new RuntimeException("Sqoop result is null!");
                }
            }

            private void createTempTable(Map<String, String> paramMap,StepExecution stepExecution,String tempTableColumnDefStr) throws Exception{
                String createTempTableSql = String.format(" CREATE TABLE %s ( %s )  STORED AS TEXTFILE ",
                        paramMap.get("tempTableFullName"), tempTableColumnDefStr);
                log.info("Temp table creation sql: {}", createTempTableSql);
                stepExecution.getExecutionContext().putString("Create temp table SQL:", createTempTableSql);
//                HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName,
//                        createTempTableSql, paramMap.get("dropTempTableSql"), paramMap.get("projectName"));

                boolean f = HiveClient.getInstance().executeHiveSqlNoRes(dwConnectUrl, dwUserName, paramMap.get("projectName"),
                        "",dwDbName,"",createTempTableSql,paramMap.get("dropTempTableSql"));
                if(!f) throw new SQLException(String.format("execute SQL Error. SQL:", createTempTableSql));
            }

            private void insertTempTableData(JobParameters jobParameters,StepExecution stepExecution,Map<String, String> paramMap,Map<String, String> sourceTableColumnMap,Map<String, String> source2TempColumnMap) throws Exception{
                String sourceTableColumns = sourceTableColumnMap.entrySet().stream()
                        .filter(e -> source2TempColumnMap.containsKey(e.getKey()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.joining(","));
                String copyToTempTableSql = String.format(
                        "INSERT OVERWRITE TABLE %s SELECT %s FROM %s ",
                        paramMap.get("tempTableFullName"), sourceTableColumns, paramMap.get("sourceTableFullName"));
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
//                HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName,
//                        copyToTempTableSql, paramMap.get("dropTempTableSql"), paramMap.get("projectName"));

                boolean f = HiveClient.getInstance().executeHiveSqlNoRes(dwConnectUrl, dwUserName, paramMap.get("projectName"),
                        "", dwDbName, "", copyToTempTableSql, paramMap.get("dropTempTableSql"));

                if(!f)throw new SQLException(String.format("execute SQL Error. SQL:", copyToTempTableSql));
            }

            private Map<String, String> getParams(StepExecution stepExecution){
                Map<String, String> map = new HashMap<>();
                String sourceTableFullName = dwDbName + "." + dwTableName;
                String tempTableName = dwTableName + "_" + String.valueOf(System.currentTimeMillis());
                String tempTableFullName = dwDbName + "." + tempTableName;
                String dropTempTableSql = getDropTempTableSql(tempTableFullName, stepExecution);
                String projectName = dwDbName.substring(dwDbName.indexOf("_") + 1);
                map.put("sourceTableFullName",sourceTableFullName);
                map.put("tempTableName",tempTableName);
                map.put("tempTableFullName", tempTableFullName);
                map.put("dropTempTableSql", dropTempTableSql);
                map.put("projectName", projectName);
                return map;
            }

            private String getTableColumns(Map<String, String> paramMap,StepExecution stepExecution,Map<String, String> source2TempColumnMap,Map<String, String> sourceTableColumnMap) throws Exception{
                String getMetadataFromSourceTableSql = String.format(" SELECT * from %s LIMIT 1 ", paramMap.get("sourceTableFullName"));
                stepExecution.getExecutionContext()
                        .putString("Get MetaData from source table SQL:", getMetadataFromSourceTableSql);

                Map<String, String> rsMap = HiveClient.getInstance().executeHiveSql(dwConnectUrl, dwUserName, paramMap.get("projectName"), "", dwDbName, "",
                        getMetadataFromSourceTableSql, paramMap.get("dropTempTableSql"), new HiveClient.ResultSetConsumer<Map<String, String>>() {
                            @Override
                            public Map<String, String> apply(ResultSet rs) throws SQLException {
                                Map<String, String> map = new HashMap<>();
                                ResultSetMetaData sourceTableMetadata = rs.getMetaData();
                                for (int i = 1; i <= sourceTableMetadata.getColumnCount(); i++) {
                                    String columnLabel = sourceTableMetadata.getColumnLabel(i).split("\\.")[1];
                                    String type = combineTypeAndLength(
                                            sourceTableMetadata.getColumnTypeName(i),
                                            sourceTableMetadata.getPrecision(i),
                                            sourceTableMetadata.getScale(i));
                                    map.put(columnLabel, type);
                                }

                                return map;
                            }
                        });

                if(rsMap == null) throw new SQLException(String.format("query MetaData from source table error. table:%s",paramMap.get("sourceTableFullName")));
                sourceTableColumnMap.putAll(rsMap);

// ResultSet resultSet = HiveConnector.executeHiveSql(dwConnectUrl, dwDbName, dwUserName,
//                        getMetadataFromSourceTableSql, paramMap.get("dropTempTableSql"), paramMap.get("projectName"));
//
//                ResultSetMetaData sourceTableMetadata = resultSet.getMetaData();
//                for (int i = 1; i <= sourceTableMetadata.getColumnCount(); i++) {
//                    String columnLabel = sourceTableMetadata.getColumnLabel(i).split("\\.")[1];
//                    String type = combineTypeAndLength(
//                            sourceTableMetadata.getColumnTypeName(i),
//                            sourceTableMetadata.getPrecision(i),
//                            sourceTableMetadata.getScale(i));
//                    sourceTableColumnMap.put(columnLabel, type);
//                }
                return sourceTableColumnMap.entrySet().stream()
                        .filter(e -> source2TempColumnMap.containsKey(e.getKey()))
                        .map(e -> source2TempColumnMap.get(e.getKey()) + " " + e.getValue())
                        .collect(Collectors.joining(","));
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
//        HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName,
//                dropTempTableSql, null, projectName);

        boolean f = HiveClient.getInstance().executeHiveSqlNoRes(dwConnectUrl,dwUserName,projectName,"",dwDbName,"",dropTempTableSql,null);
        if(!f) throw new SQLException(String.format("execute SQL Error. SQL:", dropTempTableSql));
    }

    private String getDropTempTableSql(String tempTableFullName, StepExecution stepExecution) {
        String dropTempTableSql = "DROP TABLE IF EXISTS " + tempTableFullName;
        stepExecution.getExecutionContext().putString("Drop temp table SQL:", dropTempTableSql);
        return dropTempTableSql;
    }
}
