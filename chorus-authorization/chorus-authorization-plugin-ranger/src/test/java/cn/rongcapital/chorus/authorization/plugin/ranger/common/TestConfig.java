package cn.rongcapital.chorus.authorization.plugin.ranger.common;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import cn.rongcapital.chorus.common.hadoop.DefaultHadoopClient;
import cn.rongcapital.chorus.common.xd.DefaultXDClient;
import cn.rongcapital.chorus.common.xd.autonconfigure.SpringXDTemplateDecoratorAutoConfiguration;
import cn.rongcapital.chorus.common.xd.service.impl.DslGraphServiceImpl;
import cn.rongcapital.chorus.common.xd.service.impl.XDRuntimeServiceImpl;
import cn.rongcapital.chorus.common.zk.DefaultZKClient;

@Configuration
@ComponentScan(basePackages = {"cn.rongcapital.chorus.common","cn.rongcapital.chorus.authorization"},
        excludeFilters = {@ComponentScan.Filter(value =
                {DefaultHadoopClient.class, DefaultZKClient.class, SpringXDTemplateDecoratorAutoConfiguration.class,
                        DefaultXDClient.class, DslGraphServiceImpl.class, XDRuntimeServiceImpl.class},
                type = FilterType.ASSIGNABLE_TYPE)})
@EnableConfigurationProperties
public class TestConfig {
    
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
