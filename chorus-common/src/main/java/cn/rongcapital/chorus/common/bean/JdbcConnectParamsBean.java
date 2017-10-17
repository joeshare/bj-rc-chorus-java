package cn.rongcapital.chorus.common.bean;

public class JdbcConnectParamsBean {
	
	private String driver;
	
	private String url;

	private String user;
	
	private String password;
	
	private String tableName;

	/**
	 * 数据库名
	 */
	private String dbName;
	
	private String whereStr = "";

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getWhereStr() {
		return whereStr;
	}

	public void setWhereStr(String whereStr) {
		this.whereStr = whereStr;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * 数据库名
	 */
	public String getDbName() {
		return dbName;
	}
	
	/**
	 * 数据库名
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

}
