package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.EnvironmentInfo;
import cn.rongcapital.chorus.das.entity.InstanceEnvironmentMapping;
import cn.rongcapital.chorus.das.entity.InstanceInfo;
import cn.rongcapital.chorus.das.entity.ResourceTemplate;

import java.util.List;

public interface EnvironmentInfoDOMapper {

    List<EnvironmentInfo> selectByInstanceInfoId(Long instanceId);

    List<EnvironmentInfo> selectAll();
}