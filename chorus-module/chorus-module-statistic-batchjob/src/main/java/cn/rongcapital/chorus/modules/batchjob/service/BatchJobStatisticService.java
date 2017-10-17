package cn.rongcapital.chorus.modules.batchjob.service;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 批量任务统计接口
 * @author kevin.gong
 * @Time 2017年6月22日 上午10:05:34
 */
public interface BatchJobStatisticService {

    /**
     * 批量任务统计方法
     */
    void jobStatistic(Connection chorusConn, Connection xdConn) throws SQLException;
    
}
