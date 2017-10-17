/**
 * 
 */
package cn.rongcapital.chorus.das.service;

import java.util.List;
import java.util.Map;

import cn.rongcapital.chorus.das.entity.ColumnInfo;
import cn.rongcapital.chorus.das.entity.TableInfo;
import cn.rongcapital.chorus.das.entity.TableInfoDO;
/**
 * @author fuxiangli
 *
 */
public interface TableInfoService {
	
    /**
     * 1.根据表ID,查询表信息
     *
     * @param tableId
     * @return
     */
	TableInfo selectByID(Long tableId);

    /**
	 * 3.根据项目ID,查询所有表信息
	 *
	 * @param projectId
	 * @return
	 */
	List<TableInfo> listAllTableinfo(Long projectId,int pageNum,int pageSize);
	
	/**
	 * 4.插入数据
	 *
	 * @param tableInfo
	 * @param columnInfo
	 * @return
	 */
	int bulkInsert(TableInfo tableInfo,List<ColumnInfo> columnInfo);

	/**
	 * 创建表
	 *
	 * @param tableInfo      表定义
	 * @param columnInfoList 列定义列表
	 */
	void createTable(TableInfo tableInfo, List<ColumnInfo> columnInfoList);
	
    /**
     * 分页查询所有的hive表信息
     * 
     * @param pageNum
     * @param pageSize
     * @return
     * @author yunzhong
     * @time 2017年6月21日上午11:20:22
     */
    List<TableInfoDO> listAllTables(int pageNum, int pageSize);
    
    /**
     * 查询最高关注度表
     * @param projectId 项目编号
     * @param size 表数量
     * @return resourceName：表名 ；attCount：关注度（用户申请次数）
     * @author gonglin
     * @time 2017年6月23日
     */
    List<Map<String, Object>> getTopAttRateTableInfo(long projectId, int size);
}
