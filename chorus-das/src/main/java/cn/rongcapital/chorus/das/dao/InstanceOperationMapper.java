package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.InstanceOperation;

public interface InstanceOperationMapper {
    int deleteByPrimaryKey(Long operationId);

    int insert(InstanceOperation record);

    int insertSelective(InstanceOperation record);

    InstanceOperation selectByPrimaryKey(Long operationId);

    int updateByPrimaryKeySelective(InstanceOperation record);

    int updateByPrimaryKey(InstanceOperation record);
}