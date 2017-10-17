package cn.rongcapital.chorus.das.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class ResourceOut {
    private Long resourceOutId;

    private Long projectId;

    private String resourceName;

    private String resourceSource;

    private String resourceUsage;

    private String resourceDesc;

    private String storageType;

    private String connUrl;

    private String connPort;

    private String connUser;

    private String connPass;

    private String createUserId;

    private Date createTime;

    private String updateUserId;

    private Date updateTime;

    private Date endDate;

    private String statusCode;

    private String databaseName;

    private String connHost;

    private String createUserName;
    @Setter
    @Getter
    private String guid;

    @Setter
    @Getter
    private String path;

    @Setter
    @Getter
    private String connectMode;

    public Long getResourceOutId() {
        return resourceOutId;
    }

    public void setResourceOutId(Long resourceOutId) {
        this.resourceOutId = resourceOutId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName == null ? null : resourceName.trim();
    }

    public String getResourceSource() {
        return resourceSource;
    }

    public void setResourceSource(String resourceSource) {
        this.resourceSource = resourceSource == null ? null : resourceSource.trim();
    }

    public String getResourceUsage() {
        return resourceUsage;
    }

    public void setResourceUsage(String resourceUsage) {
        this.resourceUsage = resourceUsage == null ? null : resourceUsage.trim();
    }

    public String getResourceDesc() {
        return resourceDesc;
    }

    public void setResourceDesc(String resourceDesc) {
        this.resourceDesc = resourceDesc == null ? null : resourceDesc.trim();
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType == null ? null : storageType.trim();
    }

    public String getConnUrl() {
        return connUrl;
    }

    public void setConnUrl(String connUrl) {
        this.connUrl = connUrl == null ? null : connUrl.trim();
    }

    public String getConnPort() {
        return connPort;
    }

    public void setConnPort(String connPort) {
        this.connPort = connPort == null ? null : connPort.trim();
    }

    public String getConnUser() {
        return connUser;
    }

    public void setConnUser(String connUser) {
        this.connUser = connUser == null ? null : connUser.trim();
    }

    public String getConnPass() {
        return connPass;
    }

    public void setConnPass(String connPass) {
        this.connPass = connPass == null ? null : connPass.trim();
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId == null ? null : createUserId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId == null ? null : updateUserId.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode == null ? null : statusCode.trim();
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName == null ? null : databaseName.trim();
    }

    public String getConnHost() {
        return connHost;
    }

    public void setConnHost(String connHost) {
        this.connHost = connHost == null ? null : connHost.trim();
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName == null ? null : createUserName.trim();
    }
}
