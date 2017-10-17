package cn.rongcapital.chorus.resourcemanager.service;

import cn.rongcapital.chorus.das.entity.HostInfo;
import cn.rongcapital.chorus.resourcemanager.ExecutionUnitGroup;

import java.util.List;
import java.util.Map;

/**
 * Created by abiton on 27/11/2016.
 */
public interface ResourceManagerService {

    Map<String, Integer> allocateResource(Long instanceId, List<HostInfo> hosts, ExecutionUnitGroup eu);

    Map<String, Integer> reAllocateResource(Long instanceId, List<HostInfo> hosts, Integer size);

    void destroyResource(Long instanceId);

    boolean checkResourceExist(Long projectId);
}
