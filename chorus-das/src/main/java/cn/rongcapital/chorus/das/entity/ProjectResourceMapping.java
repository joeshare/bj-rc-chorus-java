package cn.rongcapital.chorus.das.entity;

public class ProjectResourceMapping {
    private Long projectResourceOutId;

    private Long projectId;

    private Long resourceOutId;

    private String statusCode;

    public Long getProjectResourceOutId() {
        return projectResourceOutId;
    }

    public void setProjectResourceOutId(Long projectResourceOutId) {
        this.projectResourceOutId = projectResourceOutId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getResourceOutId() {
        return resourceOutId;
    }

    public void setResourceOutId(Long resourceOutId) {
        this.resourceOutId = resourceOutId;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode == null ? null : statusCode.trim();
    }
}