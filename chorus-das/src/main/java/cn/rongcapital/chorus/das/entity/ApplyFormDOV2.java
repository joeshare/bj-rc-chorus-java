package cn.rongcapital.chorus.das.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ApplyFormDOV2 {
    private Long applyFormId;

    private String tableInfoId;

    //增加表名称
    private String tableName;

    private String applyUserId;

    //增加提交人名称
    private String applyUserName;

    private Date applyTime;

    private String reason;

    private String dealUserId;

    //增加审批人名称
    private String dealUserName;
   //审批说明
    private String dealInstruction;

    private Date dealTime;

    private Date endDate;

    private String statusCode;

    private String projectName;
}
