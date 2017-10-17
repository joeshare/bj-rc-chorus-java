package cn.rongcapital.chorus.common.email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yunzhong
 * @date 2017年5月16日下午5:04:15
 */
@Slf4j
@Component
public class MailSender {

    @Autowired
    private JavaMailSender sender;

    @Value("${mail.username}")
    private String fromAddr;

    /**
     * @param data
     * @author yunzhong
     * @version
     * @since 2017年5月16日
     */
    public void send(MailSenderModel data) {
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        try {
            helper.addTo(data.getToAddr());
            helper.setSubject(data.getSubject());
            helper.setText(data.getContext(), true);
            helper.setFrom(fromAddr);
            sender.send(mimeMessage);
        } catch (MessagingException e) {
            log.info("Failed to send email message to [" + data.getToAddr() + "]", e);
        }

    }

}
