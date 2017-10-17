package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ResourceTemplate;

public interface ResourceTemplateMapper {
    int deleteByPrimaryKey(Long resourceTemplateId);

    int insert(ResourceTemplate record);

    int insertSelective(ResourceTemplate record);

    ResourceTemplate selectByPrimaryKey(Long resourceTemplateId);

    int updateByPrimaryKeySelective(ResourceTemplate record);

    int updateByPrimaryKey(ResourceTemplate record);
}