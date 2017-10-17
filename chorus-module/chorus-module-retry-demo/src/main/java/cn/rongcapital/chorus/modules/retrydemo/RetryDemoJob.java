package cn.rongcapital.chorus.modules.retrydemo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
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

import cn.rongcapital.chorus.modules.utils.retry.SimpleTasklet;
import cn.rongcapital.log.JobListenerForLog;
import lombok.extern.slf4j.Slf4j;

/**
 * retry测试类。 业务流程copy自module：import_data - ImportDataJob
 * 
 * @author kevin.gong
 * @Time 2017年5月22日 下午4:24:08
 */
@Slf4j
@org.springframework.context.annotation.Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "cn.rongcapital.chorus.modules.retrydemo")
public class RetryDemoJob {

    private static final String P_DATE_PARTITION_KEY = "p_date";

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    /**
     * 异常计数器 <br>
     * ecount = 4 : NullPointException <br>
     * ecount = 3 : error sql connection <br>
     * ecount = 2 : bad sql <br>
     * ecount = 1 : sqoop command error <br>
     * ecount = 0 : success
     */
    private static int ecount = 4;

    @Bean
    public JobExecutionListener logListener() {
        return new JobListenerForLog();
    }

    @Bean
    public Job job(@Qualifier("step1") Step step1, @Qualifier("logListener") JobExecutionListener logListener) {
        return jobs.get("retryDemoJob").listener(logListener).start(step1).build();
    }

