package cn.rongcapital.chorus.module.hdfsfilecopy;

import cn.rongcapital.log.JobListenerForLog;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yangdawei on 22/05/2017.
 */
@Slf4j
@org.springframework.context.annotation.Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "cn.rongcapital.chorus.module.hdfsfilecopy")
public class ImportDataJob {

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
    public Job job(@Qualifier("importDataStep1") Step step1,
                   @Qualifier("logListener") JobExecutionListener logListener) {
        return jobs.get("importDataJob").listener(logListener).start(step1).build();
    }

    @Bean
    public Step importDataStep1(
            @Value("${hdfsFileSourcePath}") String hdfsFileSourcePath,
            @Value("${hdfsFileTargetPath}") String hdfsFileTargetPath
    ) {
        return steps.get("importDataStep1").tasklet((contribution, chunkContext) -> {
            log.info("---hdfsFileSourcePath: {}", hdfsFileSourcePath);
            log.info("---hdfsFileTargetPath: {}", hdfsFileTargetPath);

            StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();

            try {
            	StringBuffer sb = new StringBuffer();
            	StringBuffer sbSource = new StringBuffer();
            	if(!hdfsFileSourcePath.endsWith("/")){
            		sbSource.append(hdfsFileSourcePath).append("/*");
            	}else{
            		sbSource.append(hdfsFileSourcePath).append("*");
            	}
            	sb.append("export HADOOP_USER_NAME=hdfs;hadoop dfs -cp ").append(sbSource.toString()).append(" ").append(hdfsFileTargetPath);
            	stepExecution.getExecutionContext().putString("copy command", sb.toString());
            	LocalShellTool local = new LocalShellTool();
            	Map<String,String> Result = local.exec(sb.toString());
            	log.info("file copy file: {}", Result);
              if (Result != null) {
              String strResult = Result.get(LocalShellTool.RESULT_KEY);
              String strError = Result.get(LocalShellTool.ERROR_KEY);
              int exitValue = Integer.parseInt(Result.get(LocalShellTool.EXITVALUE_KEY));
              stepExecution.getExecutionContext().putString("copy file ExitValue", String.valueOf(exitValue));
              stepExecution.getExecutionContext().putString("copy file Result", strResult);
              stepExecution.getExecutionContext().putString("copy file Error", strError);
              }
            } catch (Exception e) {
                log.error("Import data ERROR!", e);
                log.error("Import data ERROR!", e.getCause());
                throw e;
            }
            return RepeatStatus.FINISHED;
        }).build();
    }

}
    
