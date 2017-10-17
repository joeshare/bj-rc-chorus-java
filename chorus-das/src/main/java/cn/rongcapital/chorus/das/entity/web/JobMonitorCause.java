package cn.rongcapital.chorus.das.entity.web;

/**
 * @author Li.ZhiWei
 */
public class JobMonitorCause extends CommonCause {

    private String instanceId;
    private String executionId;
    private String startTime;
    private String jobAliasName;
    private Long[] projectIds;
    private String[] executionStatus;
    private int jobId;
    private String userId;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getJobAliasName() {
        return jobAliasName;
    }

    public void setJobAliasName(String jobAliasName) {
        this.jobAliasName = jobAliasName;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Long[] getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(Long[] projectIds) {
        this.projectIds = projectIds;
    }

    public String[] getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(String[] executionStatus) {
        this.executionStatus = executionStatus;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
