package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ResourceUsage;

public interface ResourceUsageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ResourceUsage record);

    int insertSelective(ResourceUsage record);

    ResourceUsage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ResourceUsage record);

    int updateByPrimaryKey(ResourceUsage record);
}