package cn.rongcapital.chorus.das.entity;

public class InstanceEnvironmentMapping {
    private Long instanceEnvironmentId;

    private Long instanceId;

    private Long environmentId;

    public Long getInstanceEnvironmentId() {
        return instanceEnvironmentId;
    }

    public void setInstanceEnvironmentId(Long instanceEnvironmentId) {
        this.instanceEnvironmentId = instanceEnvironmentId;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
    }
}