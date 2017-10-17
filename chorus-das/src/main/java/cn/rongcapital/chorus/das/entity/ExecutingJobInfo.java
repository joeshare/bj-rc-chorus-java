package cn.rongcapital.chorus.das.entity;

/**
 * 正在执行任务信息
 * @author kevin.gong
 * @Time 2017年8月10日 上午11:08:03
 */
public class ExecutingJobInfo {

    /**
     * 任务名称
     */
    private String jobName;
    
    /**
     * 任务类型（1：流式 2：批量）
     */
    private int jobType;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 任务实例ID
     */
    private Integer jobInstanceId;
    
    /**
     * 任务执行ID
     */
    private Integer jobExecutionId;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 负责人
     */
    private String createUserName;
    
    /**
     * 任务执行开始时间
     */
    private String jobStartTime;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public int getJobType() {
        return jobType;
    }

    public void setJobType(int jobType) {
        this.jobType = jobType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getJobInstanceId() {
        return jobInstanceId;
    }

    public void setJobInstanceId(Integer jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    public Integer getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(Integer jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getJobStartTime() {
        return jobStartTime;
    }

    public void setJobStartTime(String jobStartTime) {
        this.jobStartTime = jobStartTime;
    }
}
