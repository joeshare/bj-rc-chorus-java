package cn.rongcapital.chorus.modules.csv2hive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.internal.LinkedTreeMap;
import cn.rongcapital.chorus.hive.jdbc.HiveClient;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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

import com.alibaba.fastjson.JSON;

import cn.rongcapital.chorus.modules.utils.retry.SimpleTasklet;
import cn.rongcapital.log.JobListenerForLog;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by maboxiao on 08/08/2017.
 */
@Slf4j
@org.springframework.context.annotation.Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "cn.rongcapital.chorus.modules.csv2hive")
public class CSV2HiveJob {

    private static final String P_DATE_PARTITION_KEY = "p_date";

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    public JobExecutionListener logListener() {
        return new JobListenerForLog();
    }

    @Bean
    public Job job(@Qualifier("csv2HiveStep1") Step exportDataStep1,
                   @Qualifier("logListener") JobExecutionListener logListener) {
        return jobs.get("csv2HiveJob").listener(logListener).start(exportDataStep1).build();
    }

    @Bean
    public Step csv2HiveStep1(
            @Value("${csvFilePath}") String csvFilePath,
            @Value("${hasTitle}") String hasTitle,
            @Value("${dwConnectUrl}") String dwConnectUrl,
            @Value("${dwDbName}") String dwDbName,
            @Value("${dwTableName}") String dwTableName,
            @Value("${dwUserName}") String dwUserName,
            @Value("${dwLocation}") String dwLocation,
            @Value("${dataCoverStrategy}") String dataCoverStrategy,
            @Value("${columnNameMapStr}") String columnNameMapStr,
            @Value("${partitionMapStr}") String partitionMapStr,
            @Value("${retryCount:}") Integer retryCount
    ) {
        return steps.get("csv2HiveStep1").tasklet(new SimpleTasklet() {
            @Override
            public RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) throws Exception {

                StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
                JobParameters jobParameters = stepExecution.getJobParameters();

                log.info("---csvFilePath: {}", csvFilePath);
                log.info("---dwConnectUrl: {}", dwConnectUrl);
                log.info("---dwDbName: {}", dwDbName);
                log.info("---dwTableName: {}", dwTableName);
                log.info("---dwUserName: {}", dwUserName);
                log.info("---dwLocation: {}", dwLocation);
                log.info("---hasTitle: {}", hasTitle);
                log.info("---dataCoverStrategy: {}", dataCoverStrategy);
                log.info("---columnNameMapStr: {}", columnNameMapStr);
                log.info("---partitionMapStr: {}", partitionMapStr);
                log.info("---jobParameters: {}", jobParameters);
                log.info("---retryCount: {}", retryCount);

                // job初始化参数
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
    
                // 变量准备
                String targetTableFullName = dwDbName + "." + dwTableName;
                String tempTableName = dwTableName + "_" + String.valueOf(System.currentTimeMillis());
                String tempTableFullName = dwDbName + "." + tempTableName;
                String dropTempTableSql = getDropTempTableSql(tempTableFullName, stepExecution);
                String projectName = dwDbName.substring(dwDbName.indexOf("_") + 1);
                String csvFileName = csvFilePath.substring(csvFilePath.lastIndexOf("/") + 1, csvFilePath.length());
                String tempTableFile = dwLocation + tempTableName + "/" + csvFileName;
                String pDateValue = String.valueOf(partitionMap.getOrDefault(P_DATE_PARTITION_KEY,
                        "$executionDate"));
                String executionDate = jobParameters.getString("$executionDate");

                if (StringUtils.isBlank(executionDate)) {
                    executionDate = new DateTime().toString("yyyy-MM-dd");
                }
                final String finalPDateValue = pDateValue.equals("$executionDate") ?
                        executionDate : pDateValue;
                log.info("$executionDate: {}", finalPDateValue);

                String systemDate = jobParameters.getString("$systemDate");

                if (StringUtils.isBlank(systemDate)) {
                    systemDate = new DateTime().toString("yyyy-MM-dd");
                }

                final String finalCsvFilePath = csvFilePath.replaceAll("\\$systemDate", systemDate);

                log.info("finalCsvFilePath: {}", finalPDateValue);


                // 从HDFS上读取CSV文件，分析表头和列数
                FSDataInputStream fsr;
                BufferedReader bufferedReader = null;
                String lineTxt;
                String firstLine = "";
                FileSystem fs;
                try {
                    Configuration conf = new Configuration();
                    fs = FileSystem.get(conf);

                    fsr = fs.open(new Path(finalCsvFilePath));
                    bufferedReader = new BufferedReader(new InputStreamReader(fsr));

                    // 读取第一行
                    while ((lineTxt = bufferedReader.readLine()) != null) {
                        firstLine = lineTxt;
                        break;
                    }
                } catch (Exception e) {
                    throw e;
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                        }
                    }
                }

                stepExecution.getExecutionContext().putString("First Line", String.valueOf(firstLine));

                try {

                    // 0.拼接临时表列信息SQL, 不考虑进行分区.
                    String getMetadataFromTargetTableSql = String.format(" SELECT * from %s LIMIT 1 ", targetTableFullName);
                    stepExecution.getExecutionContext()
                            .putString("Get MetaData from target table SQL:", getMetadataFromTargetTableSql);

                    Map<String, String> targetTableColumnMap = HiveClient.getInstance().executeHiveSql(dwConnectUrl, dwUserName, projectName, "", dwDbName, "",
                            getMetadataFromTargetTableSql, dropTempTableSql, new HiveClient.ResultSetConsumer<Map<String, String>>() {
                                @Override
                                public Map<String, String> apply(ResultSet rs) throws SQLException {
                                    Map<String, String> targetTableColumnMap = new LinkedHashMap<>();
                                    ResultSetMetaData targetTableMetadata = rs.getMetaData();
                                    for (int i = 1; i <= targetTableMetadata.getColumnCount(); i++) {
                                        String columnLabel = targetTableMetadata.getColumnLabel(i).split("\\.")[1];
                                        if (!partitionSet.contains(columnLabel)) {
                                            String type = combineTypeAndLength(
                                                    targetTableMetadata.getColumnTypeName(i),
                                                    targetTableMetadata.getPrecision(i),
                                                    targetTableMetadata.getScale(i));
                                            targetTableColumnMap.put(columnLabel, type);

                                        }
                                    }

                                    return targetTableColumnMap;
                                }
                            });
                    if(targetTableColumnMap == null) throw new RuntimeException(String.format("query MetaData from target table error. table:%s", targetTableFullName));

//                    ResultSet resultSet = HiveConnector.executeHiveSql(dwConnectUrl, dwDbName, dwUserName,
//                    getMetadataFromTargetTableSql, dropTempTableSql, projectName);
//                    Map<String, String> targetTableColumnMap = new LinkedHashMap<>();
//                    ResultSetMetaData targetTableMetadata = resultSet.getMetaData();
//                    for (int i = 1; i <= targetTableMetadata.getColumnCount(); i++) {
//                        String columnLabel = targetTableMetadata.getColumnLabel(i).split("\\.")[1];
//                        if (!partitionSet.contains(columnLabel)) {
//                            String type = combineTypeAndLength(
//                                    targetTableMetadata.getColumnTypeName(i),
//                                    targetTableMetadata.getPrecision(i),
//                                    targetTableMetadata.getScale(i));
//                            targetTableColumnMap.put(columnLabel, type);
//
//                        }
//                    }

                    log.info("targetTableColumnMap :{}", targetTableColumnMap);

                    // 分割第一行
                    String[] titles = firstLine.split(",", -1);
                    int colSize = titles.length;

                    StringBuilder tempTableColumnDefStr = new StringBuilder();
                    String skipTitle = "";
                    // 没有表头
                    if (CSVFileHasTitle.NOT_HAVE.name().equals(hasTitle)) {
                        // 根据csv列的index创建对应的hive表的列
                        for (int i = 1;i <= colSize;i++) {
                            if (i == colSize) {
                                tempTableColumnDefStr.append("col");
                                tempTableColumnDefStr.append(i);
                                tempTableColumnDefStr.append(" string");
                            } else {
                                tempTableColumnDefStr.append("col");
                                tempTableColumnDefStr.append(i);
                                tempTableColumnDefStr.append(" string");
                                tempTableColumnDefStr.append(",");
                            }
                        }
                    } else { // 有表头
                        // 根据csv的表头创建对应的hive表的列
                        for (int i = 0;i < colSize;i++) {
                            if (i == colSize - 1) {
                                tempTableColumnDefStr.append(StringUtils.trimToEmpty(titles[i]));
                                tempTableColumnDefStr.append(" string");
                            } else {
                                tempTableColumnDefStr.append(StringUtils.trimToEmpty(titles[i]));
                                tempTableColumnDefStr.append(" string");
                                tempTableColumnDefStr.append(",");
                            }
                        }
                        skipTitle = " TBLPROPERTIES (\"skip.header.line.count\"=\"1\")";
                    }

//                    List<String> tempTableColumnNameList = targetTableColumnMap.entrySet().stream()
//                            .map(e -> StringUtils.trimToEmpty(target2TempColumnMap.get(e.getKey().toLowerCase())))
//                            .filter(StringUtils::isNotEmpty)
//                            .collect(Collectors.toList());
//                    String tempTableColumns = String.join(",", tempTableColumnNameList);
                    log.info("Temp table column def: {}", tempTableColumnDefStr.toString());

                    Path tempTableFolder = new Path(dwLocation + tempTableName);
                    if (!fs.exists(tempTableFolder)) {
                        fs.mkdirs(tempTableFolder);
                    }
                    fs.rename(new Path(finalCsvFilePath), new Path(tempTableFile));

                    // 1.创建临时表
                    String createTempTableSql = String.format(" CREATE TABLE %s ( %s ) " +
                                    "ROW FORMAT SERDE " +
                                    "'org.apache.hadoop.hive.serde2.OpenCSVSerde' " +
                                    "WITH SERDEPROPERTIES (\"separatorChar\"=\",\") " +
                                    "STORED AS TEXTFILE LOCATION '%s'%s",
                            tempTableFullName, tempTableColumnDefStr.toString(),
                            dwLocation + tempTableName, skipTitle);
                    log.info("Temp table creation sql: {}", createTempTableSql);
                    stepExecution.getExecutionContext().putString("Create temp table SQL:", createTempTableSql);
//                    HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName,
//                            createTempTableSql, dropTempTableSql, projectName);
                    boolean f = HiveClient.getInstance().executeHiveSqlNoRes(dwConnectUrl,dwUserName,projectName,"",dwDbName,"",createTempTableSql,dropTempTableSql);
                    if(!f)throw new SQLException(String.format("execute SQL Error. SQL:%s", createTempTableSql));
//                    // 2.装载CSV文件数据到临时表
//                    String loadDataSql = String.format(" LOAD DATA INPATH '%s' INTO TABLE %s",
//                            csvFilePath, tempTableFullName);
//                    log.info("Load data sql: {}", loadDataSql);
//                    stepExecution.getExecutionContext().putString("Load data sql:", loadDataSql);
//                    HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName,
//                            loadDataSql, dropTempTableSql, projectName);

                    // 3.拼接partition信息供临时表导入目标表使用
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

                    // 4.临时表导入目标表
                    String overwrite;
                    if (DataCoverStrategy.PARTITION_COVER.name().equals(dataCoverStrategy)) {
                        overwrite = "OVERWRITE";
                    } else {
                        overwrite = "INTO";
                    }
                    log.info("overwrite : {}", dataCoverStrategy);
                    String selectColumns = targetTableColumnMap.keySet().stream()
                            .map(k -> {
                                if ((!target2TempColumnMap.containsKey(k.toLowerCase())) && (!staticPartitionColumnSet.contains(k.toLowerCase()))){
                                    return " null as " + k;
                                } else {
                                    if (StringUtils.isBlank(target2TempColumnMap.get(k))) {
                                        return "null as " + k;
                                    } else {
                                        return target2TempColumnMap.get(k) + " as " + k;
                                    }
                                }
                            })
                            .collect(Collectors.joining(","))
                            ;
                    String copyFromTempTableSql = String.format(
                            "INSERT %s TABLE %s PARTITION ( %s ) SELECT %s FROM %s ",
                            overwrite, targetTableFullName, StringUtils.trimToEmpty(partitionSql),
                            selectColumns, tempTableFullName);
                    log.info("Copy from temp table sql: {}", copyFromTempTableSql);
                    stepExecution.getExecutionContext().putString("Copy temp table data SQL:", copyFromTempTableSql);
//                    HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName,
//                            copyFromTempTableSql, dropTempTableSql, projectName);
                    f = HiveClient.getInstance().executeHiveSqlNoRes(dwConnectUrl, dwUserName, projectName, "",dwDbName,"",copyFromTempTableSql, dropTempTableSql);
                    if(!f)throw new SQLException(String.format("execute SQL Error. SQL:%s", copyFromTempTableSql));

                } catch (Exception e) {
                    log.error("Import data ERROR!", e);
                    log.error("Import data ERROR!", e.getCause());
                    throw e;
                } finally {
                    // 删除临时表
                    dropTempTable(dwConnectUrl, dwDbName, dwUserName, dropTempTableSql, projectName, fs, tempTableFile, finalCsvFilePath);
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
                               String dropTempTableSql, String projectName, FileSystem fs, String fromPath, String toPath) throws Exception {
        // drop之前先把文件拷贝移动回原来位置
        try {
            Path from = new Path(fromPath);
            Path to = new Path(toPath);
            if (fs.exists(from) && !fs.exists(to)) {
                fs.rename(new Path(fromPath), new Path(toPath));
            }
        } catch (Exception e) {
            log.error("restore source file error");
            throw e;
        }
//        HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName,
//                dropTempTableSql, null, projectName);

        boolean f = HiveClient.getInstance().executeHiveSqlNoRes(dwConnectUrl, dwUserName, projectName, "", dwDbName, "", dropTempTableSql, null);
        if(!f)throw new SQLException(String.format("execute SQL Error. SQL:%s", dropTempTableSql));
    }

    private String getDropTempTableSql(String tempTableFullName, StepExecution stepExecution) {
        String dropTempTableSql = "DROP TABLE IF EXISTS " + tempTableFullName;
        stepExecution.getExecutionContext().putString("Drop temp table SQL:", dropTempTableSql);
        return dropTempTableSql;
    }

}
