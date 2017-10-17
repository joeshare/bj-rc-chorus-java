package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ColumnInfo;

import java.util.List;

public interface ColumnManageMapper {
    int deleteByPrimaryKey(Long tableInfoId, Long columnInfoId);

    int insert(ColumnInfo record);

    int insertSelective(ColumnInfo record);

    int bulkInsert(List<ColumnInfo> columnInfoList);

    ColumnInfo selectByPrimaryKey(Long tableInfoId, Long columnInfoId);

    //根据表ID,返回列信息
    List<ColumnInfo> selectByTableId(Long tableInfoId);

    int updateByPrimaryKeySelective(ColumnInfo record);

    int updateByPrimaryKey(ColumnInfo record);
}