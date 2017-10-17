package cn.rongcapital.chorus.common.util;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

/**
 * Created by alan on 02/05/2017.
 */
@Slf4j
public class HiveConnector {

    public static ResultSet executeHiveSql(String url, String userName, String queueName,
                                           String hql, String rollbackHql) {
        execute(url, userName, queueName, connection -> {
            log.info("Preparing execute sql: {} in hive (with result): {}@{} --- {}, rollback: {}",
                    hql, userName, url, queueName, rollbackHql);
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(hql);
        }, connection -> {
            if (rollbackHql != null) {
                log.info("Preparing rollback for sql: {} in hive (without result): {}@{} --- {}, rollback: {}",
                        hql, userName, url, queueName, rollbackHql);
                Statement rollbackStmt = connection.createStatement();
                rollbackStmt.execute(rollbackHql);
            }
        });
        return null;
    }

    public static void executeHiveSqlNoRes(String url, String userName, String queueName,
                                           String hql, String rollbackHql) {
        execute(url, userName, queueName, connection -> {
            log.info("Preparing execute sql: {} in hive (without result): {}@{} --- {}, rollback: {}",
                    hql, userName, url, queueName, rollbackHql);
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(hql);
            return null;
        }, connection -> {
            if (rollbackHql != null) {
                log.info("Preparing rollback for sql: {} in hive (without result): {}@{} --- {}, rollback: {}",
                        hql, userName, url, queueName, rollbackHql);
                Statement rollbackStmt = connection.createStatement();
                rollbackStmt.execute(rollbackHql);
            }
        });
    }

    public static <R> R execute(String url, String userName, String queueName, Function<Connection, R> f,
                                Consumer<Connection> rollbackF) {
        try {
            Connection conn = null;
            try {
                conn = getHiveConn(url, userName, queueName);
                return f.apply(conn);
            } catch (Exception e) {
                log.error("Error while executing sth in Hive!!! {}@{} --- {}", userName, url, queueName);
                log.error("Error while executing sth in Hive!!!", e);
                throw new ServiceException(StatusCode.HIVE_EXECUTION_FAIL, e);
            } finally {
                if (conn != null && !conn.isClosed())
                    conn.close();
            }
        } catch (ServiceException se) {
            throw se;
        } catch (Exception e) {
            log.error("Error while rollback or cleaning up...", e);
            throw new ServiceException(StatusCode.HIVE_EXECUTION_FAIL, e);
        }
    }

    /**
     * 获取conn 连接 by 用户
     */
    private static Connection getHiveConn(String url, String userName, String queueName) throws SQLException {
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
        } catch (ClassNotFoundException e) {
            log.error("Error while getting hive connection.", e);
            throw new IllegalStateException(e);
        }
        log.info("Getting connection from hive: {} -- {}", url, userName);
        return DriverManager.getConnection(url + "?tez.queue.name=" + queueName, userName, "");
    }

    @FunctionalInterface
    public static interface Function<T, R> {
        R apply(T t) throws SQLException;
    }

    @FunctionalInterface
    public static interface Consumer<T> {
        void apply(T t) throws SQLException;
    }

}

