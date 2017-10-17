package cn.rongcapital.chorus.common;

import cn.rongcapital.chorus.common.hadoop.DefaultHadoopClient;
import cn.rongcapital.chorus.common.xd.DefaultXDClient;
import cn.rongcapital.chorus.common.xd.autonconfigure.SpringXDTemplateDecoratorAutoConfiguration;
import cn.rongcapital.chorus.common.xd.model.XDAdminConfig;
import cn.rongcapital.chorus.common.xd.service.impl.DslGraphServiceImpl;
import cn.rongcapital.chorus.common.xd.service.impl.XDRuntimeServiceImpl;
import cn.rongcapital.chorus.common.zk.DefaultZKClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.integration.annotation.Filter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Mybatis相关配置
 *
 * @author li.hzh
 * @date 2016-02-29
 */
@Configuration
@ComponentScan(basePackages = "cn.rongcapital.chorus.common",
        excludeFilters = {@ComponentScan.Filter(value =
                {DefaultHadoopClient.class, DefaultZKClient.class, SpringXDTemplateDecoratorAutoConfiguration.class,
                        DefaultXDClient.class, DslGraphServiceImpl.class, XDRuntimeServiceImpl.class},
                type = FilterType.ASSIGNABLE_TYPE)})
@EnableConfigurationProperties
public class TestConfig {

    @Value("${zookeeper.address}")
    private String address;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertiesResolver() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    @ConfigurationProperties(prefix = "xd")
    public XDAdminConfig xdAdminConfig() {
        XDAdminConfig xdAdminConfig = new XDAdminConfig();
        return xdAdminConfig;
    }

    @Bean
    @ConfigurationProperties(prefix = "mail")
    public JavaMailSender javaMailServer() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        Properties javaMailProperties = new Properties();
        javaMailProperties.setProperty("mail.smtp.auth", "true");
        javaMailProperties.setProperty("mail.smtp.timeout", "25000");
        sender.setJavaMailProperties(javaMailProperties);
        return sender;
    }
}
