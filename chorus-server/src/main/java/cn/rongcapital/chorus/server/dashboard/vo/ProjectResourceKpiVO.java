package cn.rongcapital.chorus.server.dashboard.vo;

import java.util.Date;

/**
 * Created by hhlfl on 2017-7-20.
 */
public class ProjectResourceKpiVO {
    private Long id;

    private Long projectId;

    private String projectName;

    private Date kpiDate;

    private Double cpuUsage;

    private Double memoryUsage;

    private Long storageUsed;

    private Double storageUsage;

    private Long dataDailyIncr;

    private Double taskSuccessRate;

    private Byte score;

    private String userName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Date getKpiDate() {
        return kpiDate;
    }

    public void setKpiDate(Date kpiDate) {
        this.kpiDate = kpiDate;
    }

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(Double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public Double getStorageUsage() {
        return storageUsage;
    }

    public void setStorageUsage(Double storageUsage) {
        this.storageUsage = storageUsage;
    }

    public Long getDataDailyIncr() {
        return dataDailyIncr;
    }

    public void setDataDailyIncr(Long dataDailyIncr) {
        this.dataDailyIncr = dataDailyIncr;
    }

    public Double getTaskSuccessRate() {
        return taskSuccessRate;
    }

    public void setTaskSuccessRate(Double taskSuccessRate) {
        this.taskSuccessRate = taskSuccessRate;
    }

    public Byte getScore() {
        return score;
    }

    public void setScore(Byte score) {
        this.score = score;
    }

    public Long getStorageUsed() {
        return storageUsed;
    }

    public void setStorageUsed(Long storageUsed) {
        this.storageUsed = storageUsed;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
