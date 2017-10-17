package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ResourceUsedStats;

public interface ResourceUsedStatsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ResourceUsedStats record);

    int insertSelective(ResourceUsedStats record);

    ResourceUsedStats selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ResourceUsedStats record);

    int updateByPrimaryKey(ResourceUsedStats record);
}