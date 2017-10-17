package cn.rongcapital.chorus.das.entity;

public class CalculateSQL {

	/**
	 * SQL
	 */
	private String sql;

	/**
	 * 当前项目编码
	 */
	private String currentProjectCode;

	/**
	 * SQL
	 * @return
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * SQL
	 * @param sql
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getCurrentProjectCode() {
		return currentProjectCode;
	}

	public void setCurrentProjectCode(String currentProjectCode) {
		this.currentProjectCode = currentProjectCode;
	}
}
