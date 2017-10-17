package cn.rongcapital.chorus.modules.batchjob.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import cn.rongcapital.chorus.modules.batchjob.entity.Job;
import cn.rongcapital.chorus.modules.batchjob.entity.JobExecStatistic;

/**
 * chorus库Dao
 * @author kevin.gong
 * @Time 2017年8月8日 下午2:00:55
 */
public interface ChorusDao {

    /**
     * 获取所有批量任务
     * @return 批量任务信息
     */
    List<Job> getAllBatchJob(Connection connection) throws SQLException;
    
    /**
     * 添加统计结果
     * @param jobExecStatistic 统计结果
     * @return true：成功/false：失败
     */
    boolean addBatchJobExecStatistic(Connection connection, JobExecStatistic jobExecStatistic) throws SQLException;
    
}
