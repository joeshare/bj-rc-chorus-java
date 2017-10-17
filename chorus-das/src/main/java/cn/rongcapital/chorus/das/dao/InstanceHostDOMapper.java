package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.InstanceHostDO;

import java.util.List;

public interface InstanceHostDOMapper {

    List<InstanceHostDO> selectByInstanceId(Long instanceId);

    void deleteByInstanceId(Long instanceId);
}