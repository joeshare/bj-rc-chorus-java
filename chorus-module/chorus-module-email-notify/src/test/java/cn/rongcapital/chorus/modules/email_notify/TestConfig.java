package cn.rongcapital.chorus.modules.email_notify;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import cn.rongcapital.chorus.modules.utils.mail.MailSender;
import cn.rongcapital.chorus.modules.utils.mail.MailSenderImpl;

@Configuration
@EnableConfigurationProperties
public class TestConfig {

    @Bean(name = "chorusDataSource")
    public DataSource chorusDatasource() {
        DataSource dataSource = new DataSource();
        String chorusURL = "jdbc:mysql://10.200.48.79:3306/chorus";
        dataSource.setUrl(chorusURL);
        String chorusUser = "dps";
        dataSource.setUserName(chorusUser);
        String chorusPassword = "Dps@10.200.48.MySQL";
        dataSource.setPassword(chorusPassword);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        return dataSource;
    }

    @Bean
    public EmailSenderTasklet emailSenderTasklet( @Value("${mail.username}") String username) {
         final EmailSenderTasklet emailSenderTasklet = new EmailSenderTasklet();
         emailSenderTasklet.setTitle("test");
         emailSenderTasklet.setContent("<p>尊敬的用户：<b>wangyunzhong</b>.</p><p>任务执行时间：2017-09-14 20:07:11 ~ 2017-09-14 22:29:51</p>");
         emailSenderTasklet.setFrom(username);
         return emailSenderTasklet;
    }

    @Bean
    public MailSender mailSender() {
        return new MailSenderImpl();
    }

    @Bean
    public JavaMailSender javaMailServer(@Value("${mail.host}") String host, @Value("${mail.port}") int port,
            @Value("${mail.protocol}") String protocol, @Value("${mail.username}") String username,
            @Value("${mail.password}") String password) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        Properties javaMailProperties = new Properties();
        javaMailProperties.setProperty("mail.smtp.auth", "true");
        javaMailProperties.setProperty("mail.smtp.timeout", "25000");
        sender.setJavaMailProperties(javaMailProperties);
        sender.setHost(host);
        sender.setPort(port);
        sender.setProtocol(protocol);
        sender.setUsername(username);
        sender.setPassword(password);
        return sender;
    }
}
