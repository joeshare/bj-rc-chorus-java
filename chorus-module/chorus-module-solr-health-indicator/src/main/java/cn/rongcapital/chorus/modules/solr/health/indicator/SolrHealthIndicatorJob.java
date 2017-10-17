package cn.rongcapital.chorus.modules.solr.health.indicator;

import cn.rongcapital.chorus.modules.utils.mail.MailSenderImpl;
import cn.rongcapital.chorus.modules.utils.retry.SimpleTasklet;
import cn.rongcapital.log.JobListenerForLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.mail.MessagingException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yimin
 */
@Slf4j
@Configuration
@EnableBatchProcessing
@PropertySource(value = "classpath:/config/spring-module.properties")
@ComponentScan(basePackages = {"cn.rongcapital.chorus.modules.solr.health.indicator", "cn.rongcapital.chorus.modules.utils.mail"})
public class SolrHealthIndicatorJob {
    private static final String JOB_NAME                                                     = "solr-health-indicator-job";
    private static final String STEP_NAME                                                    = "solr-health-indicator-job-steps-cluster-state";
    public static final  String SOLR_HEALTH_INDICATOR_JOB_STEPS_CLUSTER_STATE_STEP_BEAN_NAME = "solrHealthIndicatorJobStepsClusterState";
    public static final  String JOB_LISTENER_FOR_LOG_OF_SOLR_HEALTH_INDICATOR_JOB_BEAN_NAME  = "JobListenerForLogOfSolrHealthIndicatorJob";

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private SolrMetricService solrMetricService;

    @Autowired
    private List<Indicator> indicators;

    @Autowired
    private MailSenderImpl mailSender;
    @Value("${mail.from}")
    private String         from;
    @Value("${mail.to}")
    private String[]       to;

    @Bean(name = JOB_LISTENER_FOR_LOG_OF_SOLR_HEALTH_INDICATOR_JOB_BEAN_NAME)
    public JobListenerForLog jobListenerForLog() {
        return new JobListenerForLog();
    }

    @Bean
    public Job job(
            @Qualifier(JOB_LISTENER_FOR_LOG_OF_SOLR_HEALTH_INDICATOR_JOB_BEAN_NAME) JobExecutionListener logListener,
            @Qualifier(SOLR_HEALTH_INDICATOR_JOB_STEPS_CLUSTER_STATE_STEP_BEAN_NAME) TaskletStep step
    ) {
        return jobs.get(JOB_NAME).listener(logListener).start(step).build();
    }

    @Bean(name = SOLR_HEALTH_INDICATOR_JOB_STEPS_CLUSTER_STATE_STEP_BEAN_NAME)
    public TaskletStep step() {
        return steps.get(STEP_NAME).tasklet(new SimpleTasklet() {
            @Override
            public RepeatStatus execute(
                    ChunkContext chunkContext, StepContribution contribution
            ) throws Exception {
                ClusterState clusterState = solrMetricService.clusterState();
                List<Indications> indications = indicators.stream()
                                                          .map(indicator -> indicator.indicate(clusterState))
                                                          .filter(Indications::isNotEmpty)
                                                          .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(indications)) {
                    log.info("SOLR CLOUD CLUSTER NOT IN GOOD STATUS");
                    notification(indications);
                    log.info("send out notifications");
                } else {
                    log.info("SOLR CLOUD CLUSTER IN GOOD STATUS");
                }
                return RepeatStatus.FINISHED;
            }
        }).build();
    }

    private void notification(List<Indications> indications) {
        String htmlText =
                indications.stream()
                           .map(is -> is.getIndications().stream()
                                        .map(Indication::toString)
                                        .reduce("", (a, b) -> a + "<br/>" + b))
                           .reduce("", (a, b) -> a + "<hr/>" + b);
        if (log.isDebugEnabled()) {
            log.debug("mail content " + htmlText);
        }

        try {
            mailSender.send("Solr Cloud Cluster Alarm", htmlText, from, to);
        } catch (MessagingException e) {
            log.error("Failed to send email from {} ,to {}", from, to);
            log.error(e.getLocalizedMessage(), e);
        }
    }
}
