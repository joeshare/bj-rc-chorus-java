package cn.rongcapital.chorus.common.util;

import cn.rongcapital.chorus.common.bean.JdbcConnectParamsBean;
import org.apache.commons.lang3.StringUtils;

public class ConnectorInfoUtils {

	public static final String DB_TYPE_MYSQL = "MYSQL";

	public static final String DB_TYPE_SQLSERVER = "SQLSERVER";

	public static final String DB_TYPE_POSTGRESQL = "POSTGRESQL";

	public static final String DB_TYPE_ORACLE = "ORACLE";
	
	public static final String DB_TYPE_ELASTICSEARCH = "ELASTICSEARCH";

	public static final String DB_DRIVER_SQLSERVER = "net.sourceforge.jtds.jdbc.Driver";

	public static final String DB_DRIVER_MYSQL = "com.mysql.jdbc.Driver";

	public static final String DB_DRIVER_POSTGRESQL = "org.postgresql.Driver";

	public static final String DB_DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver";

	/**
	 * JDBC连接信息生成
	 * 
	 * @param dbType
	 * @param dbIp
	 * @param dbPort
	 * @param dbName
	 * @param dbUser
	 * @param dbPWD
	 * @param dbTableName
	 * @param conWhere
	 * @return
	 */
	public static JdbcConnectParamsBean getJdbcConnectParams(String dbType, String dbIp, String dbPort, String dbName,
															 String dbUser, String dbPWD, String dbTableName, String conWhere) {
		JdbcConnectParamsBean conParm = new JdbcConnectParamsBean();
		if (DB_TYPE_SQLSERVER.equalsIgnoreCase(dbType)) {
			conParm.setDriver(DB_DRIVER_SQLSERVER);
			conParm.setUrl("jdbc:jtds:sqlserver://" + dbIp + ":" + dbPort + "/" + dbName);
		} else if (DB_TYPE_POSTGRESQL.equalsIgnoreCase(dbType)) { // PostgreSQL
			conParm.setDriver(DB_DRIVER_POSTGRESQL);
			conParm.setUrl("jdbc:postgresql://" + dbIp + ":" + dbPort + "/" + dbName);
		} else if (DB_TYPE_ORACLE.equalsIgnoreCase(dbType)) { // PostgreSQL
			conParm.setDriver(DB_DRIVER_ORACLE);
			conParm.setUrl("jdbc:oracle:thin:@" + dbIp + ":" + dbPort + ":" + dbName);
		} else { // mysql
			conParm.setDriver(DB_DRIVER_MYSQL);
			conParm.setUrl("jdbc:mysql://" + dbIp + ":" + dbPort + "/" + dbName
					+ "?useUnicode=true&amp;characterEncoding=utf-8");
		}
		conParm.setUser(dbUser);
		conParm.setPassword(dbPWD);
		conParm.setDbName(dbName);
		if (StringUtils.isNotBlank(dbTableName)) {
			conParm.setTableName(dbTableName);
		}
		if (StringUtils.isNotEmpty(conWhere)) {
			// conWhere = "";
			conParm.setWhereStr(conWhere);
		}

		return conParm;
	}
	
	/**
	 * JDBC连接信息生成
	 * 
	 * @param url
	 * @param dbType
	 * @param dbUser
	 * @param dbPWD
	 * @param dbTableName
	 * @param conWhere
	 * @return
	 */
	public static JdbcConnectParamsBean getJdbcConnectParams(String url, String dbType,
			String dbUser, String dbPWD, String dbTableName, String conWhere) {
		JdbcConnectParamsBean conParm = new JdbcConnectParamsBean();
		if (DB_TYPE_SQLSERVER.equalsIgnoreCase(dbType)) {
			conParm.setDriver(DB_DRIVER_SQLSERVER);
			conParm.setUrl(url);
		} else if (DB_TYPE_POSTGRESQL.equalsIgnoreCase(dbType)) { // PostgreSQL
			conParm.setDriver(DB_DRIVER_POSTGRESQL);
			conParm.setUrl(url);
		} else if (DB_TYPE_ORACLE.equalsIgnoreCase(dbType)) { // PostgreSQL
			conParm.setDriver(DB_DRIVER_ORACLE);
			conParm.setUrl(url);
		} else { // mysql
			conParm.setDriver(DB_DRIVER_MYSQL);
			conParm.setUrl(url + "?useUnicode=true&amp;characterEncoding=utf-8");
		}
		conParm.setUser(dbUser);
		conParm.setPassword(dbPWD);
		
		String dbName = "";
		if (url.lastIndexOf("/") != -1) {
			dbName = url.substring(url.lastIndexOf("/") + 1, url.length());
		}
		conParm.setDbName(dbName);
		if (StringUtils.isNotBlank(dbTableName)) {
			conParm.setTableName(dbTableName);
		}
		if (StringUtils.isNotEmpty(conWhere)) {
			// conWhere = "";
			conParm.setWhereStr(conWhere);
		}

		return conParm;
	}

