package cn.rongcapital.chorus.server.project.controller.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Created by shicheng on 2016/11/25.
 */
@Data
@ToString
public class ProjectInfoVo {

    private Long projectId;
    private String projectCode;
    private String projectName;
    private String projectDesc;
    private String projectManagerId;
    private String managerTelephone;
    private String createUserId;
    private Date createTime;
    private String updateUserId;
    private Date updateTime;
    private Long caasTopicId; // CAAS 系统对接字段
    private String userName;
    private String statusCode;

}
