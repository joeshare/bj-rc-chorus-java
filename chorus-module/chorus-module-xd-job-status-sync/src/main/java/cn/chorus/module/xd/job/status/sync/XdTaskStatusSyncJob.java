package cn.chorus.module.xd.job.status.sync;

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

import cn.chorus.module.xd.job.status.sync.service.XdJobStatusSyncService;
import cn.rongcapital.chorus.modules.utils.retry.SimpleTasklet;
import cn.rongcapital.log.JobListenerForLog;
import lombok.extern.slf4j.Slf4j;
/**
 * @author Lovett
 */
@Slf4j
@org.springframework.context.annotation.Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "cn.chorus.module.xd.job.status.sync")
public class XdTaskStatusSyncJob {

    private static final String DRIVE_CLASS_NAME = "com.mysql.jdbc.Driver";

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private XdJobStatusSyncService xdJobStatusSyncService;

    @Bean
    public JobExecutionListener logListener() {
        return new JobListenerForLog();
    }

    @Bean
    public Job job(@Qualifier("jobStep") Step jobStep, @Qualifier("logListener") JobExecutionListener logListener) {
        return jobs.get("xdTaskStatusSyncJob").listener(logListener).start(jobStep).build();
    }

    @Bean
    public Step jobStep(@Value("${xdUrl}") String xdUrl, @Value("${xdUserName}") String xdUserName,
            @Value("${xdPassword}") String xdPassword,
            @Value("${retryCount:}") Integer retryCount, @Value("${timeOutHours}") Integer timeOutHours) {
        return steps.get("jobStep").tasklet(new SimpleTasklet() {
            @Override
            public RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) throws Exception {
                Connection xdConn = null;
                try {
                    xdConn = Connect(DRIVE_CLASS_NAME, xdUrl, xdUserName, xdPassword);
                    xdJobStatusSyncService.syncTimeoutJob(xdConn, timeOutHours);
                } catch (Exception e) {
                    log.error("XD 超时任务状态同步失败!", e);
                    throw e;
                } finally {
                    try {
                        if (xdConn != null && !xdConn.isClosed()) {
                            xdConn.close();
                        }
                    } catch (SQLException e) {
                        log.error("xd 连接关闭异常！", e);
                    }
                }
                return RepeatStatus.FINISHED;
            }
        }.retry(retryCount)).build();
    }

    public static Connection Connect(String driveClassName, String url, String userName, String password)
            throws ClassNotFoundException, SQLException {
        Class.forName(driveClassName);
        return DriverManager.getConnection(url, userName, password);
    }
}
