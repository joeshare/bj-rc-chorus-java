package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.EnvironmentInfo;

public interface EnvironmentInfoMapper {
    int deleteByPrimaryKey(Long environmentId);

    int insert(EnvironmentInfo record);

    int insertSelective(EnvironmentInfo record);

    EnvironmentInfo selectByPrimaryKey(Long environmentId);

    int updateByPrimaryKeySelective(EnvironmentInfo record);

    int updateByPrimaryKey(EnvironmentInfo record);
}