package cn.rongcapital.chorus.modules.resource.kpi.snapshot.bean;


import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by hhlfl on 2017-7-17.
 */
public class ProjectResourceKPISnapshot {
    private long id;
    private long projectId;
    private String projectName;
    private Date kpiDate;
    private double cpuUsage;
    private double memoryUsage;
    private double storageUsage;
    private long dataDailyIncr;
    private double taskSuccessRate;
    private int score;
    private Timestamp createTime;
    private int cpuTotal;
    private int cpuUsed;
    private int memoryTotal;
    private int memoryUsed;
    private long storageTotal;
    private long storageUsed;
    private int taskTotal;
    private int taskSuccess;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCpuTotal() {
        return cpuTotal;
    }

    public void setCpuTotal(int cpuTotal) {
        this.cpuTotal = cpuTotal;
    }

    public int getCpuUsed() {
        return cpuUsed;
    }

    public void setCpuUsed(int cpuUsed) {
        this.cpuUsed = cpuUsed;
    }

    public int getMemoryTotal() {
        return memoryTotal;
    }

    public void setMemoryTotal(int memoryTotal) {
        this.memoryTotal = memoryTotal;
    }

    public int getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(int memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    public long getStorageTotal() {
        return storageTotal;
    }

    public void setStorageTotal(long storageTotal) {
        this.storageTotal = storageTotal;
    }

    public long getStorageUsed() {
        return storageUsed;
    }

    public void setStorageUsed(long storageUsed) {
        this.storageUsed = storageUsed;
    }

    public int getTaskTotal() {
        return taskTotal;
    }

    public void setTaskTotal(int taskTotal) {
        this.taskTotal = taskTotal;
    }

    public int getTaskSuccess() {
        return taskSuccess;
    }

    public void setTaskSuccess(int taskSuccess) {
        this.taskSuccess = taskSuccess;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
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

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public double getStorageUsage() {
        return storageUsage;
    }

    public void setStorageUsage(double storageUsage) {
        this.storageUsage = storageUsage;
    }

    public long getDataDailyIncr() {
        return dataDailyIncr;
    }

    public void setDataDailyIncr(long dataDailyIncr) {
        this.dataDailyIncr = dataDailyIncr;
    }

    public double getTaskSuccessRate() {
        return taskSuccessRate;
    }

    public void setTaskSuccessRate(double taskSuccessRate) {
        this.taskSuccessRate = taskSuccessRate;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
