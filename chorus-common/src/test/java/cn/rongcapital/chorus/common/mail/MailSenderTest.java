package cn.rongcapital.chorus.common.mail;

import cn.rongcapital.chorus.common.BasicSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.rongcapital.chorus.common.email.MailSender;
import cn.rongcapital.chorus.common.email.MailSenderModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MailSenderTest extends BasicSpringTest {

    @Autowired
    private MailSender mailSender;

    /**
     * 
     * @author yunzhong
     * @version
     * @since 2017年5月22日
     */
    @Test
    public void testSendEmail() {
        MailSenderModel data = new MailSenderModel();
        data.setContext("这是测试信息");
        data.setSubject("测试");
        data.setToAddr("lihongzhe@rongcapital.cn");
        mailSender.send(data);
        log.info("wait send mail.");
    }
}
