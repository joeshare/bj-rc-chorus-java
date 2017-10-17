package cn.rongcapital.chorus.modules.email_notify.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;

import cn.rongcapital.chorus.modules.email_notify.DataSource;
import cn.rongcapital.chorus.modules.email_notify.EmailSenderTasklet;
import cn.rongcapital.log.JobListenerForLog;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableConfigurationProperties
@PropertySource(value = "classpath:/config/spring-module.properties")
@EnableBatchProcessing
@ComponentScan(basePackages = { "cn.rongcapital.chorus.modules.email_notify", "cn.rongcapital.chorus.modules.utils.mail" })
@Slf4j
public class EmailSenderConfig {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    public JobExecutionListener logListener() {
        return new JobListenerForLog();
    }

    @Bean
    public EmailSenderTasklet emailSenderTasklet(@Value("${mail.username}") String from,
            @Value("${title}") String title,
            @Value("${emailContent}") String content) {
        final EmailSenderTasklet tasklet = new EmailSenderTasklet();
        tasklet.setContent(content);
        tasklet.setTitle(title);
        tasklet.setFrom(from);
        return tasklet;
    }

    @Bean
    public Job job(@Qualifier("step1") Step step1, @Qualifier("logListener") JobExecutionListener logListener) {
        log.info("init job with step1 and listener.");
        return jobs.get("emailSenderJob").listener(logListener).start(step1).build();
    }

    @Bean
    @DependsOn(value = "emailSenderTasklet")
    public Step step1(@Value("${retryCount:}") Integer retryCount,
            @Qualifier("emailSenderTasklet") EmailSenderTasklet tasklet) {
        log.info("init step1 with retry {}", retryCount);
        return steps.get("emailSenderStep").tasklet(tasklet.retry(retryCount)).build();
    }

    @Bean(name = "chorusDataSource")
    public DataSource chorusDatasource(@Value("${chorusURL}") String chorusURL,
            @Value("${chorusUser}") String chorusUser, @Value("${chorusPassword}") String chorusPassword) {
        log.info("init chorus datasource url:{}, user:{}", chorusURL, chorusUser);
        DataSource dataSource = new DataSource();
        dataSource.setUrl(chorusURL);
        dataSource.setUserName(chorusUser);
        dataSource.setPassword(chorusPassword);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        return dataSource;
    }
}
