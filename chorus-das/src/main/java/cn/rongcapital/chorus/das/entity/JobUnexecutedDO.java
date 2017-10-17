package cn.rongcapital.chorus.das.entity;

import java.util.Date;

/**
 * @author kevin.gong
 * @Time 2017年9月21日 下午1:47:03
 */
public class JobUnexecutedDO {
    
    private int id;
    
    private String jobName;
    
    private String scheExecTime;
    
    private int rerunFlag;
    
    private int noticeFlag;
    
    private int status;
    
    private String execTime;
    
    private Date createTime;
    
    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getScheExecTime() {
        return scheExecTime;
    }

    public void setScheExecTime(String scheExecTime) {
        this.scheExecTime = scheExecTime;
    }

    public int getRerunFlag() {
        return rerunFlag;
    }

    public void setRerunFlag(int rerunFlag) {
        this.rerunFlag = rerunFlag;
    }

    public int getNoticeFlag() {
        return noticeFlag;
    }

    public void setNoticeFlag(int noticeFlag) {
        this.noticeFlag = noticeFlag;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getExecTime() {
        return execTime;
    }

    public void setExecTime(String execTime) {
        this.execTime = execTime;
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
}
