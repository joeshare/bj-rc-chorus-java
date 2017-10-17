package cn.rongcapital.chorus.das.service;

import java.util.List;
import java.util.Map;

import cn.rongcapital.chorus.das.entity.StreamExecStatistic;

/**
 * 任务执行统计Service
 * @author kevin.gong
 * @Time 2017年6月20日 上午10:12:52
 */
public interface StreamExecStatisticService {
    
    /**
     * 获取流式任务执行分布
     * @param projectId 项目编号
     * @param startDate 开始日期
     * @return date:日期，noExecNum：未执行数量，failedNum：执行失败数量，runningNum：正在执行数量
     */
    List<Map<String, Object>> getStreamExecDist(long projectId, int recentDayNum);
    
    /**
     * 添加流式任务统计结果
     * @param streamExecStatistic 流式任务统计结果Bean
     * @return true:成功/false:失败
     */
    boolean addStreamJobExecStatistic(StreamExecStatistic streamExecStatistic);
}
