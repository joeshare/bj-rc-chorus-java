package cn.rongcapital.chorus.das.entity;

/**
 * execute_history表映射BEAN
 *
 * @author maboxiao
 */
public class ExecuteHistory extends CommonEntity {

	/**
	 * 执行历史ID
	 */
	private Long executeHistoryId;
	/**
	 * 执行状态(-1:执行失败,0:执行中,1:执行成功)
	 */
	private int executeStatus;
	/**
	 * 执行SQL
	 */
	private String executeSql;
	/**
	 * 执行耗时
	 */
	private Long executeTime;
	
	public Long getExecuteHistoryId() {
		return executeHistoryId;
	}
	public void setExecuteHistoryId(Long executeHistoryId) {
		this.executeHistoryId = executeHistoryId;
	}
	public int getExecuteStatus() {
		return executeStatus;
	}
	public void setExecuteStatus(int executeStatus) {
		this.executeStatus = executeStatus;
	}
	public String getExecuteSql() {
		return executeSql;
	}
	public void setExecuteSql(String executeSql) {
		this.executeSql = executeSql;
	}
	public Long getExecuteTime() {
		return executeTime;
	}
	public void setExecuteTime(Long executeTime) {
		this.executeTime = executeTime;
	}
}
