package cn.rongcapital.chorus.das.service;

import java.util.List;
import java.util.Map;

import cn.rongcapital.chorus.das.entity.ExecutingJobInfo;
import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.PageInfo;
import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.das.entity.web.JobCause;

/**
 * 数据任务
 *
 * @author lengyang
 */
public interface JobService {
    /**
     * 保存数据任务
     *
     * @param job
     */
    void saveJob(Job job);

    /**
     * 修改数据任务
     *
     * @param job
     */
    void updateJob(Job job);

    /**
     * 查询所有数据任务
     *
     * @return
     */
    List<Job> selectAll();

    /**
     * 删除任务信息
     *
     * @param cause
     */
    void delJob(JobCause cause);

    /**
     * 根据Id查询任务信息
     *
     * @param jobId
     * @return
     */
    Job selectJob(int jobId);

    /**
     * 根据Id查询任务关联执行信息
     *
     * @param jobId
     * @return
     */
    Job selectExcuteJobInfos(int jobId);

    /**
     * 查询项目任务
     *
     * @param
     * @return
     */
    List<Job> getProjectJobList(JobCause jobCause);

    /**
     * 任务发布
     *
     * @param jobCause
     * @return
     */
    void deploy(JobCause jobCause);

    /**
     * 任务取消发布
     *
     * @param cause
     * @return
     */
    void undeploy(JobCause cause);

    /**
     * 任务发布
     *
     * @param jobExecutionId
     * @return
     */
    void restartJobExecution(long jobExecutionId);

    /**
     * 任务执行
     *
     * @param cause
     * @return
     */
    void excute(JobCause cause);

    /**
     * 根据Job昵称取得项目下Job列表
     *
     * @param jobCause
     * @return
     */
    List<Job> checkProjectJobByAsName(JobCause jobCause);

    /**
     * 取得记录条数
     *
     * @param jobCause
     * @return
     */
    int count(JobCause jobCause);

    /**
     * @param jobName
     * @return
     * @author yunzhong
     * @version
     * @since 2017年5月19日
     */
    Job selectJobByName(String jobName);
    
    /**
     * 获取任务状态分布
     * @param projectId 项目编号
     * @return key：Job状态    value：该状态数量
     */
    Map<String, Long> getJobStatusDistribution(long projectId);
    
    /**
     * 获取所有流式任务状态
     * @return 任务信息
     */
    List<Job> getAllStreamJob();
    
    /**
     * 获取所有批量任务状态
     * @return 任务信息
     */
    List<Job> getAllBatchJob();

    /**
     * @param xdExecutions
     * @return
     * @author yunzhong
     * @time 2017年6月27日上午11:46:32
     */
    List<Job> getJobs(List<XDExecution> xdExecutions);
    
    /**
     * 获取正在执行流式任务信息
     */
    PageInfo<ExecutingJobInfo> getExecutingStreamJobList(Integer pageNum, Integer size);
    
    /**
     * 获取正在执行批量任务信息
     */
    PageInfo<ExecutingJobInfo> getExecutingBatchJobList(Integer pageNum, Integer size);

    /**
     * @param instanceId
     * @return 正在执行的批量任务
     */
    List<ExecutingJobInfo> getExecutingBatchJobByInstanceId(Long instanceId);

    /**
     * @param instanceId
     * @return 正在执行的流式任务
     */
    List<ExecutingJobInfo> getExecutingStreamByInstanceId(Long instanceId);

    boolean isRunning(Integer jobId);
}
