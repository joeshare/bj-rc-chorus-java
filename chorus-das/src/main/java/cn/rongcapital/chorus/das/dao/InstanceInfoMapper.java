package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.InstanceInfo;

import java.util.List;

public interface InstanceInfoMapper {
    int deleteByPrimaryKey(Long instanceId);

    int insert(InstanceInfo record);

    int insertSelective(InstanceInfo record);

    InstanceInfo selectByPrimaryKey(Long instanceId);

    int updateByPrimaryKeySelective(InstanceInfo record);

    int updateByPrimaryKey(InstanceInfo record);

    InstanceInfo selectByProjectIdAndGroupName(InstanceInfo instanceInfo);

    List<InstanceInfo> selectByStatus(InstanceInfo instanceInfo);

}