package cn.rongcapital.chorus.modules.email_notify;

import cn.rongcapital.chorus.modules.utils.mail.JavaMailSenderAutoConfiguration;
import cn.rongcapital.chorus.modules.utils.mail.MailSender;
import cn.rongcapital.chorus.modules.utils.mail.MailSenderImpl;
import org.apache.commons.io.FileUtils;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.Test;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MarkdownConverterTest {

    private final MarkdownConverter markdownConverter = new MarkdownConverter();

    @Test
    public void testConvert(){
        final String html = markdownConverter.convert("#this is context");
        assertNotNull(html);
        assertTrue(html.equals("<p><strong>text</strong></p>"));
    }

    @Test
    public void convertFileContent() throws Exception {
        System.out.println(markdownConverter.convert(FileUtils.readFileToString(new File("src/test/resources/content.md"))));
    }

    @Test
    public void sendMarddownMail() throws Exception {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        EnvironmentTestUtils.addEnvironment(
                applicationContext,
                "mail.host=smtp.exmail.qq.com",
                "mail.port=587",
                "mail.protocol=smtp",
                "mail.username=wuyimin@rongcapital.cn",
                "mail.password=pHoenix_718"
        );
        applicationContext.register(JavaMailSenderAutoConfiguration.class, MailSenderImpl.class, PropertyPlaceholderAutoConfiguration.class);
        applicationContext.refresh();

        final MailSender mailSender = applicationContext.getBean(MailSender.class);
        final String htmlMailContent = markdownConverter.convert(FileUtils.readFileToString(new File("src/test/resources/content.md")));

        mailSender.send("Chorus 内部测试，请直接回复邮件告诉您的文案的改进意见", htmlMailContent, "wuyimin@rongcapital.cn", "dp@rongcapital.cn");
    }
    
    @Test
    public void testUrlDecode(){
        String decode = null;
        MarkdownConverter converter = new MarkdownConverter();
        try {
            decode = URLDecoder.decode("Dear+All%EF%BC%9AChorus+%E5%8F%91%E7%89%88%E4%BF%A1%E6%81%AF%E5%A6%82%E4%B8%8B%EF%BC%8C%E8%AF%B7%E5%91%A8%E7%9F%A5%3A%0A%0A%23+release+notes+for+v0.6.0%0A%3E+%E4%B8%8A%E7%BA%BF%E8%BF%90%E7%BB%B4%E6%97%B6%E9%97%B4%EF%BC%9A%E9%A2%84%E8%AE%A1%E4%B8%8A%E7%BA%BF%E6%89%A7%E8%A1%8C%E6%97%B6%E9%97%B4%EF%BC%9A2017-09-28+19%3A00-21%3A00%5B%E4%BB%8A%E5%A4%A9%5D%EF%BC%9B%0A%23+%E5%BD%B1%E5%93%8D%E8%8C%83%E5%9B%B4%EF%BC%9AChorus+%E5%B9%B3%E5%8F%B0%E5%8F%AF%E8%83%BD%E6%97%A0%E6%B3%95%E7%99%BB%E9%99%86%E5%92%8C%E4%BD%BF%E7%94%A8%EF%BC%9B%E3%80%90Chorus%E6%9B%B4%E6%96%B0%E9%87%8D%E5%90%AF%E3%80%91%0A%23+%E7%BA%BF%E4%B8%8A%E6%AD%A3%E5%9C%A8%E6%89%A7%E8%A1%8C%E7%9A%84%E4%BB%BB%E5%8A%A1%E4%BC%9A%E4%B8%AD%E6%96%AD%EF%BC%9B%E5%90%84%E9%A1%B9%E7%9B%AE%E8%B4%9F%E8%B4%A3%E4%BA%BA%E8%AF%B7%E5%9C%A8%E4%B8%8A%E7%BA%BF%E7%BB%93%E6%9D%9F%E5%90%8E%EF%BC%8C%E5%85%B3%E6%B3%A8%E9%A1%B9%E7%9B%AE%E7%9A%84%E4%BB%BB%E5%8A%A1%E7%8A%B6%E6%80%81%EF%BC%9B%E6%9C%89%E4%BB%BB%E4%BD%95%E9%97%AE%E9%A2%98%E8%AF%B7%E8%81%94%E7%B3%BB%E6%88%91%E4%BB%AC%EF%BC%9B%E3%80%90SpringXD%E6%9B%B4%E6%96%B0%E9%87%8D%E5%90%AF%E3%80%91%0A%0A%E9%BB%98%E8%AE%A4%E8%AF%A5%E8%AE%A1%E5%88%92%E6%89%A7%E8%A1%8C%E9%A1%BA%E5%88%A9%EF%BC%8C%E6%88%91%E4%BB%AC%E4%B8%8D%E4%BC%9A%E5%86%8D%E5%8F%91%E9%80%81%E4%BB%BB%E4%BD%95%E9%82%AE%E4%BB%B6%E9%80%9A%E7%9F%A5%EF%BC%9B%0A%0A%E5%A6%82%E6%9C%89%E4%BB%BB%E4%BD%95%E8%AE%A1%E5%88%92%E5%A4%96%E7%9A%84%E6%83%85%E5%86%B5%E5%8F%91%E7%94%9F%E6%88%91%E4%BB%AC%E5%8F%8A%E6%97%B6%E4%BB%A5%E9%82%AE%E4%BB%B6%E6%96%B9%E5%BC%8F%E9%80%9A%E7%9F%A5%E5%A4%A7%E5%AE%B6%EF%BC%9B%E8%AF%B7%E7%90%86%E8%A7%A3%EF%BC%9B%0A%0A%E6%82%A8%E6%9C%89%E4%BB%BB%E4%BD%95%E7%96%91%E9%97%AE%E6%88%96%E8%80%85%E5%9B%B0%E6%83%91%E8%AF%B7%E7%9B%B4%E6%8E%A5%E8%81%94%E7%B3%BB%E6%88%91%E4%BB%AC%EF%BC%8C%E7%94%B5%E8%AF%9D%EF%BC%8CQQ%E6%88%96%E8%80%85%E9%82%AE%E4%BB%B6%E9%83%BD%E5%8F%AF%E4%BB%A5%EF%BC%9B%0A%0A%5Bchorus%5D%28http%3A%2F%2Fchorusweb.dataengine.com%29%0A%0A%E5%A4%9A%E8%B0%A2%EF%BC%81","UTF-8");
            final String html = converter.convert(decode);
            System.out.println(html);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(decode);
    }
}
