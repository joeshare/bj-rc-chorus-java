package cn.rongcapital.chorus.das.entity;

import java.util.Date;
/**
 * @author Li.Zhiwei
 */
public class JobMonitor {
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
    private Date jobStartTime;
    /**
     * 执行结束时间
     */
    private Date jobStopTime;
    /**
     * 处理数据详情
     */
    private String detailInfo;
    /**
     * 模块类型 1:默认Job 2:用户自定义Job 3:默认Stream source 4:用户自定义Stream source 5:默认Stream processor 6:用自定义Stream processor 7:默认Stream sink 8:用户自定义Stream sink 9:默认Stream other 10:用户自定义Stream other 11:job definition
     */
    private int moduleType;
    /**
     * Spring XD定义模块名
     */
    private String moduleName;
    /**
     * 异常日志信息
     */
     private String logInfo;

    public Integer getJobId() {
        return jobId;
    }

    public Long getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(Long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public Long getJobInstanceId() {
        return jobInstanceId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public void setJobInstanceId(Long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    public String getJobAliasName() {
        return jobAliasName;
    }

    public void setJobAliasName(String jobAliasName) {
        this.jobAliasName = jobAliasName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobExecuteStatus() {
        return jobExecuteStatus;
    }

    public void setJobExecuteStatus(String jobExecuteStatus) {
        this.jobExecuteStatus = jobExecuteStatus;
    }

    public Date getJobStartTime() {
        return jobStartTime;
    }

    public void setJobStartTime(Date jobStartTime) {
        this.jobStartTime = jobStartTime;
    }

    public Date getJobStopTime() {
        return jobStopTime;
    }

    public void setJobStopTime(Date jobStopTime) {
        this.jobStopTime = jobStopTime;
    }

    public String getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(String detailInfo) {
        this.detailInfo = detailInfo;
    }

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public int getModuleType() {
        return moduleType;
    }

    public void setModuleType(int moduleType) {
        this.moduleType = moduleType;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

}
