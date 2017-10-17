package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ResourceType;

public interface ResourceTypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ResourceType record);

    int insertSelective(ResourceType record);

    ResourceType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ResourceType record);

    int updateByPrimaryKey(ResourceType record);
}