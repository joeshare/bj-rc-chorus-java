package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.bo.LogBO;
import cn.rongcapital.chorus.das.entity.JobMonitor;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.das.entity.web.JobMonitorCause;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Li.ZhiWei
 */
public interface JobMonitorService {
    /**
     * 根据任务别名称查询与指定用户相匹配的任务监控列表
     * 
     * @param jobMonitorCause
     * @param userId
     * @return
     */
    Map<String, List<JobMonitor>> searchJobMonitor(JobMonitorCause jobMonitorCause, String userId);

    /**
     * 根据执行id 获取子任务id
     * 
     * @param executionId
     */
    List<Long> getSubJobExecutionIds(long executionId);

    /**
     * 根据执行id 获取子任务监控对象
     * 
     * @param executionId
     * @return
     */
    List<JobMonitor> getSubJobMonitor(int jobId, long executionId);

    /**
     * 根据jobId 获取任务监控对象
     * 
     * @param jobCause
     * @return
     */
    List<JobMonitor> searchJobMonitorWithJobId(JobMonitorCause jobMonitorCause);

    /**
     * 根据用户获取与之相关的项目列表
     * 
     * @param userId
     * @return 项目集合
     */
    List<ProjectInfo> searchProjectInfoFromUser(String userId);

    /**
     * 修改用户相关项目的过滤状态
     * 
     * @param userId
     * @param jobMonitorCause
     */
    int modifyUserProjectFilterState(String userId, JobMonitorCause jobMonitorCause);

    List<JobMonitor> composeJobMonitorWithXdByLimit(List<JobMonitor> jmListFilterByJob,
            List<JobMonitor> jmListFilterByXD, JobMonitorCause jobMonitorCause);

    /**
     * 根据stream名，获取已经处理条数
     * 
     * @param streamName
     * @return 处理条数
     */
    long getStreamRecordCount(String streamName);

    /**
     * @param pageNum
     * @param pageSize
     * @param statusList
     * @return
     * @author yunzhong
     * @version
     * @since 2017年5月16日
     */
    List<XDExecution> getXDExecutions(int pageNum, int pageSize, List<String> statusList);

    /**
     * 获得 {@code executionId} 的实时执行日志
     *
     * @param executionId
     * @param startTimestamp
     * @param from
     * @param size
     *
     * @return
     */
    LogBO realTimeLog(long executionId, long startTimestamp, int from, int size) throws IOException;
}
