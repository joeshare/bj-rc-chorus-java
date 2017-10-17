package cn.rongcapital.chorus.server.resourceoperate.vo;

import lombok.Data;

import java.util.Date;

/**
 * Created by abiton on 18/11/2016.
 */
@Data
public class ResourceOperationVO {
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
