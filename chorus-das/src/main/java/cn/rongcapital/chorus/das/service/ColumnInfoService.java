package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.*;

import java.util.List;

/**
 * @author fuxiangli
 *
 */
public interface ColumnInfoService {
    /**
     * 1.根据表ID,列ID,查询字段信息
     *
     * @param tableId
     * @param columnId
     * @return
     */
	ColumnInfo selectColumnInfo(Long tableId,Long columnId);
    /**
     * 2.根据表ID,查询字段信息
     *
     * @param tableId
     * @return
     */
    List<ColumnInfo> selectColumnInfo(Long tableId);
    /**
     * 3.插入列信息
     *
     * @param columnInfoList
     * @return
     */
    int bulkInsert(List<ColumnInfo> columnInfoList);
}
