package cn.rongcapital.chorus.das.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务部署结果模型
 *
 * @author li.hzh
 * @date 2016-11-22 10:19
 */
public class JobDeploymentResult {

    private Job job;
    private JobDeploymentStatus status;
    // 失败原因
    private Throwable cause;

    // 成功的步骤列表用于回滚
    private List<Task> successStep = new ArrayList<Task>();
    // 失败的步骤
    private Task failedStep;

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public List<Task> getSuccessStep() {
        return successStep;
    }

    public void addSuccessStep(Task task) {
        this.successStep.add(task);
    }

    public Task getFailedStep() {
        return failedStep;
    }

    public void setFailedStep(Task failedStep) {
        this.failedStep = failedStep;
    }

    public JobDeploymentResult(Job job) {
        this.job = job;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public JobDeploymentStatus getStatus() {
        return status;
    }

    public void setStatus(JobDeploymentStatus status) {
        this.status = status;
    }

    public enum JobDeploymentStatus {
        SUCCESS, STEP_FAILED, COMPOSED_JOB_FAILED
    }
}
