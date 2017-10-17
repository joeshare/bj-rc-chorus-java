package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.DatalabInfo;

public interface DatalabInfoMapper {
    int deleteByPrimaryKey(Long labId);

    int insert(DatalabInfo record);

    int insertSelective(DatalabInfo record);

    DatalabInfo selectByPrimaryKey(Long labId);

    int updateByPrimaryKeySelective(DatalabInfo record);

    int updateByPrimaryKey(DatalabInfo record);
}