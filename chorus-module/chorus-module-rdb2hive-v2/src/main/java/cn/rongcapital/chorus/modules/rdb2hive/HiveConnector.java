package cn.rongcapital.chorus.modules.rdb2hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HiveConnector {

	private static Logger logger = LoggerFactory.getLogger(HiveConnector.class);

	/**
	 * 返回结果集
	 * @param dbName
	 * @param hql
	 * @return
	 * @throws SQLException
	 */
	public static ResultSet excuteHiveSql(String dbName, String hql,String hiveSerUrl,String dbuser) throws SQLException {
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = getHiveConn(dbName,dbuser,hiveSerUrl);
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(hql);
		} catch (Exception ex) {
			return null;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return rs;
	}

	/**
	 * 执行sql 泛用
	 * @param dbName
	 * @param hql
	 * @return
	 * @throws SQLException
	 */
	public static int excuteHiveSqlNoRes(String dbName, String hql,String hiveSerUrl,String dbuser) throws SQLException {
		Connection conn = null;
		try {
			conn = getHiveConn(dbName,dbuser,hiveSerUrl);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(hql);
		} catch (Exception ex) {
			logger.error(ex.toString());
			ex.printStackTrace();
			throw new IllegalStateException(ex.toString());
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return 0;
	}

	/**
	 * 获取conn 连接 by 用户 
	 * @param dbName
	 * @param hiveUser
	 * @return
	 * @throws SQLException
	 */
	private static Connection getHiveConn(String dbName, String hiveUser,String hiveSerUrl) throws SQLException {
		try {
			Class.forName("org.apache.hive.jdbc.HiveDriver");
		} catch (ClassNotFoundException e) {
			logger.error(e.toString());
			e.printStackTrace();
			throw new IllegalStateException(e.toString());
		}
		return DriverManager.getConnection(hiveSerUrl + dbName, hiveUser, "");
	}


}
