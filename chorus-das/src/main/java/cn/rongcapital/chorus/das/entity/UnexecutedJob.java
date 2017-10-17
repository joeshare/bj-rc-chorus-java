package cn.rongcapital.chorus.das.entity;

/**
 * 未执行任务信息
 * @author kevin.gong
 * @Time 2017年9月20日 下午5:12:43
 */
public class UnexecutedJob {
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务描述
     */
    private String description;
    /**
     * 项目
     */
    private String projectName;
    /**
     * 任务负责人
     */
    private String createUserName;
    /**
     * 任务执行时间（应执行时间）
     */
    private String scheExecTime;
    
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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
    public String getScheExecTime() {
        return scheExecTime;
    }
    public void setScheExecTime(String scheExecTime) {
        this.scheExecTime = scheExecTime;
    }
}