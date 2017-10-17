package cn.rongcapital.chorus.modules.ranger.sync.task;

import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;

/**
 * Created by shicheng on 2017/4/11.
 */
@Mixin({})
public class TaskMeta {

    private String dbHost;
    private int dbPort;
    private String databaseName;
    private String dbUserName;
    private String dbPassword;

    private String rangerHost;
    private int rangerPort;
    private String rangerLoginUrl = "/j_spring_security_check";
    private String rangerRestApiUrl = "/service/public/api/policy";
    private String rangerRepositoryName = "choruscluster_hive";
    private String rangerUsername;
    private String rangerPassword;

    public String getDbHost() {
        return dbHost;
    }

    @ModuleOption(value = "Database host")
    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public int getDbPort() {
        return dbPort;
    }

    @ModuleOption(value = "Database host port")
    public void setDbPort(int dbPort) {
        this.dbPort = dbPort;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    @ModuleOption(value = "Database name")
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    @ModuleOption(value = "Database username")
    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    @ModuleOption(value = "Database password")
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getRangerHost() {
        return rangerHost;
    }

    @ModuleOption(value = "ranger host")
    public void setRangerHost(String rangerHost) {
        this.rangerHost = rangerHost;
    }

    public int getRangerPort() {
        return rangerPort;
    }

    @ModuleOption(value = "ranger port")
    public void setRangerPort(int rangerPort) {
        this.rangerPort = rangerPort;
    }

    public String getRangerLoginUrl() {
        return rangerLoginUrl;
    }

    @ModuleOption(value = "ranger login url")
    public void setRangerLoginUrl(String rangerLoginUrl) {
        this.rangerLoginUrl = rangerLoginUrl;
    }

    public String getRangerRestApiUrl() {
        return rangerRestApiUrl;
    }

    @ModuleOption(value = "ranger rest api url")
    public void setRangerRestApiUrl(String rangerRestApiUrl) {
        this.rangerRestApiUrl = rangerRestApiUrl;
    }

    public String getRangerRepositoryName() {
        return rangerRepositoryName;
    }

    @ModuleOption(value = "ranger repository name")
    public void setRangerRepositoryName(String rangerRepositoryName) {
        this.rangerRepositoryName = rangerRepositoryName;
    }

    public String getRangerUsername() {
        return rangerUsername;
    }

    @ModuleOption(value = "ranger user name")
    public void setRangerUsername(String rangerUsername) {
        this.rangerUsername = rangerUsername;
    }

    public String getRangerPassword() {
        return rangerPassword;
    }

    @ModuleOption(value = "ranger user password")
    public void setRangerPassword(String rangerPassword) {
        this.rangerPassword = rangerPassword;
    }
}