    /**
     * retry测试step
     * <p>该方法业务流程与ImportDataJob - importDataStep1相同，只增加了retry机制。异常顺序参考ecount属性注释，即执行第五次才会成功。
     * <p>retry原理为将原Tasklet接口，替换成重新封装的SimpleTasklet类。原代码业务模块都不需要改变，如果需要添加retry机制，调用SimpleTasklet的retry方法，添加retry次数即可。
     * <p>例：<br>
     * 原写法：return steps.get(${stepName}).tasklet(new Tasklet() {<br>
     *       @Override<br>
     *       public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {<br>
     *           //业务逻辑<br>
     *           return RepeatStatus.FINISHED;<br>
     *       }<br>
     *   }).build();<br>
     *   
     * 修改后： return steps.get(${stepName}).tasklet(new SimpleTasklet() {<br>
     *       @Override<br>
     *       public RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) throws Exception {<br>
     *           //业务逻辑<br>
     *           return RepeatStatus.FINISHED;<br>
     *       }<br>
     *   }.retry(${retryCount})).build();
     *   
     * <p>retryCount为重试次数。为0时，不进行重试。
     * <p>SimpleTasklet还支持只对指定类型Exception进行retry
     */
    @Bean
    public Step step1(@Value("${dwConnectUrl}") String dwConnectUrl, @Value("${dwDbName}") String dwDbName,
            @Value("${dwTableName}") String dwTableName, @Value("${dwUserName}") String dwUserName,
            @Value("${dwLocation}") String dwLocation, @Value("${rdbConnectUrl}") String rdbConnectUrl,
            @Value("${rdbUserName}") String rdbUserName, @Value("${rdbPassword}") String rdbPassword,
            @Value("${rdbTable}") String rdbTable, @Value("${where:}") String where,
            @Value("${columnNameMapStr}") String columnNameMapStr, @Value("${partitionMapStr}") String partitionMapStr,
            @Value("${retryCount:}") Integer retryCount) {
        return steps.get("step1").tasklet(new SimpleTasklet() {
            @Override
            public RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) throws Exception {
                StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
                JobParameters jobParameters = stepExecution.getJobParameters();
                log.info("---dwConnectUrl: {}", dwConnectUrl);
                log.info("---dwDbName: {}", dwDbName);
                log.info("---dwTableName: {}", dwTableName);
                log.info("---dwUserName: {}", dwUserName);
                log.info("---dwLocation: {}", dwLocation);
                log.info("---rdbConnectUrl: {}", rdbConnectUrl);
                log.info("---rdbUserName: {}", rdbUserName);
                log.info("---rdbPassword: {}", rdbPassword);
                log.info("---rdbTable: {}", rdbTable);
                log.info("---where: {}", where);
                log.info("---columnNameMapStr: {}", columnNameMapStr);
                log.info("---partitionMapStr: {}", partitionMapStr);
                log.info("---jobParameters: {}", jobParameters);
                log.info("---retryCount: {}", retryCount);

                // job初始化参数
                Map<String, String> target2TempColumnMap = JSON.parseObject(columnNameMapStr).entrySet().stream()
                        .collect(Collectors.toMap(e -> String.valueOf(e.getValue()), Map.Entry::getKey));
                Map<String, Object> partitionMap = JSON.parseObject(partitionMapStr);

                // job parameter获取
                String jpPartitionMapStr = jobParameters.getString("partitionMapStr");
                Map<String, Object> jpPartitionMap = JSON.parseObject(jpPartitionMapStr);
                jpPartitionMap = jpPartitionMap == null ? new HashMap<>() : jpPartitionMap;

                // 变量准备
                String targetTableFullName = dwDbName + "." + dwTableName;
                String tempTableName = dwTableName + "_" + String.valueOf(System.currentTimeMillis());
                String tempTableFullName = dwDbName + "." + tempTableName;
                String dropTempTableSql = getDropTempTableSql(tempTableFullName, stepExecution);
                String pDateValue = String.valueOf(
                        jpPartitionMap.getOrDefault(P_DATE_PARTITION_KEY, partitionMap.get(P_DATE_PARTITION_KEY)));

                /** TEST1:空指针异常 begin **/
                if (ecount == 4) {
                    ecount--;
                    pDateValue = null;
                }
                /** TEST1:空指针异常 end **/

                final String finalPDateValue = pDateValue.equals("$executionDate")
                        ? new DateTime().toString("yyyy-MM-dd") : pDateValue;
                String projectName = dwDbName.substring(dwDbName.indexOf("_") + 1);

                try {
                    // 0.拼接临时表列信息SQL, 不考虑进行分区.
                    String getMetadataFromTargetTableSql = String.format(" SELECT * from %s LIMIT 1 ",
                            targetTableFullName);
                    stepExecution.getExecutionContext().putString("Get MetaData from target table SQL:",
                            getMetadataFromTargetTableSql);
                    String tempTableColumnDefStr;
                    ResultSet resultSet = HiveConnector.executeHiveSql(dwConnectUrl, dwDbName, dwUserName,
                            getMetadataFromTargetTableSql, dropTempTableSql, projectName);
                    Map<String, String> targetTableColumnMap = new LinkedHashMap<>();
                    ResultSetMetaData targetTableMetadata = resultSet.getMetaData();
                    for (int i = 1; i <= targetTableMetadata.getColumnCount(); i++) {
                        String columnLabel = targetTableMetadata.getColumnLabel(i).split("\\.")[1];
                        targetTableColumnMap.put(columnLabel, targetTableMetadata.getColumnTypeName(i));
                    }
                    List<String> tempTableColumnNameList = targetTableColumnMap.entrySet().stream()
                            .map(e -> StringUtils.trimToEmpty(target2TempColumnMap.get(e.getKey())))
                            .filter(StringUtils::isNotEmpty).collect(Collectors.toList());
                    tempTableColumnDefStr = targetTableColumnMap.entrySet().stream()
                            .filter(e -> target2TempColumnMap.containsKey(e.getKey()))
                            .map(e -> target2TempColumnMap.get(e.getKey()) + " " + e.getValue())
                            .collect(Collectors.joining(","));
                    log.info("Temp table column def: {}", tempTableColumnDefStr);
                    stepExecution.getExecutionContext().putString("Target table's columns", tempTableColumnDefStr);

                    // 1.创建临时表
                    String createTempTableSql = String.format(
                            " CREATE TABLE %s ( %s )  STORED AS TEXTFILE LOCATION '%s'", tempTableFullName,
                            tempTableColumnDefStr, dwLocation + tempTableName);
                    log.info("Temp table creation sql: {}", createTempTableSql);
                    stepExecution.getExecutionContext().putString("Create temp table SQL:", createTempTableSql);

                    /** TEST3:BAD SQL begin **/
                    if (ecount == 2) {
                        ecount--;
                        createTempTableSql = "select n_data from t_asdf";
                    }
                    /** TEST3:BAD SQL end **/

                    /** TEST2:数据库连接异常 begin **/
                    if (ecount == 3) {
                        ecount--;
                        HiveConnector.executeHiveSqlNoRes(dwConnectUrl, null, null, createTempTableSql,
                                dropTempTableSql, projectName);
                    }
                    /** TEST2:数据库连接异常 end **/

                    HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName, createTempTableSql,
                            dropTempTableSql, projectName);

                    // 2.Sqoop导RDB数据到临时表
                    String tempTableColumns = String.join(",", tempTableColumnNameList);
                    StringBuilder sb = new StringBuilder();
                    sb.append("sqoop import").append(" -Dmapreduce.job.queuename=").append(projectName)
                            .append(" -Dtez.queue.name=").append(projectName);
                    String finalRdbConnectUrl = rdbConnectUrl.startsWith("jdbc:mysql")
                            ? rdbConnectUrl + "?useUnicode=true&characterEncoding=utf-8" : rdbConnectUrl;
                    sb.append(" --connect '").append(finalRdbConnectUrl).append("'");
                    sb.append(" --username '").append(rdbUserName).append("'");
                    sb.append(" --password '").append(rdbPassword).append("'");
                    sb.append(" --table '").append(rdbTable).append("'");
                    sb.append(" --hcatalog-database '").append(dwDbName).append("'");
                    sb.append(" --hcatalog-table '").append(tempTableName).append("'");
                    sb.append(" --delete-target-dir");
                    sb.append(" --columns '").append(tempTableColumns).append("'");
                    if (StringUtils.isNotBlank(where)) {
                        sb.append(" --where '").append(where).append("'");
                    }
                    sb.append(" --m 1");

                    String sqoopCommand = sb.toString();
                    log.info("Sqoop import data sqoopCommand: {}", sqoopCommand);
                    stepExecution.getExecutionContext().putString("Sqoop Command", sqoopCommand);
                    LocalShellTool local = new LocalShellTool();
                    /** TEST4:错误的sqoop命令 begin **/
                    if (ecount == 1) {
                        ecount--;
                        sqoopCommand = "not exist sqoop command";
                    }
                    /** TEST4:错误的sqoop命令 end **/
                    Map<String, String> sqoopResult = local.exec(sqoopCommand);
                    log.info("Sqoop import data sqoopResult: {}", sqoopResult);
                    if (sqoopResult != null) {
                        String strResult = sqoopResult.get(LocalShellTool.RESULT_KEY);
                        String strError = sqoopResult.get(LocalShellTool.ERROR_KEY);
                        int exitValue = Integer.parseInt(sqoopResult.get(LocalShellTool.EXITVALUE_KEY));
                        stepExecution.getExecutionContext().putString("Sqoop ExitValue", String.valueOf(exitValue));
                        stepExecution.getExecutionContext().putString("Sqoop Result", strResult);
                        stepExecution.getExecutionContext().putString("Sqoop Error", strError);
                        if (exitValue != 0) {
                            dropTempTable(dwConnectUrl, dwDbName, dwUserName, dropTempTableSql, projectName);
                            throw new RuntimeException(strError);
                        }
                    } else {
                        dropTempTable(dwConnectUrl, dwDbName, dwUserName, dropTempTableSql, projectName);
                        log.error("Execute sqoop ERROR!");
                        throw new RuntimeException("Sqoop result is null!");
                    }

                    // 3.Drop原p_date分区
                    String dropPDatePartitionSql = String.format("alter table %s drop partition(p_date='%s')",
                            targetTableFullName, finalPDateValue);
                    log.info("Drop origin p_date partition sql: {}", dropPDatePartitionSql);

                    HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName, dropPDatePartitionSql,
                            dropTempTableSql, projectName);

                    // 4.拼接partition信息供临时表导入目标表使用
                    String partitionSql = targetTableColumnMap.entrySet().stream().map(Map.Entry::getKey)
                            .filter(c -> partitionMap.containsKey(c) || partitionMap.containsKey("#" + c)).map(c -> {
                        if (c.equalsIgnoreCase(P_DATE_PARTITION_KEY))
                            return String.format("%s='%s'", P_DATE_PARTITION_KEY, finalPDateValue);
                        else if (partitionMap.containsKey("#" + c))
                            // 动态分区支持
                            return c;
                        else
                            return String.format("%s='%s'", c,
                                    StringUtils.trimToEmpty(String.valueOf(partitionMap.get(c))));
                    }).collect(Collectors.joining(","));

                    // 5.临时表导入目标表
                    String copyFromTempTableSql = String.format(
                            "INSERT OVERWRITE TABLE %s PARTITION ( %s ) SELECT %s FROM %s ", targetTableFullName,
                            StringUtils.trimToEmpty(partitionSql), tempTableColumns, tempTableFullName);
                    log.info("Copy from temp table sql: {}", copyFromTempTableSql);
                    stepExecution.getExecutionContext().putString("Copy temp table data SQL:", copyFromTempTableSql);
                    HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName, copyFromTempTableSql,
                            dropTempTableSql, projectName);

                    // 删除临时表
                    dropTempTable(dwConnectUrl, dwDbName, dwUserName, dropTempTableSql, projectName);
                } catch (Exception e) {
                    log.error("Import data ERROR!", e);
                    log.error("Import data ERROR!", e.getCause());
                    throw e;
                }

                /** TEST0:成功后ecount回置 **/
                ecount = 4;
                /** TEST0:成功后ecount回置 **/

                return RepeatStatus.FINISHED;
            }
        }.retry(retryCount)).build();
    }

    private void dropTempTable(String dwConnectUrl, String dwDbName, String dwUserName, String dropTempTableSql,
            String projectName) throws SQLException {
        HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName, dropTempTableSql, null, projectName);
    }

    private String getDropTempTableSql(String tempTableFullName, StepExecution stepExecution) {
        String dropTempTableSql = "DROP TABLE IF EXISTS " + tempTableFullName;
        stepExecution.getExecutionContext().putString("Drop temp table SQL:", dropTempTableSql);
        return dropTempTableSql;
    }

}
