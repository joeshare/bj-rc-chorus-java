package cn.rongcapital.chorus.modules.json2hive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.hsqldb.lib.StringUtil;
import org.joda.time.DateTime;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
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
import com.google.gson.internal.LinkedTreeMap;

import cn.rongcapital.chorus.modules.utils.retry.SimpleTasklet;
import cn.rongcapital.log.JobListenerForLog;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@org.springframework.context.annotation.Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "cn.rongcapital.chorus.modules.json2hive")
public class Json2HiveJob {

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
    public Job job(@Qualifier("json2HiveStep") Step json2HiveStep,
                   @Qualifier("logListener") JobExecutionListener logListener) {
        return jobs.get("json2HiveJob").listener(logListener).start(json2HiveStep).build();
    }

    @Bean
    public Step json2HiveStep(
            @Value("${filePath}") String filePath,
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
        return steps.get("json2HiveStep").tasklet(new SimpleTasklet() {
            @Override
            public RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) throws Exception {

                StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
                JobParameters jobParameters = stepExecution.getJobParameters();

                log.info("---filePath: {}", filePath);
                log.info("---dwConnectUrl: {}", dwConnectUrl);
                log.info("---dwDbName: {}", dwDbName);
                log.info("---dwTableName: {}", dwTableName);
                log.info("---dwUserName: {}", dwUserName);
                log.info("---dwLocation: {}", dwLocation);
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
                String pDateValue = String.valueOf(partitionMap.getOrDefault(P_DATE_PARTITION_KEY,
                        "$executionDate"));
                final String finalPDateValue = pDateValue.equals("$executionDate") ?
                        new DateTime().toString("yyyy-MM-dd") : pDateValue;
                log.info("$executionDate: {}", finalPDateValue);

                // 从HDFS上读取文件
                FSDataInputStream fsr;
                BufferedReader bufferedReader = null;
                String firstLine = "";
                FileSystem fs;
                FileStatus[] files;
                StringBuilder tempTableColumnDefStr = new StringBuilder();
                try {
                    Configuration conf = new Configuration();
                    fs = FileSystem.get(conf);
                    Path path = new Path(filePath);
                    files = fs.listStatus(path);
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

                for (int fn = 0; fn < files.length; fn++) {
                    if(!files[fn].isFile()){
                        continue;
                    }
                    Path fPath = files[fn].getPath();
                    String pathName = fPath.toString().substring(fPath.toString().indexOf(filePath), fPath.toString().length());
                    if(fPath.toString().endsWith("_SUCCESS")) {
                        continue;
                    }
                    String fileName = pathName.substring(pathName.lastIndexOf("/") + 1, pathName.length());
                    String tempTableFile = dwLocation + tempTableName + "/" + fileName;
                    fsr = fs.open(fPath);
                    bufferedReader = new BufferedReader(new InputStreamReader(fsr));
                    
                    if(StringUtils.isEmpty(firstLine)) {
                        // 读取第一行
                        while ((firstLine = bufferedReader.readLine()) != null) {
                            LinkedTreeMap firstLineMap = null;
                            try {
                                firstLineMap = JSON.parseObject(firstLine, LinkedTreeMap.class);
                            } catch (Exception e) {
                                log.error("parse json file firstLine error : {}", firstLine, e);
                            }
                            
                            firstLineMap.keySet().stream().forEach(column -> {
                                tempTableColumnDefStr.append(column);
                                tempTableColumnDefStr.append(" string");
                                tempTableColumnDefStr.append(",");
                            });
                            tempTableColumnDefStr.deleteCharAt(tempTableColumnDefStr.length()-1);
                                
                            log.info("Temp table column def: {}", tempTableColumnDefStr.toString());
                            break;
                        }
                        stepExecution.getExecutionContext().putString("First Line", String.valueOf(firstLine));
                    }
    
                    if(StringUtils.isEmpty(firstLine)) {
                        continue;
                    }
                    
                    try {
    
                        // 0.拼接临时表列信息SQL, 不考虑进行分区.
                        String getMetadataFromTargetTableSql = String.format(" SELECT * from %s LIMIT 1 ", targetTableFullName);
                        stepExecution.getExecutionContext()
                                .putString("Get MetaData from target table SQL:", getMetadataFromTargetTableSql);
    
                        ResultSet resultSet = HiveConnector.executeHiveSql(dwConnectUrl, dwDbName, dwUserName,
                        getMetadataFromTargetTableSql, dropTempTableSql, projectName);
                        Map<String, String> targetTableColumnMap = new LinkedHashMap<>();
                        ResultSetMetaData targetTableMetadata = resultSet.getMetaData();
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
                        log.info("targetTableColumnMap :{}", targetTableColumnMap);
    
    
                        Path tempTableFolder = new Path(dwLocation + tempTableName);
                        if (!fs.exists(tempTableFolder)) {
                            fs.mkdirs(tempTableFolder);
                        }
                        stepExecution.getExecutionContext().putString("tempTableFile:", tempTableFile);
                        fs.rename(fPath, new Path(tempTableFile));
                        
                        // 1.创建临时表
                        String createTempTableSql = String.format(" CREATE TABLE %s ( %s ) " +
                                        "ROW FORMAT SERDE " +
                                        "'org.apache.hive.hcatalog.data.JsonSerDe' " +
                                        "STORED AS TEXTFILE LOCATION '%s'",
                                tempTableFullName, tempTableColumnDefStr.toString(),
                                dwLocation + tempTableName);
                        log.info("Temp table creation sql: {}", createTempTableSql);
                        stepExecution.getExecutionContext().putString("Create temp table SQL:", createTempTableSql);
                        HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName,
                                createTempTableSql, dropTempTableSql, projectName);
    
                        // 2.拼接partition信息供临时表导入目标表使用
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
    
                        // 3.临时表导入目标表
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
                        HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName,
                                copyFromTempTableSql, dropTempTableSql, projectName);
    
                    } catch (Exception e) {
                        log.error("Import data ERROR!", e);
                        log.error("Import data ERROR!", e.getCause());
                        throw e;
                    } finally {
                        stepExecution.getExecutionContext().putString("pathName:", pathName);
                        // 删除临时表
                        dropTempTable(dwConnectUrl, dwDbName, dwUserName, dropTempTableSql, projectName, fs, tempTableFile, pathName);
                    }
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
        HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName,
                dropTempTableSql, null, projectName);
    }

    private String getDropTempTableSql(String tempTableFullName, StepExecution stepExecution) {
        String dropTempTableSql = "DROP TABLE IF EXISTS " + tempTableFullName;
        stepExecution.getExecutionContext().putString("Drop temp table SQL:", dropTempTableSql);
        return dropTempTableSql;
    }

}
