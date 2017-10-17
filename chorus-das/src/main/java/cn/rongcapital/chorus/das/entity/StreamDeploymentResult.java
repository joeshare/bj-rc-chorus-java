package cn.rongcapital.chorus.das.entity;

/**
 * Stream任务部署结果模型
 *
 * @author li.hzh
 * @date 2016-11-22 10:19
 */
public class StreamDeploymentResult {
    
    private Job job;
    private StreamDeploymentStatus status;
    // 失败原因
    private Throwable cause;
    
    public Throwable getCause() {
        return cause;
    }
    
    public void setCause(Throwable cause) {
        this.cause = cause;
    }
    
    public StreamDeploymentResult(Job job) {
        this.job = job;
    }
    
    public Job getJob() {
        return job;
    }
    
    public void setJob(Job job) {
        this.job = job;
    }
    
    public StreamDeploymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(StreamDeploymentStatus status) {
        this.status = status;
    }
    
    public enum StreamDeploymentStatus {
        SUCCESS, FAILED
    }
}
