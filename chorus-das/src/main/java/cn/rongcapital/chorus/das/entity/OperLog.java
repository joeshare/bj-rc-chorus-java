package cn.rongcapital.chorus.das.entity;

import java.util.Date;

public class OperLog {

	private String operId = null;

	private String fromTable = null;

	private String operLog = null;

	private String operUserId = null;

	private Date createTime = null;

	private Date updateTime = null;

	private String recordKey = null;

	public String getOperId() {
		return operId;
	}

	public void setOperId(String operId) {
		this.operId = operId;
	}

	public String getFromTable() {
		return fromTable;
	}

	public void setFromTable(String fromTable) {
		this.fromTable = fromTable;
	}

	public String getOperLog() {
		return operLog;
	}

	public void setOperLog(String operLog) {
		this.operLog = operLog;
	}

	public String getOperUserId() {
		return operUserId;
	}

	public void setOperUserId(String operUserId) {
		this.operUserId = operUserId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getRecordKey() {
		return recordKey;
	}

	public void setRecordKey(String recordKey) {
		this.recordKey = recordKey;
	}

}
