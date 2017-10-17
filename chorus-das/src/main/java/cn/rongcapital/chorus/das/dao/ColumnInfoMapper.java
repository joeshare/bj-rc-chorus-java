package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ColumnInfo;

public interface ColumnInfoMapper {
    int deleteByPrimaryKey(Long columnInfoId);

    int insert(ColumnInfo record);

    int insertSelective(ColumnInfo record);

    ColumnInfo selectByPrimaryKey(Long columnInfoId);

    int updateByPrimaryKeySelective(ColumnInfo record);

    int updateByPrimaryKey(ColumnInfo record);
}