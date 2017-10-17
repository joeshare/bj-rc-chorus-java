package cn.rongcapital.chorus.modules.table.stats.config;

import org.apache.atlas.AtlasClientV2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;

import cn.rongcapital.chorus.modules.table.stats.DataSource;
import cn.rongcapital.chorus.modules.table.stats.HiveTableMonitorTasklet;
import cn.rongcapital.log.JobListenerForLog;
import lombok.extern.slf4j.Slf4j;

@org.springframework.context.annotation.Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "cn.rongcapital.chorus.modules.table.stats")
@Slf4j
public class HiveTableMonitorConfig {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    public JobExecutionListener logListener() {
        return new JobListenerForLog();
    }

    @Bean
    public Job job(@Qualifier("step1") Step step1, @Qualifier("logListener") JobExecutionListener logListener) {
        log.info("init job with step1 and listener.");
        return jobs.get("hiveTableMonitorJob").listener(logListener).start(step1).build();
    }

    @Bean
    @DependsOn(value = "hiveTableMonitorTasklet")
    public Step step1(@Value("${retryCount:}") Integer retryCount,
            @Qualifier("hiveTableMonitorTasklet") HiveTableMonitorTasklet tasklet) {
        log.info("init step1 with retry {}", retryCount);
        return steps.get("hiveTableMonitorStep").tasklet(tasklet.retry(retryCount)).build();
    }

    @Bean
    public HiveTableMonitorTasklet hiveTableMonitorTasklet() {
        return new HiveTableMonitorTasklet();
    }

    @Bean(name = "hiveDataSource")
    public DataSource hiveDatasource(@Value("${hiveURL}") String hiveURL, @Value("${hiveUser}") String hiveUser,
            @Value("${hivePassword}") String hivePassword) {
        log.info("init hive datasource url:{},user:{}", hiveURL, hiveUser);
        DataSource dataSource = new DataSource();
        dataSource.setUrl(hiveURL);
        dataSource.setUserName(hiveUser);
        dataSource.setPassword(hivePassword);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        return dataSource;
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

    @Bean
    public AtlasClientV2 atlasClient(@Value("${atlasURL}") String atlasURL, @Value("${atlasUser}") String atlasUser,
            @Value("${atlasPassword}") String atlasPassword) {
        log.info("init atlas client url:{}, user:{}", atlasURL, atlasUser);
        String[] baseUrl = atlasURL.split(",");
        String[] basicAuthUserNamePassword = new String[] { atlasUser, atlasPassword };
        AtlasClientV2 client = new AtlasClientV2(baseUrl, basicAuthUserNamePassword);
        return client;
    }

}
