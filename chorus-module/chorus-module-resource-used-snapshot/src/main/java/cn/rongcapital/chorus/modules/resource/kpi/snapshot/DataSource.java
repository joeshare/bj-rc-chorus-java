package cn.rongcapital.chorus.modules.resource.kpi.snapshot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by hhlfl on 2017-7-17.
 */
@Slf4j
@Component
public class DataSource {
    private String driverClassName;
    private String url;
    private String userName;
    private String password;

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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
