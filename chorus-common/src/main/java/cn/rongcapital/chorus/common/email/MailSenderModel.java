package cn.rongcapital.chorus.common.email;

import lombok.Data;

@Data
public class MailSenderModel {

    private String toAddr;
    private String context;
    private String subject;

}
