package cn.rongcapital.chorus.modules.hive2vertica;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.sql.*;


@Slf4j
public class HiveConnector {

    public static ResultSet executeHiveSql(String url, String dbName, String userName,
                                           String hql, String rollbackHql, String queueName) throws SQLException {
        Connection conn = null;
        conn = getHiveConn(url, dbName, userName, queueName);
        ResultSet rs;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(hql);
        } catch (Exception ex) {
            log.error("Error while executing hql, hql: {}.", hql);
            log.error("Error while executing hql.", ex);
            if (conn != null && StringUtils.isNotBlank(rollbackHql)) {
                log.info("rollback...{}", rollbackHql);
                Statement rollbackStmt = conn.createStatement();
                rollbackStmt.execute(rollbackHql);
            }
            throw new IllegalStateException(ex.toString());
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return rs;
    }

    public static void executeHiveSqlNoRes(String url, String dbName, String userName,
                                           String hql, String rollbackHql, String queueName) throws SQLException {
        Connection conn = null;
        conn = getHiveConn(url, dbName, userName, queueName);
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(hql);
        } catch (Exception ex) {
            log.error("Error while executing hql, hql: {}.", hql);
            log.error("Error while executing hql.", ex);
            if (conn != null && StringUtils.isNotBlank(rollbackHql)) {
                log.info("rollback...{}", rollbackHql);
                Statement rollbackStmt = conn.createStatement();
                rollbackStmt.execute(rollbackHql);
            }
            throw new IllegalStateException(ex.toString());
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * 获取conn 连接 by 用户
     */
    private static Connection getHiveConn(String url, String dbName, String userName, String queueName) throws SQLException {
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
        } catch (ClassNotFoundException e) {
            log.error("Error while getting hive connection.", e);
            throw new IllegalStateException(e);
        }
        log.info("Getting connection from hive: {}/{} -- {}", url, dbName, userName);
        return DriverManager.getConnection(
                url + "/" + dbName + "?tez.queue.name=" + queueName,
                userName, "");
    }

}
