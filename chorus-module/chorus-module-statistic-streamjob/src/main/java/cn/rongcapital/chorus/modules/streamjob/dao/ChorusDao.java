package cn.rongcapital.chorus.modules.streamjob.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import cn.rongcapital.chorus.modules.streamjob.entity.Job;
import cn.rongcapital.chorus.modules.streamjob.entity.StreamExecStatistic;

/**
 * chorus库Dao
 * @author kevin.gong
 * @Time 2017年8月8日 下午2:00:55
 */
public interface ChorusDao {

    /**
     * 获取所有流式任务
     * @return 流式任务信息
     */
    List<Job> getAllStreamJob(Connection connection) throws SQLException;
    
    /**
     * 添加统计结果
     * @param jobExecStatistic 统计结果
     * @return true：成功/false：失败
     */
    boolean addStreamJobStatistic(Connection connection, StreamExecStatistic streamExecStatistic) throws SQLException;
    
}
