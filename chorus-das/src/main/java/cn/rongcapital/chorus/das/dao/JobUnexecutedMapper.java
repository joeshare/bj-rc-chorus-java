package cn.rongcapital.chorus.das.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.rongcapital.chorus.das.entity.JobUnexecutedDO;
import cn.rongcapital.chorus.das.entity.UnexecutedJob;

/**
 * 未执行任务Mapper
 * @author kevin.gong
 * @Time 2017年9月21日 上午11:22:51
 */
public interface JobUnexecutedMapper {

    /**
     * 获取待执行任务列表
     * @return 待执行任务列表
     */
    List<UnexecutedJob> selectWaitExecuteJobs(@Param("top")Integer top, @Param("size")Integer size);
    
    /**
     * 获取待执行任务列表数量
     * @return 待执行任务列表数量
     */
    int selectWaitExecuteJobsCount();

    /**
     * 获取未执行任务信息
     */
    List<JobUnexecutedDO> selectJobUnexecuted(@Param("rerunFlag")int rerunFlag, @Param("status")int status);
    
    /**
     * 修改执行状态
     * @param jobName job名称
     */
    int updateWaitExecuteJobStatus(@Param("jobName")String jobName);
    
    /**
     * 添加未执行任务信息
     */
    int insert(JobUnexecutedDO jobUnexecutedDO);
}
