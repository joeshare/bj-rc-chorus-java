package cn.rongcapital.chorus.test.monitor.ranger.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Properties;

/**
 * @author yunzhong
 * @date 2017年5月16日下午3:19:29
 */
@Configuration
public class MailConfig {

    /**
     * mail sender
     * 
     * @return
     * @author yunzhong
     * @version
     * @since 2017年5月16日
     */
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

    /**
     * @return
     * @author yunzhong
     * @version
     * @since 2017年5月17日
     */
    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        String templateLoaderPath = "classpath:/ftltemplates/";
        configurer.setTemplateLoaderPath(templateLoaderPath);
        Properties settings = new Properties();
        settings.setProperty("output_encoding", "UTF-8");
        settings.setProperty("locale", "zh_CN");
        settings.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
        configurer.setFreemarkerSettings(settings);
        configurer.setDefaultEncoding("UTF-8");
        return configurer;
    }
}
