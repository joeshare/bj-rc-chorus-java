package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.entity.InstanceInfo;
import cn.rongcapital.chorus.das.entity.InstanceInfoDO;
import cn.rongcapital.chorus.das.entity.InstanceInfoWithHostsDO;

import java.util.List;

/**
 * Created by alan on 11/27/16.
 */
public interface InstanceInfoService {

    List<InstanceInfo> listByProject(Long projectId);

    List<InstanceInfoDO> listDOByProject(Long projectId);

    List<InstanceInfoDO> listDOByProject(Long projectId,int pageNum,int pageSize);

    List<InstanceInfoWithHostsDO> listByStatusCode(StatusCode... statusCodeList);

    List<InstanceInfoWithHostsDO> listByProjectIdAndStatusCode(Long projectId, StatusCode... statusCodeArray);

    List<InstanceInfo> getInstanceInfoByStatusCode(StatusCode statusCode);

    InstanceInfo listByProjectIdAndGroup(Long projectId, String group);

    InstanceInfo getById(Long instanceId);

    InstanceInfoDO getDOById(Long instanceId);

    boolean insert(InstanceInfo instanceInfo);

    boolean modify(InstanceInfo instanceInfo);

    boolean delete(Long instanceId);

    List<InstanceInfo> selectAll();

}
