package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.InstanceEnvironmentMapping;

public interface InstanceEnvironmentMappingMapper {
    int deleteByPrimaryKey(Long instanceEnvironmentId);

    int insert(InstanceEnvironmentMapping record);

    int insertSelective(InstanceEnvironmentMapping record);

    InstanceEnvironmentMapping selectByPrimaryKey(Long instanceEnvironmentId);

    int updateByPrimaryKeySelective(InstanceEnvironmentMapping record);

    int updateByPrimaryKey(InstanceEnvironmentMapping record);
}