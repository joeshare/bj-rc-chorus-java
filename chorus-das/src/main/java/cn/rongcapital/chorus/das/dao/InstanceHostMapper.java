package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.InstanceHost;

public interface InstanceHostMapper {
    int deleteByPrimaryKey(Long id);

    int insert(InstanceHost record);

    int insertSelective(InstanceHost record);

    InstanceHost selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InstanceHost record);

    int updateByPrimaryKey(InstanceHost record);
}