package cn.rongcapital.chorus.modules.import_data;

import cn.rongcapital.chorus.modules.utils.retry.LineageTasklet;
import cn.rongcapital.chorus.modules.utils.retry.listener.LineageStepListener;
import cn.rongcapital.log.JobListenerForLog;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.gson.internal.LinkedTreeMap;
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

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by alan on 24/04/2017.
 */
@Slf4j
@org.springframework.context.annotation.Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = {"cn.rongcapital.chorus.modules.import_data","cn.rongcapital.chorus.modules.utils.retry"})
public class ImportDataJob {

    private static final String P_DATE_PARTITION_KEY = "p_date";

    private static final String DRIVER_CLASS_NAME_MYSQL = "com.mysql.jdbc.Driver";

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
    public Job job(@Qualifier("importDataStep1") Step step1,
                   @Qualifier("logListener") JobExecutionListener logListener) {
        return jobs.get("importDataJob").listener(logListener).start(step1).build();
    }

    @Bean
    public Step importDataStep1(
            @Value("${projectId}") String projectId,
            @Value("${dwConnectUrl}") String dwConnectUrl,
            @Value("${dwDbName}") String dwDbName,
            @Value("${dwTableName}") String dwTableName,
            @Value("${dwUserName}") String dwUserName,
            @Value("${dwLocation}") String dwLocation,
            @Value("${rdbName}") String rdbName,
            @Value("${rdbConnectUrl}") String rdbConnectUrl,
            @Value("${rdbUserName}") String rdbUserName,
            @Value("${rdbPassword}") String rdbPassword,
            @Value("${rdbTable:}") String rdbTable,
            @Value("${where:}") String where,
            @Value("${columnNameMapStr}") String columnNameMapStr,
            @Value("${partitionMapStr}") String partitionMapStr,
            @Value("${retryCount:}") Integer retryCount,
            @Value("${dataCoverStrategy}") String dataCoverStrategy,
            @Value("${importStrategy}") String importStrategy,
            @Value("${dataTableType}") String dataTableType,
            @Value("${rdbTableDynamic:}") String rdbTableDynamic,
            @Qualifier("lineageStepListener")LineageStepListener lineageStepListener
    ) {
        return steps.get("importDataStep1").listener(lineageStepListener).tasklet(new LineageTasklet() {
            @Override
            public RepeatStatus executeWithLineage(ChunkContext chunkContext, StepContribution contribution) throws Exception {

                StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
                JobParameters jobParameters = stepExecution.getJobParameters();
                log.info("---projectId: {}", projectId);
                log.info("---dwConnectUrl: {}", dwConnectUrl);
                log.info("---dwDbName: {}", dwDbName);
                log.info("---dwTableName: {}", dwTableName);
                log.info("---dwUserName: {}", dwUserName);
                log.info("---dwLocation: {}", dwLocation);
                log.info("---rdbName: {}", rdbName);
                log.info("---rdbConnectUrl: {}", rdbConnectUrl);
                log.info("---rdbUserName: {}", rdbUserName);
                log.info("---rdbTable: {}", rdbTable);
                log.info("---where: {}", where);
                log.info("---columnNameMapStr: {}", columnNameMapStr);
                log.info("---partitionMapStr: {}", partitionMapStr);
                log.info("---jobParameters: {}", jobParameters);
                log.info("---retryCount: {}", retryCount);
                log.info("---dataCoverStrategy: {}", dataCoverStrategy);
                log.info("---importStrategy: {}", importStrategy);
                log.info("---dataTableType: {}", dataTableType);
                log.info("---rdbTableDynamic: {}", rdbTableDynamic);

                stepExecution.getExecutionContext().put("projectId", projectId);
                // job初始化参数
//                Map<String, String> target2TempColumnMap = JSON.parseObject(
//                        columnNameMapStr)
//                        .entrySet().stream()
//                        .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
                Map<String, String> target2TempColumnMap = new HashMap<String, String>();
                JSON.parseObject(columnNameMapStr).entrySet().forEach(
                        e -> {
                            String tempColumn = String.valueOf(e.getValue());
                            String targetColumn = e.getKey().toLowerCase();
                            target2TempColumnMap.put(targetColumn, tempColumn);
                        }
                );

                LinkedTreeMap<String, Object> partitionMap = new LinkedTreeMap<String, Object>();
                Set<String> partitionSet = new TreeSet<>();
                JSON.parseObject(partitionMapStr).entrySet().forEach(
                        e -> {
                            partitionMap.put(e.getKey().toLowerCase(), e.getValue());
                            if (StringUtils.isNotBlank(String.valueOf(e.getValue()))) {
                                partitionSet.add(e.getKey().toLowerCase());
                            }
                        }
                );

                // job parameter获取
//                String jpPartitionMapStr = jobParameters.getString("partitionMapStr");
//                Map<String, Object> jpPartitionMap = JSON.parseObject(jpPartitionMapStr);
//                jpPartitionMap = jpPartitionMap == null ? new HashMap<>() : jpPartitionMap;

                // 变量准备
                String targetTableFullName = dwDbName + "." + dwTableName;
                String tempTableName = dwTableName + "_" + String.valueOf(System.currentTimeMillis());
                String tempTableFullName = dwDbName + "." + tempTableName;
                String dropTempTableSql = getDropTempTableSql(tempTableFullName, stepExecution);
                String pDateValue = String.valueOf(partitionMap.getOrDefault(P_DATE_PARTITION_KEY,
                        "$executionDate"));
                String executionDate = jobParameters.getString("$executionDate");

                if (StringUtils.isBlank(executionDate)) {
                    executionDate = new DateTime().toString("yyyy-MM-dd");
                }
                final String finalPDateValue = pDateValue.equals("$executionDate") ?
                        executionDate : pDateValue;
                log.info("$executionDate: {}", finalPDateValue);

                String projectName = dwDbName.substring(dwDbName.indexOf("_") + 1);

                try {
                    // 0.拼接临时表列信息SQL, 不考虑进行分区.
                    String getMetadataFromTargetTableSql = String.format(" SELECT * from %s LIMIT 1 ", targetTableFullName);
                    stepExecution.getExecutionContext()
                            .putString("Get MetaData from target table SQL:", getMetadataFromTargetTableSql);
                    String tempTableColumnDefStr;
//                    ResultSet resultSet = HiveConnector.executeHiveSql(dwConnectUrl, dwDbName, dwUserName,
//                            getMetadataFromTargetTableSql, dropTempTableSql, projectName);
                    Map<String, String> targetTableColumnMap = HiveClient.getInstance().executeHiveSql(dwConnectUrl, dwUserName, projectName, "", dwDbName, "",
                            getMetadataFromTargetTableSql, dropTempTableSql, new HiveClient.ResultSetConsumer<Map<String, String>>() {
                        @Override
                        public Map<String, String> apply(ResultSet rs) throws SQLException {
                            Map<String, String> targetTableColumnMap_0 = new LinkedHashMap<>();
                            ResultSetMetaData targetTableMetadata = rs.getMetaData();
                            for (int i = 1; i <= targetTableMetadata.getColumnCount(); i++) {
                                String columnLabel = targetTableMetadata.getColumnLabel(i).split("\\.")[1];
                                if (!partitionSet.contains(columnLabel)) {
                                    String type = combineTypeAndLength(
                                            targetTableMetadata.getColumnTypeName(i),
                                            targetTableMetadata.getPrecision(i),
                                            targetTableMetadata.getScale(i));
                                    targetTableColumnMap_0.put(columnLabel, type);
                                }
                            }
                            return targetTableColumnMap_0;
                        }
                    });

                    if(targetTableColumnMap == null) throw new RuntimeException("query target table column error.");

                    log.info("targetTableColumnMap :{}", targetTableColumnMap);
                    Map<String, String> tempTableColumnNameTypeMap = new LinkedHashMap<String, String>();
                    targetTableColumnMap.entrySet().stream()
                            .filter(e -> target2TempColumnMap.containsKey(e.getKey()))
                            .forEach(e -> {
                                String tempColumn = target2TempColumnMap.get(e.getKey());
                                if (tempTableColumnNameTypeMap.containsKey(tempColumn)) {
                                    if (!tempTableColumnNameTypeMap.get(tempColumn).equalsIgnoreCase(e.getValue())) {
                                        throw new RuntimeException("column " + tempColumn + "should not use as different data type");
                                    }
                                } else {
                                    tempTableColumnNameTypeMap.put(tempColumn, e.getValue());
                                }
                            });
                    Set<String> temTableColumnNameSet = new TreeSet<String>();
                    tempTableColumnDefStr = tempTableColumnNameTypeMap.entrySet().stream()
                            .map(e -> e.getKey() + " " + e.getValue())
                            .collect(Collectors.joining(","));
                    target2TempColumnMap.entrySet().forEach(
                            e -> {
                                temTableColumnNameSet.add(e.getValue().toLowerCase());
                            }
                    );
                    log.info("Temp table column def: {}", tempTableColumnDefStr);
                    stepExecution.getExecutionContext().putString("Target table's columns", tempTableColumnDefStr);

                    // 1.创建临时表
                    String createTempTableSql = String.format(" CREATE TABLE %s ( %s )  STORED AS TEXTFILE LOCATION '%s'",
                            tempTableFullName, tempTableColumnDefStr,
                            dwLocation + tempTableName);
                    log.info("Temp table creation sql: {}", createTempTableSql);
                    stepExecution.getExecutionContext().putString("Create temp table SQL:", createTempTableSql);
                    boolean f = HiveClient.getInstance().executeHiveSqlNoRes(dwConnectUrl, dwUserName, projectName,"",dwDbName,"",createTempTableSql,dropTempTableSql);
                    if(!f)throw new RuntimeException("create temp table error.");
                    //                    HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName,
//                            createTempTableSql, dropTempTableSql, projectName);

                    // 2.Sqoop导RDB数据到临时表
                    String tempTableColumns = String.join(",", temTableColumnNameSet);
                    String finalWhere = "";
                    if (ImportStrategy.SQL.name().equals(importStrategy) && StringUtils.isNotBlank(where)) {
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
                        finalWhere = where;
                        finalWhere = finalWhere.replaceAll("\\$whereToday", today);
                        finalWhere = finalWhere.replaceAll("\\$whereYesterday", yesterday);
                        if (!StringUtils.isBlank(var1)) finalWhere = finalWhere.replaceAll("\\$whereVar1", var1);
                        if (!StringUtils.isBlank(var2)) finalWhere = finalWhere.replaceAll("\\$whereVar2", var2);
                        if (!StringUtils.isBlank(var3)) finalWhere = finalWhere.replaceAll("\\$whereVar3", var3);
                        if (!StringUtils.isBlank(var4)) finalWhere = finalWhere.replaceAll("\\$whereVar4", var4);
                        if (!StringUtils.isBlank(var5)) finalWhere = finalWhere.replaceAll("\\$whereVar5", var5);
                    }

                    List<String> rdbTableList = Lists.newArrayList();
                    // 动态表名
                    if (DataTableType.DYNAMIC.name().equals(dataTableType)) {
                        String systemDate = jobParameters.getString("$systemDate");
                        if (StringUtils.isBlank(systemDate)) {
                            log.debug("default system date");
                            systemDate = new DateTime().toString("yyyyMMdd");
                        }
                        String finalRdbTableDynamic = rdbTableDynamic.replaceAll("\\$systemDate", systemDate);

                        // 根据动态表名从元数据表中查询符合条件的表,暂时只支持mysql
                        if (rdbConnectUrl.startsWith("jdbc:mysql")) {
                            Connection conn = null;
                            Statement st = null;
                            ResultSet rs = null;
                            try {
                                String rdbDbName = rdbConnectUrl.substring(rdbConnectUrl.lastIndexOf("/") + 1, rdbConnectUrl.length());
                                String sql = "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME LIKE '%s'";
                                conn = JdbcConnector.getConnection(DRIVER_CLASS_NAME_MYSQL, rdbConnectUrl + "?useUnicode=true&characterEncoding=utf-8&useSSL=false", rdbUserName, rdbPassword);
                                st = conn.createStatement();
                                sql = String.format(sql, rdbDbName, finalRdbTableDynamic + "%");
                                log.info("sql : {}", sql);
                                rs = st.executeQuery(sql);
                                while (rs.next()) {
                                    String tableName = rs.getString("TABLE_NAME");
                                    rdbTableList.add(tableName);
                                }
                            } catch (Exception ex) {
                                throw ex;
                            } finally {
                                if (rs != null) {
                                    try {
                                        rs.close();
                                    } catch (SQLException e) {
                                    }
                                }
                                if (st != null) {
                                    try {
                                        st.close();
                                    } catch (SQLException e) {
                                    }
                                }
                                if (conn != null) {
                                    try {
                                        conn.close();
                                    } catch (SQLException e) {
                                    }
                                }
                            }
                        }
                    } else { // 静态表名
                        rdbTableList.add(rdbTable);
                    }
                    stepExecution.getExecutionContext().put("rdbTableList", rdbTableList);

                    log.info("import rdb tables :{}", rdbTableList.size());

                    for (String rdbTableName : rdbTableList) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("sqoop import")
                                .append(" -Dmapreduce.job.queuename=").append(projectName)
                                .append(" -Dtez.queue.name=").append(projectName);
                        String finalRdbConnectUrl = rdbConnectUrl.startsWith("jdbc:mysql") ?
                                rdbConnectUrl + "?useUnicode=true&characterEncoding=utf-8" : rdbConnectUrl;
                        sb.append(" --connect '").append(finalRdbConnectUrl).append("'");
                        sb.append(" --username '").append(rdbUserName).append("'");
                        sb.append(" --password '").append(rdbPassword).append("'");
                        sb.append(" --table '").append(rdbTableName).append("'");
                        sb.append(" --hcatalog-database '").append(dwDbName).append("'");
                        sb.append(" --hcatalog-table '").append(tempTableName).append("'");
                        sb.append(" --delete-target-dir");
                        sb.append(" --hive-drop-import-delims");
                        sb.append(" --columns '").append(tempTableColumns).append("'");
                        if (StringUtils.isNotBlank(finalWhere)) {
                            sb.append(" --where '").append(finalWhere).append("'");
                        }
                        sb.append(" --m 1");

                        String sqoopCommand = sb.toString();
                        log.debug("Sqoop import data sqoopCommand: {}", sqoopCommand);
                        stepExecution.getExecutionContext().putString("Sqoop Command", sqoopCommand);
                        LocalShellTool local = new LocalShellTool();
                        Map<String, String> sqoopResult = local.exec(sqoopCommand);
                        log.info("Sqoop import data sqoopResult: {}", sqoopResult);
                        if (sqoopResult != null) {
                            String strResult = sqoopResult.get(LocalShellTool.RESULT_KEY);
                            String strError = sqoopResult.get(LocalShellTool.ERROR_KEY);
                            int exitValue = Integer.parseInt(sqoopResult.get(LocalShellTool.EXITVALUE_KEY));
                            stepExecution.getExecutionContext().putString("Sqoop ExitValue", String.valueOf(exitValue));
//                        stepExecution.getExecutionContext().putString("Sqoop Result", strResult);
//                        stepExecution.getExecutionContext().putString("Sqoop Error", strError);
                            if (exitValue != 0) {
                                dropTempTable(dwConnectUrl, dwDbName, dwUserName, dropTempTableSql, projectName);
                                throw new RuntimeException(strError);
                            }
                        } else {
                            dropTempTable(dwConnectUrl, dwDbName, dwUserName, dropTempTableSql, projectName);
                            log.error("Execute sqoop ERROR!");
                            throw new RuntimeException("Sqoop result is null!");
                        }
                    }

                    // 3.Drop原p_date分区
//                    String dropPDatePartitionSql = String.format("alter table %s drop partition(p_date='%s')",
//                            targetTableFullName, finalPDateValue);
//                    log.info("Drop origin p_date partition sql: {}", dropPDatePartitionSql);
//                    HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName,
//                            dropPDatePartitionSql, dropTempTableSql, projectName);

                    // 4.拼接partition信息供临时表导入目标表使用
                    log.info("partitionMap:{}", partitionMap);
                    Set<String> staticPartitionColumnSet = new TreeSet<String>();
                    staticPartitionColumnSet.add(P_DATE_PARTITION_KEY);
                    String partitionSql = partitionMap.keySet().stream()
                            .map(c -> {
                                if (c.equalsIgnoreCase(P_DATE_PARTITION_KEY))
                                    return String.format("%s='%s'", P_DATE_PARTITION_KEY, finalPDateValue);
                                else if (partitionMap.containsKey("#" + c))
                                    // 动态分区支持
                                    return c;
                                else if (StringUtils.isBlank(String.valueOf(partitionMap.get(c)))) {
                                    return c;
                                } else {
                                    staticPartitionColumnSet.add(c.toLowerCase());
                                    return String.format("%s='%s'",
                                            c, StringUtils.trimToEmpty(String.valueOf(partitionMap.get(c))));
                                }
                            }).collect(Collectors.joining(","));

                    // 5.临时表导入目标表
                    String overwrite;
                    if (DataCoverStrategy.PARTITION_COVER.name().equals(dataCoverStrategy)) {
                        overwrite = "OVERWRITE";
                    } else {
                        overwrite = "INTO";
                    }
                    log.info("overwrite : {}", dataCoverStrategy);
                    String selectColumns = targetTableColumnMap.keySet().stream()
                            .map(k -> {
                                if ((!target2TempColumnMap.containsKey(k.toLowerCase())) && (!staticPartitionColumnSet.contains(k.toLowerCase()))) {
                                    return " null as " + k;
                                } else {
                                    return target2TempColumnMap.get(k) + " as " + k;
                                }
                            })
                            .collect(Collectors.joining(","));
                    String copyFromTempTableSql = String.format(
                            "INSERT %s TABLE %s PARTITION ( %s ) SELECT %s FROM %s ",
                            overwrite, targetTableFullName, StringUtils.trimToEmpty(partitionSql),
                            selectColumns, tempTableFullName);
                    log.info("Copy from temp table sql: {}", copyFromTempTableSql);
                    stepExecution.getExecutionContext().putString("Copy temp table data SQL:", copyFromTempTableSql);
                    f = HiveClient.getInstance().executeHiveSqlNoRes(dwConnectUrl, dwUserName, projectName, "",dwDbName,"",copyFromTempTableSql,dropTempTableSql);
                    if(!f)throw new RuntimeException("copy temp table to real table error.");
//                    HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName,
//                            copyFromTempTableSql, dropTempTableSql, projectName);

                    // 删除临时表
                    dropTempTable(dwConnectUrl, dwDbName, dwUserName, dropTempTableSql, projectName);
                } catch (Exception e) {
                    log.error("Import data ERROR!", e);
                    log.error("Import data ERROR!", e.getCause());
                    throw e;
                }
                return RepeatStatus.FINISHED;
            }

            @Override
            protected Vertex inputs(ChunkContext chunkContext) {
                ExternalTableVertex externalTableVertex = new ExternalTableVertex();
                StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();

                List<String> rdbTableList = (List<String>) stepExecution.getExecutionContext().get("rdbTableList");
                rdbTableList.forEach(t -> externalTableVertex.add(projectId, rdbName, rdbUserName, rdbConnectUrl, t));

                return externalTableVertex;
            }

            @Override
            protected Vertex outputs(ChunkContext chunkContext) {
                InternalTableVertex internalTableVertex = new InternalTableVertex();
                internalTableVertex.add(dwDbName, dwTableName);
                return internalTableVertex;
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
        boolean exp = HiveClient.getInstance().executeHiveSqlNoRes(dwConnectUrl, dwUserName, projectName, "", dwDbName,"", dropTempTableSql,"");
        if(!exp)throw new SQLException(String.format("execute SQL Exception. SQL:%s", dropTempTableSql));
        //        HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName,
//                dropTempTableSql, null, projectName);
    }

    private String getDropTempTableSql(String tempTableFullName, StepExecution stepExecution) {
        String dropTempTableSql = "DROP TABLE IF EXISTS " + tempTableFullName;
        stepExecution.getExecutionContext().putString("Drop temp table SQL:", dropTempTableSql);
        return dropTempTableSql;
    }

}
