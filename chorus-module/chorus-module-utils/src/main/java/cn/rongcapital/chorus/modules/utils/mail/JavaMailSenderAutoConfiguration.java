package cn.rongcapital.chorus.modules.utils.mail;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * @author yunzhong
 * @date 2017年5月16日下午3:19:29
 */
@Configuration
@Setter
@Slf4j
public class JavaMailSenderAutoConfiguration {

    @Bean
    public JavaMailSender javaMailServer(
            @Value("${mail.host}") String host,
            @Value("${mail.port}") int port,
            @Value("${mail.protocol}") String protocol,
            @Value("${mail.username}") String username,
            @Value("${mail.password}") String password
    ) {
        log.debug("init log sender.Send email to {} protocol {} of user {} ", host, protocol, username);
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
