package cn.rongcapital.chorus.das.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ResourceOutDO {

    private Long resourceOutId;
    private Long projectId;
    private String resourceName;
    private String resourceSource;
    private String resourceUsage;
    private String resourceDesc;
    private String storageType;
    private String connUrl;
    private String connPort;
    private String connUser;
    private String connPass;
    private String createUserId;
    private Date createTime;
    private String updateUserId;
    private Date updateTime;
    private Date endDate;
    private String statusCode;
    private String databaseName;
    private String userName;
    private String usageName;
    private String typeName;
    private String connHost;
    private String createUserName;
    private String guid;
}
