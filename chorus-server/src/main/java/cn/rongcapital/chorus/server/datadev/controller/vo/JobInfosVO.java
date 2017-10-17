package cn.rongcapital.chorus.server.datadev.controller.vo;

import cn.rongcapital.chorus.common.util.DateUtils;
import lombok.Data;

import java.util.Date;

@Data
public class JobInfosVO {
    /**
     * 任务ID
     */
    private Integer jobId;

    /**
     * 任务运行名称.
     * 前端自动生成uuid,必须英文和下划线
     */
    private String jobName;

    /**
     * 任务名(显示)
     */
    private String jobAliasName;

    /**
     * 任务类型(1:实时 2:定期)
     */
    private int jobType;

    /**
     * 发布状态(UNDEPLOY:未发布 DEPLOY:已发布 DELETE:删除)
     */
    private String status;

    /**
     * 描述
     */
    private String description;

    /**
     * 任务负责人ID
     */
    private String deployUserId;

    /**
     * 任务负责人名
     */
    private String deployUserName;

    /**
     * 任务创建人名
     */
    private String createUserName;
    /**
     * 创建用户ID
     */
    private String createUser;

    /**
     * 数据插入时间
     */
    private String createTime;

    public void setFormatTime(Date createTime) {
        this.createTime = DateUtils.format(createTime, DateUtils.FORMATER_SECOND);
    }



}