	/**
	 * JDBC连接字符串生成
	 * 
	 * @param dbType
	 * @param dbIp
	 * @param dbPort
	 * @param dbName
	 * @param dbTableName
	 * @param dbUser
	 * @param dbPWD
	 * @return
	 */
	public static String getJdbcConnectParams(String dbType, String dbIp, String dbPort, String dbName,
			String dbTableName, String dbUser, String dbPWD) {
		StringBuffer jdbcConnectParams = new StringBuffer();
		if (DB_TYPE_SQLSERVER.equalsIgnoreCase(dbType)) { // SqlServer
			jdbcConnectParams.append("url=jdbc:jtds:sqlserver://" + dbIp);
			jdbcConnectParams.append(":" + dbPort);
			jdbcConnectParams.append("/" + dbName + "\n");
		} else if (DB_TYPE_POSTGRESQL.equalsIgnoreCase(dbType)) { // PostgreSQL
			jdbcConnectParams.append("url=jdbc:postgresql://" + dbIp);
			jdbcConnectParams.append(":" + dbPort);
			jdbcConnectParams.append("/" + dbName + "\n");
		} else if (DB_DRIVER_ORACLE.equalsIgnoreCase(dbType)) { // PostgreSQL
			jdbcConnectParams.append("url=jdbc:oracle:thin:@" + dbIp);
			jdbcConnectParams.append(":" + dbPort);
			jdbcConnectParams.append(":" + dbName + "\n");
		} else { // mysql
			jdbcConnectParams.append("url=jdbc:mysql://" + dbIp);
			jdbcConnectParams.append(":" + dbPort);
			jdbcConnectParams.append("/" + dbName + "\n");
		}
		jdbcConnectParams.append("table=" + dbTableName + "\n");
		jdbcConnectParams.append("user=" + dbUser + "\n");
		jdbcConnectParams.append("pass=" + dbPWD + "\n");
		return jdbcConnectParams.toString();
	}

	/**
	 * 数据库表信息（表名/注释）取得SQL文
	 * 
	 * @param dbType
	 * @param dbName
	 * @return
	 */
	public static String getTableInfoSql(String dbType, String dbName) {

		StringBuffer sql = new StringBuffer();
		if (ConnectorInfoUtils.DB_TYPE_SQLSERVER.equalsIgnoreCase(dbType)) {
			sql.append("SELECT c.name as table_name, cast(isnull(f.[value], '') as nvarchar(255)) as table_comment from sysobjects c left join sys.extended_properties f on f.major_id=c.id and f.minor_id=0 and f.class=1 where (Type='U' OR Type='V') ORDER BY table_name");
		} else if (ConnectorInfoUtils.DB_TYPE_POSTGRESQL.equalsIgnoreCase(dbType)) {
			sql.append("select psut.relname as table_name,pd.description as table_comment from pg_stat_user_tables psut left join pg_description pd  on psut.relid=pd.objoid and pd.objsubid=0  ");
			sql.append(" order by table_name");
		} else if (ConnectorInfoUtils.DB_TYPE_ORACLE.equalsIgnoreCase(dbType)) {
			sql.append("select table_name as table_name,comments as table_comment from user_tab_comments ");
			sql.append(" order by table_name");
		} else {
			sql.append("Select table_name,table_comment from INFORMATION_SCHEMA.TABLES where table_schema = '");
			sql.append(dbName);
			sql.append("' order by table_name");
		}
		return sql.toString();
	}

	/**
	 * 数据库表列表信息取得SQL文
	 * 
	 * @param dbType
	 * @param dbName
	 * @return
	 */
	public static String getTableListSql(String dbType, String dbName) {

		StringBuffer sql = new StringBuffer();

		if (ConnectorInfoUtils.DB_TYPE_SQLSERVER.equalsIgnoreCase(dbType)) {
			sql.append("SELECT name as table_name from sysobjects where (Type='U' OR Type='V') ORDER BY table_name");
		} else if (ConnectorInfoUtils.DB_TYPE_POSTGRESQL.equalsIgnoreCase(dbType)) {
			// 用户自己添加的表
			sql.append("select relname as table_name from pg_stat_user_tables where schemaname = 'public' ");
			sql.append("order by table_name");
		} else if (ConnectorInfoUtils.DB_TYPE_ORACLE.equalsIgnoreCase(dbType)) {
			// 用户自己添加的表
			sql.append("select table_name from user_tables ");
			sql.append("order by table_name");
		} else {
			sql.append("Select table_name from INFORMATION_SCHEMA.TABLES where table_schema = '");
			sql.append(dbName);
			sql.append("' order by table_name");
		}
		return sql.toString();
	}

	/**
	 * 数据库表字段列表信息取得SQL文
	 * 
	 * @param dbType
	 * @param tableName
	 * @return
	 */
	public static String getFieldListSql(String dbType, String tableName) {
		String sql = " select * from " + tableName + " where 1=2 ";

		if (ConnectorInfoUtils.DB_TYPE_MYSQL.equalsIgnoreCase(dbType)) {
			sql = " select * from `" + tableName + "` where 1=2 ";
		}
		return sql;
	}

	/**
	 * 表名在数据库中是否存在全段SQL文
	 * 
	 * @param dbType
	 * @param dbName
	 * @param tableName
	 * @return
	 */
	public static String isExistTableSql(String dbType, String dbName, String tableName) {
		StringBuffer sql = new StringBuffer();
		if (ConnectorInfoUtils.DB_TYPE_SQLSERVER.equalsIgnoreCase(dbType)) {
			sql.append("SELECT 1 from sysobjects where (Type='U' OR Type='V')");
			sql.append(" AND name='" + tableName + "'");
		} else if (ConnectorInfoUtils.DB_TYPE_SQLSERVER.equalsIgnoreCase(dbType)) {
			// 所有表判断（包含系统表）
			sql.append("SELECT 1 from pg_tables where ");
			sql.append(" tablename='" + tableName + "'");
		} else if (ConnectorInfoUtils.DB_TYPE_ORACLE.equalsIgnoreCase(dbType)) {
			// 所有表判断（包含系统表）
			sql.append("SELECT 1 from all_tables where ");
			sql.append(" table_name='" + tableName + "'");
		} else {
			sql.append("Select 1 from INFORMATION_SCHEMA.TABLES Where table_schema = '");
			sql.append(dbName);
			sql.append("' AND table_name='" + tableName + "'");
		}
		return sql.toString();
	}
}