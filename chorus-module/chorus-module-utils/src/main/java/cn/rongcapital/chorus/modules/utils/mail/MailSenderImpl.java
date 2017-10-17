package cn.rongcapital.chorus.modules.utils.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author yunzhong
 * @date 2017年5月16日下午5:04:15
 */
@Component
public class MailSenderImpl implements MailSender {

    @Autowired
    private JavaMailSender sender;

    /**
     * @param subject
     * @param text
     * @param from
     * @param to
     *
     * @author yunzhong
     * @version
     * @throws MessagingException 
     * @since 2017年5月16日
     */
    public void send(String subject, String text, String from, String... to) throws MessagingException {
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        helper.setFrom(from);
        sender.send(mimeMessage);

    }

}
