package cn.rongcapital.chorus.das.entity;

import lombok.Data;

import java.util.Date;

/**
 * Created by abiton on 21/11/2016.
 */
@Data
public class ResourceOperationDO {
    private Integer operationId;
    private Integer resourceId;
    private Integer projectId;
    private String projectName;
    private String createUserId;
    private String createUserName;
    private Date createTime;
    private String updateUserId;
    private String updateUserName;
    private Date updateTime;
    private String reason;
    private String statusCode;
    private String statusDesc;
    private String notes;
    private Integer cpu;
    private Integer memory;
    private Integer storage;
}
