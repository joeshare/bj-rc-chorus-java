package cn.rongcapital.chorus.das.entity.web;

public class TaskCause extends CommonCause {
	/**
	 * 业务主键
	 */
	private Integer taskId;

	/**
	 * 任务ID
	 */
	private int jobId;

	/**
	 * 步骤名
	 */
	private String taskName;

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public int getJobId() {
		return jobId;
	}

	public void setJobId(int id) {
		this.jobId = id;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

}
