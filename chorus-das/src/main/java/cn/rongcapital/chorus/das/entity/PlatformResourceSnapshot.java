package cn.rongcapital.chorus.das.entity;

import java.util.Date;

public class PlatformResourceSnapshot {
    private Long id;

    private Date snapshotDate;

    private Integer appliedCpu;

    private Integer platformCpu;

    private Integer appliedMemory;

    private Integer platformMemory;

    private Long appliedStorage;

    private Long platformStorage;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(Date snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public Integer getAppliedCpu() {
        return appliedCpu;
    }

    public void setAppliedCpu(Integer appliedCpu) {
        this.appliedCpu = appliedCpu;
    }

    public Integer getPlatformCpu() {
        return platformCpu;
    }

    public void setPlatformCpu(Integer platformCpu) {
        this.platformCpu = platformCpu;
    }

    public Integer getAppliedMemory() {
        return appliedMemory;
    }

    public void setAppliedMemory(Integer appliedMemory) {
        this.appliedMemory = appliedMemory;
    }

    public Integer getPlatformMemory() {
        return platformMemory;
    }

    public void setPlatformMemory(Integer platformMemory) {
        this.platformMemory = platformMemory;
    }

    public Long getAppliedStorage() {
        return appliedStorage;
    }

    public void setAppliedStorage(Long appliedStorage) {
        this.appliedStorage = appliedStorage;
    }

    public Long getPlatformStorage() {
        return platformStorage;
    }

    public void setPlatformStorage(Long platformStorage) {
        this.platformStorage = platformStorage;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}