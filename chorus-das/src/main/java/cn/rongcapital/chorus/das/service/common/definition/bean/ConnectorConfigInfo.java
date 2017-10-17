package cn.rongcapital.chorus.das.service.common.definition.bean;

/**
 * 连接器配置信息
 *
 * @author maboxiao
 * @date 2016-04-18
 */
public class ConnectorConfigInfo {
	
	/**
	 * PRESTO服务器URL
	 */
	private String prestoServerUrl;
	
	/**
	 * HIVE用户名
	 */
	private String hiveUser;

	/**
	 * warehouseHome
	 */
	private String warehouseHome;

	public String getPrestoServerUrl() {
		return prestoServerUrl;
	}

	public void setPrestoServerUrl(String prestoServerUrl) {
		this.prestoServerUrl = prestoServerUrl;
	}

	public String getHiveUser() {
		return hiveUser;
	}

	public void setHiveUser(String hiveUser) {
		this.hiveUser = hiveUser;
	}

	public String getWarehouseHome() {
		return warehouseHome;
	}

	public void setWarehouseHome(String warehouseHome) {
		this.warehouseHome = warehouseHome;
	}
}
