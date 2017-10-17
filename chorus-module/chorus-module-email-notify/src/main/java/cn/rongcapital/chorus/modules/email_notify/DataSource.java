package cn.rongcapital.chorus.modules.email_notify;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yunzhong
 *
 * @date 2017年8月8日下午5:00:41
 */
@Slf4j
@Data
public class DataSource {
    private String driverClassName;
    private String url;
    private String userName;
    private String password;

    public Connection getConnection() throws SQLException {

        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            log.error("Error while getting connection.", e);
            throw new IllegalStateException(e);
        }

        log.debug("Getting connection from: {} -- {}", this.url, this.userName);
        return DriverManager.getConnection(this.url, this.userName, this.password);
    }
}
