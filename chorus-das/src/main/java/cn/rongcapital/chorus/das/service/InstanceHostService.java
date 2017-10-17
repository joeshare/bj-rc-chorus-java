package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.InstanceHostDO;

import java.util.List;
import java.util.Map;

/**
 * Created by abiton on 28/11/2016.
 */
public interface InstanceHostService {

    List<InstanceHostDO> getHostInfosByInstance(Long instanceId);

    void createInstanceHost(Long instanceId,Map<Long,Integer> hosts);

    void deleteInstanceHost(Long instanceId);

}
