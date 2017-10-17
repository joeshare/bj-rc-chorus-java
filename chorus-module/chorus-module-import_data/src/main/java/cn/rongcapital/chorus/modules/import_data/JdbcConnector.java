package cn.rongcapital.chorus.modules.import_data;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by hhlfl on 2017-6-27.
 */
@Slf4j
public class JdbcConnector {
    public static Connection getConnection(String driverClassName, String url, String userName, String pwd) throws SQLException {

        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            log.error("Error while getting connection.", e);
            throw new IllegalStateException(e);
        }

        log.info("Getting connection from: {} -- {}", url, userName);
        return DriverManager.getConnection(url,userName, pwd);
    }
}
