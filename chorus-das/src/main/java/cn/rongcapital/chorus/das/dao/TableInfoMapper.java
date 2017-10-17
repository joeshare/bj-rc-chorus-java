package cn.rongcapital.chorus.das.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.rongcapital.chorus.das.entity.TableInfo;

public interface TableInfoMapper {
    int deleteByPrimaryKey(Long tableInfoId);

    int insert(TableInfo record);

    int insertSelective(TableInfo record);

    TableInfo selectByPrimaryKey(Long tableInfoId);

    int updateByPrimaryKeySelective(TableInfo record);

    int updateByPrimaryKey(TableInfo record);
    
    /**
     * @return
     * @author yunzhong
     * @time 2017年6月21日上午11:25:56
     */
    List<TableInfo> listAllTables();
    
    /**
     * 查询最高关注度表
     * @param projectId 项目编号
     * @param size 表数量
     * @return resourceName：表名 ；attCount：关注度（用户申请次数）
     */
    List<Map<String, Object>> selectTopAttRateTable(@Param("projectId")long projectId, @Param("size")int size);
}