package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.JobDeploymentResult;
import cn.rongcapital.chorus.das.entity.StreamDeploymentResult;

/**
 * 任务部署接口
 *
 * @author li.hzh
 * @date 2016-11-22 10:46
 */
public interface JobDeploymentService {
    
    /**
     * 部署任务
     *
     * @param job 待部署任务,
     *            如果待部署任务为空或存在其他参数不合法情况,会抛出{@link IllegalArgumentException}
     * @return 部署结果状态
     */
    JobDeploymentResult deployJob(Job job);

    /**
     * 卸载任务, 实际对应XD中的destroy任务
     *
     * @param job 待卸载任务
     * @return 任务卸载结果状态
     */
    JobDeploymentResult undeployJob(Job job);

    /**
     * 重新执行任务
     *
     * @param jobExecutionId jobExecutionId
     * @return 重新执行任务结果状态
     */
    void restartJobExecution(long jobExecutionId);

    /**
     * 卸载Stream任务
     *
     * @param job
     * @return
     */
    void undeployStream(Job job);

    /**
     * 卸载Stream Count任务
     *
     * @param job
     * @return
     */
    void undeployStreamCount(Job job);

    /**
     * 执行任务
     *
     * @param job 待执行任务
     * @return 任务执行状态
     */
    JobDeploymentResult launchJob(Job job);
    
    /**
     * 部署Stream
     *
     * @param job
     * @return
     */
    StreamDeploymentResult deployStream(Job job);

    /**
     * 部署Stream计数器
     *
     * @param job
     * @return
     */
    StreamDeploymentResult deployStreamCount(Job job);
    
}
