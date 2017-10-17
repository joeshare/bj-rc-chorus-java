package cn.rongcapital.chorus.das.entity.web;

/**
 * 操作日志查询条件BEAN
 * 
 * @author lipeng
 *
 */
public class OperLogCause extends CommonCause {
	private String operId = null;

	private String fromTable = null;

	private String operLog = null;

	private String operUserId = null;

	private String createTime = null;

	private String updateTime = null;

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

	public String getRecordKey() {
		return recordKey;
	}

	public void setRecordKey(String recordKey) {
		this.recordKey = recordKey;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
}
