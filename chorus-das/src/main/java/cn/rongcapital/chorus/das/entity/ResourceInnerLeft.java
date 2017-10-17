package cn.rongcapital.chorus.das.entity;

import java.util.Date;

public class ResourceInnerLeft {
    private Long resourceInnerId;

    private Long projectId;

    private String createUserId;

    private Integer resourceCpu;

    private Integer resourceMemory;

    private Double resourceStorage;

    private Date createTime;

    private Date updateTime;

    private String statusCode;

    public Long getResourceInnerId() {
        return resourceInnerId;
    }

    public void setResourceInnerId(Long resourceInnerId) {
        this.resourceInnerId = resourceInnerId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId == null ? null : createUserId.trim();
    }

    public Integer getResourceCpu() {
        return resourceCpu;
    }

    public void setResourceCpu(Integer resourceCpu) {
        this.resourceCpu = resourceCpu;
    }

    public Integer getResourceMemory() {
        return resourceMemory;
    }

    public void setResourceMemory(Integer resourceMemory) {
        this.resourceMemory = resourceMemory;
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

    public Double getResourceStorage() {
        return resourceStorage;
    }

    public void setResourceStorage(Double resourceStorage) {
        this.resourceStorage = resourceStorage;
    }
}