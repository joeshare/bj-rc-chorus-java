package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.TableMonitorInfoV2;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * just version 2 for api {@link TableMonitorService}
 */
public interface TableMonitorServiceV2 {
    /**
     * @param table
     * @return
     * @author yunzhong
     * @time 2017年6月21日上午10:27:42
     */
    int insert(@Param("table") TableMonitorInfoV2 table);

    /**
     * 按照行数降序排列
     * 
     * @param projectId
     * @param top
     * @return
     * @author yunzhong
     * @time 2017年6月21日上午10:29:19
     */
    List<TableMonitorInfoV2> selectRowsTop(Long projectId, Integer top);

    /**
     * 按照存储降序排列
     * 
     * @param projectId
     * @param top
     * @return
     * @author yunzhong
     * @time 2017年6月21日上午10:29:22
     */
    List<TableMonitorInfoV2> selectStorageTop(Long projectId, Integer top);

    /**
     * 获得项目所有表占用空间
     * 
     * @param projectId
     * @return
     * @author yunzhong
     * @time 2017年6月21日上午10:57:39
     */
    long selectStorageTotal(Long projectId);

    /**
     * 获得项目下所有表的个数
     * 
     * @param projectId
     * @return
     * @author yunzhong
     * @time 2017年6月23日上午10:17:04
     */
    long getTablesTotal(Long projectId);

    /**
     * @return
     * @author yunzhong
     * @time 2017年7月12日下午2:33:35
     */
    int clearToday();
}
