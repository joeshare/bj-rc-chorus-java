package cn.rongcapital.chorus.modules.streamjob.service;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 流式任务统计接口
 * @author kevin.gong
 * @Time 2017年8月9日 上午10:05:12
 */
public interface StreamJobStatisticService {

    /**
     * 流式任务统计方法
     */
    void jobStatistic(Connection conn, String monitorSpringXdZkPath, String zookeeperAddress, int zookeeperTimeout) throws SQLException;
    
}
