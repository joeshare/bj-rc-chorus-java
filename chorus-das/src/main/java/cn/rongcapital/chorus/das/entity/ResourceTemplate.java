package cn.rongcapital.chorus.das.entity;

import java.util.Date;

public class ResourceTemplate {
    private Long resourceTemplateId;

    private String resourceName;

    private Integer resourceCpu;

    private Integer resourceMemory;

    private Integer resourceStorage;

    private Date createTime;

    private Date updateTime;

    private String statusCode;

    public Long getResourceTemplateId() {
        return resourceTemplateId;
    }

    public void setResourceTemplateId(Long resourceTemplateId) {
        this.resourceTemplateId = resourceTemplateId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName == null ? null : resourceName.trim();
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

    public Integer getResourceStorage() {
        return resourceStorage;
    }

    public void setResourceStorage(Integer resourceStorage) {
        this.resourceStorage = resourceStorage;
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