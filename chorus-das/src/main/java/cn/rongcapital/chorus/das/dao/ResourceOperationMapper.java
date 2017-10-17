package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ResourceOperation;

public interface ResourceOperationMapper {
    int deleteByPrimaryKey(Long operationId);

    int insert(ResourceOperation record);

    int insertSelective(ResourceOperation record);

    ResourceOperation selectByPrimaryKey(Long operationId);

    int updateByPrimaryKeySelective(ResourceOperation record);

    int updateByPrimaryKey(ResourceOperation record);
}