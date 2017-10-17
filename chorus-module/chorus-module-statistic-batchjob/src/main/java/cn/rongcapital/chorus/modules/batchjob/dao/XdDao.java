package cn.rongcapital.chorus.modules.batchjob.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import cn.rongcapital.chorus.modules.batchjob.entity.JobExecStatistic;

/**
 * XD库Dao
 * @author kevin.gong
 * @Time 2017年8月8日 下午2:01:11
 */
public interface XdDao {

    /**
     * 获取job统计结果
     * @param statisticDate 统计日期
     * @return 统计结果
     */
    List<JobExecStatistic> getJobStatisticFromXDExecutions(Connection connection, String statisticDate) throws SQLException;
    
}
