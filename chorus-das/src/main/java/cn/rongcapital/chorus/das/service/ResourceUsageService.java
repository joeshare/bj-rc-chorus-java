package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ResourceUsage;

import java.util.List;

public interface ResourceUsageService {
    int deleteByPrimaryKey(Integer id);

    int insert(ResourceUsage record);

    int insertSelective(ResourceUsage record);

    ResourceUsage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ResourceUsage record);

    int updateByPrimaryKey(ResourceUsage record);

    List<ResourceUsage> selectResourceUsages();

}