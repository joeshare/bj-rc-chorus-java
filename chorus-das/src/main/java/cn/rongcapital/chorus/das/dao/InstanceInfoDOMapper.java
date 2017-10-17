package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.InstanceInfo;
import cn.rongcapital.chorus.das.entity.InstanceInfoDO;
import cn.rongcapital.chorus.das.entity.InstanceInfoWithHostsDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InstanceInfoDOMapper {

    List<InstanceInfo> selectByProjectId(Long projectId);

    List<InstanceInfoDO> selectDOByProjectId(Long projectId);

    List<InstanceInfoWithHostsDO> selectByStatusCode(List<String> statusCodeList);

    List<InstanceInfoWithHostsDO> selectByProjectIdAndStatusCode(@Param("projectId") Long projectId,
                                                                 @Param("statusCodeList") List<String> statusCodeList);

    InstanceInfoDO getDOById(Long instanceId);

    List<InstanceInfo> selectAll();
}