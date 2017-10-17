package cn.rongcapital.chorus.modules.streamjob;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
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

import cn.rongcapital.chorus.modules.streamjob.service.StreamJobStatisticService;
import cn.rongcapital.chorus.modules.utils.retry.SimpleTasklet;
import cn.rongcapital.log.JobListenerForLog;
import lombok.extern.slf4j.Slf4j;

/**
 * 流式任务统计job类
 * @author kevin.gong
 * @Time 2017年8月9日 上午9:43:51
 */
@Slf4j
@org.springframework.context.annotation.Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "cn.rongcapital.chorus.modules.streamjob")
public class StreamJobStatisticJob {

    private static final String DRIVE_CLASS_NAME = "com.mysql.jdbc.Driver";
    
    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;
    
    @Autowired
    private StreamJobStatisticService streamJobStatisticService;

    @Bean
    public JobExecutionListener logListener() {
        return new JobListenerForLog();
    }

    @Bean
    public Job job(@Qualifier("jobStep") Step jobStep, @Qualifier("logListener") JobExecutionListener logListener) {
        return jobs.get("streamJobStatisticJob").listener(logListener).start(jobStep).build();
    }

    @Bean
    public Step jobStep(@Value("${monitorSpringXdZkPath}") String monitorSpringXdZkPath, @Value("${zookeeperAddress}") String zookeeperAddress,
            @Value("${zookeeperTimeout}") Integer zookeeperTimeout, @Value("${chorusUrl}") String chorusUrl,
            @Value("${chorusUserName}") String chorusUserName, @Value("${chorusPassword}") String chorusPassword,
            @Value("${retryCount:}") Integer retryCount) {
        return steps.get("jobStep").tasklet(new SimpleTasklet() {
            @Override
            public RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) throws Exception {
                Connection chorusConn = null;
                try {
                    chorusConn = Connect(DRIVE_CLASS_NAME, chorusUrl, chorusUserName, chorusPassword);
                    streamJobStatisticService.jobStatistic(chorusConn, monitorSpringXdZkPath, zookeeperAddress, zookeeperTimeout);
                } catch (Exception e) {
                    log.error("流式任务统计失败!", e);
                    throw e;
                } finally {
                    if(chorusConn !=null && !chorusConn.isClosed()) {
                        chorusConn.close();
                    }
                }
                return RepeatStatus.FINISHED;
            }
        }.retry(retryCount)).build();
    }

    public static Connection Connect(String driveClassName, String url, String userName, String password) throws ClassNotFoundException, SQLException{  
        Class.forName(driveClassName);  
        return DriverManager.getConnection(url, userName, password);  
    } 
}
