package cn.rongcapital.chorus.modules.utils.mail;

import javax.mail.MessagingException;

/**
 * @author yimin
 */
public interface MailSender {
    
    public void send(String subject, String text, String from, String... to) throws MessagingException;
}
