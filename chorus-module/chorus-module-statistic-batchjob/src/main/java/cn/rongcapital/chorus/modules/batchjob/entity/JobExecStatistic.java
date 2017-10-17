package cn.rongcapital.chorus.modules.batchjob.entity;

import java.util.Date;

/**
 * job_exec_statistic表实体类
 * @author kevin.gong
 * @Time 2017年6月21日 上午9:45:12
 */
public class JobExecStatistic extends CommonEntity {

	private Long id;
	
	private Long projectId;
	
	private String jobName;
	
	private String jobAliasName;
	
	private Integer maxDuration;
	
	private Float avgDuration;
	
	private Integer completedNum;
	
	private Integer failedNum;
	
	private Integer runningNum;
	
	private Integer currentRunningNum;
	
	private Date currentRunningTime;
	
	private String date;
	
	private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobAliasName() {
        return jobAliasName;
    }

    public void setJobAliasName(String jobAliasName) {
        this.jobAliasName = jobAliasName;
    }

    public Integer getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(Integer maxDuration) {
        this.maxDuration = maxDuration;
    }

    public Float getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(Float avgDuration) {
        this.avgDuration = avgDuration;
    }

    public Integer getCompletedNum() {
        return completedNum;
    }

    public void setCompletedNum(Integer completedNum) {
        this.completedNum = completedNum;
    }

    public Integer getFailedNum() {
        return failedNum;
    }

    public void setFailedNum(Integer failedNum) {
        this.failedNum = failedNum;
    }

    public Integer getRunningNum() {
        return runningNum;
    }

    public void setRunningNum(Integer runningNum) {
        this.runningNum = runningNum;
    }
    
    public Integer getCurrentRunningNum() {
        return currentRunningNum;
    }

    public void setCurrentRunningNum(Integer currentRunningNum) {
        this.currentRunningNum = currentRunningNum;
    }

    public Date getCurrentRunningTime() {
        return currentRunningTime;
    }

    public void setCurrentRunningTime(Date currentRunningTime) {
        this.currentRunningTime = currentRunningTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
