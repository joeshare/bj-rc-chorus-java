package cn.rongcapital.chorus.server.datadev.controller.vo;

import cn.rongcapital.chorus.common.constant.JobType;
import cn.rongcapital.chorus.common.util.DateUtils;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class JobInfoVO {
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
     * 任务类型(1:一次性;2:周期)
     */
    private String labJobType;
    
    /**
     * 实例ID
     */
    private Long instanceId;
    
    /**
     * 发布状态(UNDEPLOY:未发布 DEPLOY:已发布 DELETE:删除)
     */
    private String status;
    
    /**
     * Spring XD DSL
     */
    private String workFlowDSL;
    
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
    
    /**
     * 告警信息
     */
    private String warningConfig;
    
    private ScheduleVO schedule;
    
    /**
     * Task列表
     */
    private List<TaskVO> taskList;
    
    // 输入源
    private String dataInput;
    //输出源
    private String dataOutput;
    
    public void setFormatTime(Date createTime) {
        this.createTime = DateUtils.format(createTime, DateUtils.FORMATER_SECOND);
    }
    
    public void setJobType(JobType type) {
        this.jobType = type.getCode();
        this.labJobType = type.getDesc();
    }
}
