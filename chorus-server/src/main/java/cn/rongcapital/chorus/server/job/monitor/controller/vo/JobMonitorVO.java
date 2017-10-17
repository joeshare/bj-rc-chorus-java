package cn.rongcapital.chorus.server.job.monitor.controller.vo;

import java.util.Date;

import cn.rongcapital.chorus.common.util.DateUtils;
import lombok.Data;

@Data
public class JobMonitorVO {
    /**
     * 任务Id
     */
    private Integer jobId;
    /**
     * 执行Id
     */
    private Long jobExecutionId;
    /**
     * 任务实例Id
     */
    private Long jobInstanceId;
    /**
     * 任务名(uuid)
     */
    private String jobName;
    /**
     * 任务名(显示)
     */
    private String jobAliasName;
    /**
     * 任务说明(job表remark)
     */
    private String jobDescription;
    /**
     * 任务实例执行状态
     */
    private String jobExecuteStatus;
    /**
     * 任务状态
     */
    private String jobStatus;
    /**
     * 执行开始时间
     */
    private String jobStartTime;
    /**
     * 执行结束时间
     */
    private String jobStopTime;
    /**
     * 模块类型 1:默认Job 2:用户自定义Job 3:默认Stream source 4:用户自定义Stream source 5:默认Stream processor 6:用自定义Stream processor 7:默认Stream sink 8:用户自定义Stream sink 9:默认Stream other 10:用户自定义Stream other 11:job definition
     */
    private int moduleType;
    /**
     * Spring XD定义模块名
     */
    private String moduleName;

    public void setFormatTime(Date jobStartTime, Date jobStopTime) {
        this.jobStartTime = DateUtils.format(jobStartTime, DateUtils.FORMATER_SECOND);
        this.jobStopTime = DateUtils.format(jobStopTime, DateUtils.FORMATER_SECOND);
    }
}
