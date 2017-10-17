package cn.rongcapital.chorus.das.entity;

import java.util.Date;

public class EnvironmentInfo {
    private Long environmentId;

    private String environmentName;

    private String environmentVersion;

    private Date createTime;

    private Date updateTime;

    private String statusCode;

    public Long getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName == null ? null : environmentName.trim();
    }

    public String getEnvironmentVersion() {
        return environmentVersion;
    }

    public void setEnvironmentVersion(String environmentVersion) {
        this.environmentVersion = environmentVersion == null ? null : environmentVersion.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode == null ? null : statusCode.trim();
    }
}