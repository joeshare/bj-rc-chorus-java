package cn.rongcapital.chorus.das.entity.caas;

import lombok.Data;

@Data
public class CaasRole {
    private String appCode;
    private String appName;
    private String code;
    private String comment;
    private String creationTime;
    private String creationUser;
    private String name;
    private Boolean removed;
    private String roleType;
    private String subjectCode;
    private Integer version;
}
