package cn.rongcapital.chorus.monitor.ranger;

import lombok.Data;

import java.util.Date;

@Data
public class RangerAuditModel {

    private String id;
    private String reqUser;
    private String resource;
    private Long policy;
    private String action;
    private Date evtTime;
    private String repo;
    private Integer result;
    private String resType;

    @Override
    public String toString() {
        return  "\r\nReqUser: " + reqUser + "\r\n" +
                "Resource: " + resource + "\r\n" +
                "Action: " + action + "\r\n" +
                "Repo: " + repo + "\r\n" +
                "EvtTime: " + evtTime + "\r\n" +
                "ResType: " + resType + "\r\n" +
                "Policy: " + policy;
    }
}