package cn.rongcapital.chorus.das.entity;

import java.util.Date;

public class InstanceInfo {
    private Long instanceId;

    private Long projectId;

    private Long resourceInnerId;

    private Long resourceTemplateId;

    private Integer instanceSize;

    private String groupName;

    private Date createTime;

    private Date updateTime;

    private String statusCode;

    private String instanceDesc;

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getResourceInnerId() {
        return resourceInnerId;
    }

    public void setResourceInnerId(Long resourceInnerId) {
        this.resourceInnerId = resourceInnerId;
    }

    public Long getResourceTemplateId() {
        return resourceTemplateId;
    }

    public void setResourceTemplateId(Long resourceTemplateId) {
        this.resourceTemplateId = resourceTemplateId;
    }

    public Integer getInstanceSize() {
        return instanceSize;
    }

    public void setInstanceSize(Integer instanceSize) {
        this.instanceSize = instanceSize;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
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

    public String getInstanceDesc() {
        return instanceDesc;
    }

    public void setInstanceDesc(String instanceDesc) {
        this.instanceDesc = instanceDesc == null ? null : instanceDesc.trim();
    }
}