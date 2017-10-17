package cn.rongcapital.chorus.das.entity;

import java.util.Date;

/**
 * stream_exec_statistic表实体类
 * @author kevin.gong
 * @Time 2017年6月21日 上午9:44:38
 */
public class StreamExecStatistic extends CommonEntity {

	private Long id;
	
	private Long projectId;
	
	private Integer noExecNum;
	
	private Integer failedNum;
	
	private Integer runningNum;
	
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

    public Integer getNoExecNum() {
        return noExecNum;
    }

    public void setNoExecNum(Integer noExecNum) {
        this.noExecNum = noExecNum;
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
