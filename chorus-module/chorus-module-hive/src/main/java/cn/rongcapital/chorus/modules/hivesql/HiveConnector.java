package cn.rongcapital.chorus.modules.hivesql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HiveConnector {
		
	private static Logger logger = LoggerFactory.getLogger(HiveConnector.class);
	
	/**
	 * 执行sql 泛用
	 * @param queueName
	 * @param hql
	 * @param hiveServerUrl
	 * @param hiveUser
	 * @return
	 * @throws SQLException
	 */
	public static int excuteHiveSqlNoRes(String queueName, String hql, String hiveServerUrl, String hiveUser) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getHiveConn(queueName, hiveUser, hiveServerUrl);
			stmt = conn.createStatement();
			stmt.executeUpdate(hql);
		} catch (Exception e) {
			logger.error("", e);
			throw e;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		return 0;
	}
	
	/**
	 * 获取conn 连接 by 用户 
	 * @param queueName
	 * @param hiveUser
	 * @param hiveServerUrl
	 * @return
	 * @throws SQLException
	 */
	private static Connection getHiveConn(String queueName, String hiveUser, String hiveServerUrl) throws Exception {
		try {
			Class.forName("org.apache.hive.jdbc.HiveDriver");
		} catch (ClassNotFoundException e) {
			logger.error("", e);
			throw e;
		}
		return DriverManager.getConnection(hiveServerUrl + "?tez.queue.name=" + queueName, hiveUser, "");
	}
}
