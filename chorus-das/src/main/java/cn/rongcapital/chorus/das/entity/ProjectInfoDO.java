package cn.rongcapital.chorus.das.entity;

import java.util.Date;

import lombok.Data;
import lombok.ToString;

/**
 * Created by shicheng on 2016/11/25.
 */
@Data
@ToString
public class ProjectInfoDO {

    private Long projectMemberId;
    private Long projectId;
    private String projectCode;
    private String projectName;
    private String projectDesc;
    private String projectManagerId;
    private String managerTelephone;
    private String createUserId;
    private String userId;
    private String roleId;
    private String roleCode;
    private String roleName;
    private String userName;
    private String email;
    private Date createTime;
    private String caasTopicId;
    private String statusCode;

}
