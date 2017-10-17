package cn.rongcapital.chorus.das.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.rongcapital.chorus.das.entity.TableMonitorInfo;

public interface TableMonitorMapper {

    /**
     * @param table
     * @return
     * @author yunzhong
     * @time 2017年6月21日上午10:27:42
     */
    int insert(@Param("table") TableMonitorInfo table);

    /**
     * 最近一次统计中，按照行数降序排列
     * 
     * @param projectId
     * @param top
     * @return
     * @author yunzhong
     * @time 2017年6月21日上午10:29:19
     */
    List<TableMonitorInfo> selectRowsTop(@Param("projectId") Long projectId, @Param("top") Integer top);

    /**
     * 最近一次统计中，按照存储降序排列
     * 
     * @param projectId
     * @param top
     * @return
     * @author yunzhong
     * @time 2017年6月21日上午10:29:22
     */
    List<TableMonitorInfo> selectStorageTop(@Param("projectId") Long projectId, @Param("top") Integer top);

    /**
     * 最近一次统计中，项目所有表占用存储
     * 
     * @param projectId
     * @return
     * @author yunzhong
     * @time 2017年6月21日上午11:00:52
     */
    long selectStorageTotal(@Param("projectId") Long projectId);

    /**
     * 最近一次统计中，项目所有表总数
     * 
     * @param projectId
     * @return
     * @author yunzhong
     * @time 2017年6月23日上午10:30:57
     */
    long getTablesTotal(@Param("projectId") Long projectId);

    /**
     * @return
     * @author yunzhong
     * @time 2017年7月12日下午2:34:06
     */
    int clearToday();
}
