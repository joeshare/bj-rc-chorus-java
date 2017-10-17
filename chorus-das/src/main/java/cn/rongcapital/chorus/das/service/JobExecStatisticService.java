package cn.rongcapital.chorus.das.service;

import java.util.List;
import java.util.Map;

import cn.rongcapital.chorus.das.entity.JobExecStatistic;

/**
 * 任务执行统计Service
 * @author kevin.gong
 * @Time 2017年6月20日 上午10:12:52
 */
public interface JobExecStatisticService {
    
    /**
     * 获取任务执行分布
     * @param projectId 项目编号
     * @param recentDayNum 天数
     * @return date:日期，completedNum：执行完成次数，failedNum：执行失败次数，runningNum：正在执行次数， runNumAtStatTime：统计时正在执行的任务数量
     */
    List<Map<String, Object>> getJobExecDist(long projectId, int recentDayNum);
    
    /**
     * 获取执行时间最长的任务
     * @param projectId 项目编号
     * @param size 数量
     * @return jobName：任务名称，seconds：执行时间（单位：秒）
     */
    List<Map<String, Object>> getLongestExecTimeJob(long projectId, int size);
    
    /**
     * 添加批量任务统计结果
     * @param jobExecStatistic 批量任务统计结果Bean
     * @return true:成功/false:失败
     */
    boolean addBatchJobExecStatistic(JobExecStatistic jobExecStatistic);
    
    /**
     * 修改时长信息
     * @param jobExecStatistic 统计数据结果
     * @return true:成功/false:失败
     */
    boolean updateDuration(JobExecStatistic jobExecStatistic);
}
