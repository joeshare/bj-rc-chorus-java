package cn.rongcapital.chorus.modules.utils.mail;

import org.junit.Test;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author yimin
 */
public class MailSenderTest {
    @Test
    public void send() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        EnvironmentTestUtils.addEnvironment(context,
                                            "mail.host=smtp.exmail.qq.com",
                                            "mail.port=587",
                                            "mail.protocol=smtp",
                                            "mail.username=chorus@rongcapital.cn",
                                            "mail.password=Chorus123",
                                            "mail.from=chorus@rongcapital.cn");
        context.register(JavaMailSenderAutoConfiguration.class, MailSenderImpl.class, PropertyPlaceholderAutoConfiguration.class);
        context.refresh();
        MailSenderImpl mailSender = context.getBean(MailSenderImpl.class);
        String from = context.getEnvironment().getProperty("mail.from");
        System.out.println(from);
        mailSender.send("Test", "This is content just for test", from, "wuyimin@rongcapital.cn");
    }
}
