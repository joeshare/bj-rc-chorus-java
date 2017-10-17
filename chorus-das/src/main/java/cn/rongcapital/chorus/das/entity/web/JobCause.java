package cn.rongcapital.chorus.das.entity.web;

import cn.rongcapital.chorus.das.entity.Schedule;

import java.util.Date;
import java.util.Map;

public class JobCause extends CommonCause {

    /**
     * 创建用户ID
     */
    private String createUser;

    /**
     * 任务创建人名
     */
    private String createUserName;

    /**
     * 是否系统管理员
     */
    private String sysUserYn;

    /**
     * 任务ID
     */
    private Integer jobId;

    /**
     * 任务类型(1:实时 2:定期)
     */
    private int jobType;

    /**
     * 任务运行名称.
     * 前端自动生成uuid,必须英文和下划线
     */
    private String jobName;

    /**
     * 实例ID
     */
    private Long instanceId;

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 数据更新时间
     */
    private Date updateTime;

    /**
     * 更新者用户ID
     */
    private String updateUser;

    /**
     * 更新者用户名
     */
    private String updateUserName;
    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 任务步骤名
     */
    private String taskName;

    /**
     * 任务名(显示)
     */
    private String jobAliasName;

    /**
     * 任务步骤ID
     */
    private Integer taskId;

    /**
     * 发布状态(UNDEPLOY:未发布 DEPLOY:已发布 DELETE:删除)
     */
    private String status;

    /**
     * 任务负责人ID
     */
    private String deployUserId;

    /**
     * 任务负责人名
     */
    private String deployUserName;

    /**
     * Task数据
     */
    private Schedule schedule;

    /**
     * 告警信息
     */
    private String warningConfig;
    /**
     * 任务执行参数 JSON 格式的任务参数
     */
    private Map<String, Map<String, String>> jobParameters;

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getSysUserYn() {
        return sysUserYn;
    }

    public void setSysUserYn(String sysUserYn) {
        this.sysUserYn = sysUserYn;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getJobType() {
        return jobType;
    }

    public void setJobType(int jobType) {
        this.jobType = jobType;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getJobAliasName() {
        return jobAliasName;
    }

    public void setJobAliasName(String jobAliasName) {
        this.jobAliasName = jobAliasName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getDeployUserId() {
        return deployUserId;
    }

    public void setDeployUserId(String deployUserId) {
        this.deployUserId = deployUserId;
    }

    public String getDeployUserName() {
        return deployUserName;
    }

    public void setDeployUserName(String deployUserName) {
        this.deployUserName = deployUserName;
    }

    public Schedule getSchedule() {
        if (schedule == null) {
            schedule = new Schedule();
        }
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public String getWarningConfig() {
        return warningConfig;
    }

    public void setWarningConfig(String warningConfig) {
        this.warningConfig = warningConfig;
    }

    public Map<String, Map<String, String>> getJobParameters() {
        return jobParameters;
    }

    public void setJobParameters(Map<String, Map<String, String>> jobParameters) {
        this.jobParameters = jobParameters;
    }
}
