package cn.rongcapital.chorus.resourcemanager.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.OrikaBeanMapper;
import cn.rongcapital.chorus.das.entity.HostInfo;
import cn.rongcapital.chorus.das.entity.HostReservationSizeDO;
import cn.rongcapital.chorus.das.entity.InstanceHostDO;
import cn.rongcapital.chorus.das.entity.ResourceInner;
import cn.rongcapital.chorus.das.service.HostInfoService;
import cn.rongcapital.chorus.das.service.InstanceHostService;
import cn.rongcapital.chorus.das.service.ResourceInnerService;
import cn.rongcapital.chorus.resourcemanager.ExecutionUnitGroup;
import cn.rongcapital.chorus.resourcemanager.service.CuratorService;
import cn.rongcapital.chorus.resourcemanager.service.ResourceManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

import static cn.rongcapital.chorus.das.entity.HostReservationSizeDO.emptyReservationDO;

/**
 * Created by abiton on 27/11/2016.
 */
@Service
@Slf4j
public class ResourceManagerServiceImpl implements ResourceManagerService {
    @Autowired
    private CuratorService curatorService;
    @Autowired
    private InstanceHostService instanceHostService;

    @Autowired
    private ResourceInnerService resourceInnerService;

    @Autowired
    private YarnClient yarnClient;
    @Autowired
    private HostInfoService hostInfoService;

    @Value("${zk.chorus.host.path}")
    private String hostPath;

    private List<HostInfo> getCurrentHostResource(List<HostInfo> hosts) {
        try {
            List<NodeReport> nodeReports = yarnClient.getNodeReports(NodeState.RUNNING);
            Map<Long, HostReservationSizeDO> reservationSizeDOMap = hostInfoService.getReservationSizeMap();
            Map<String, Resource> nodeReportMap = new HashMap<>();
            nodeReports.forEach(nodeReport ->
                    {
                        String host = nodeReport.getNodeId().getHost();
                        Resource used = nodeReport.getUsed();
                        Resource capability = nodeReport.getCapability();
                        capability.setVirtualCores(capability.getVirtualCores() - used.getVirtualCores());
                        capability.setMemory(capability.getMemory() - used.getMemory());
                        nodeReportMap.put(host, capability);
                    }
            );

            List<HostInfo> result = new ArrayList<>();
            hosts.forEach(host -> {
                HostInfo hostInfo = new HostInfo();
                if (nodeReportMap.containsKey(host.getHostName())) {
                    Resource resource = nodeReportMap.get(host.getHostName());
                    HostReservationSizeDO reservationSizeDO =
                            reservationSizeDOMap.getOrDefault(hostInfo.getHostId(), emptyReservationDO);
                    hostInfo.setCpu(resource.getVirtualCores() - reservationSizeDO.getCpu());
                    hostInfo.setMemory((resource.getMemory() / 1024) - reservationSizeDO.getMemory());
                    hostInfo.setHostId(host.getHostId());
                    hostInfo.setHostName(host.getHostName());
                    result.add(hostInfo);
                }

            });
            return result;

        } catch (YarnException e) {
            log.error("get node report error ", e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        } catch (IOException e) {
            log.error("get node report error ", e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        }

    }

    @Transactional
    @Override
    public Map<String, Integer> allocateResource(Long instanceId, List<HostInfo> hosts, ExecutionUnitGroup eu) {
        Map<String, Integer> result = new HashMap<>();

        curatorService.executeWithLock(client -> {
            log.info("---------allocating resources... instanceId: {}, hosts: {}, ExecutionUnitGroup: {}",
                    instanceId, hosts, eu);

            List<HostInfo> current = getCurrentHostResource(hosts);
            Integer cpu = eu.getResourceTemplate().getResourceCpu();
            Integer memory = eu.getResourceTemplate().getResourceMemory();
            Integer size = eu.getInstanceSize();
            Map<HostInfo, Integer> allocate = allocate(current, cpu, memory, size);

            Map<Long, Integer> hostMap = new HashMap<>();
            allocate.forEach((hostInfo, hostSize) -> {
                result.put(hostInfo.getHostName(), hostSize);
                hostMap.put(hostInfo.getHostId(), hostSize);
            });

            instanceHostService.createInstanceHost(instanceId, hostMap);
            log.info("---------FINISHED allocate resources... instanceId: {}, result: {}", instanceId, result);
        });

        return result;
    }

    @Transactional
    @Override
    public Map<String, Integer> reAllocateResource(Long instanceId, List<HostInfo> hosts, Integer size) {
        Map<String, Integer> result = new HashMap<>();

        curatorService.executeWithLock(client -> {
            log.info("---------reAllocating resources... instanceId: {}, hosts: {}, size: {}",
                    instanceId, hosts, size);

            List<InstanceHostDO> byInstance = instanceHostService.getHostInfosByInstance(instanceId);
            int cpu = byInstance.get(0).getCpu();
            int memory = byInstance.get(0).getMemory();

            Map<String, InstanceHostDO> allocatedMap = new HashMap<>();
            Map<HostInfo, Integer> allocated = new HashMap<HostInfo, Integer>();
            byInstance.forEach(instanceHostDO -> {
                allocatedMap.put(instanceHostDO.getHostName(), instanceHostDO);
                HostInfo hostInfo = new HostInfo();
                hostInfo.setCpu(cpu * instanceHostDO.getSize());
                hostInfo.setMemory(memory * instanceHostDO.getSize());
                hostInfo.setHostId(instanceHostDO.getHostId());
                hostInfo.setHostName(instanceHostDO.getHostName());
                allocated.put(hostInfo, instanceHostDO.getSize());
            });

            List<HostInfo> current = getCurrentHostResource(hosts);
            List<HostInfo> preCurrent = new ArrayList<>();
            current.forEach(hostInfo -> {
                HostInfo target = new HostInfo();
                BeanUtils.copyProperties(hostInfo, target);

                if (allocatedMap.containsKey(hostInfo.getHostName())) {
                    InstanceHostDO instanceHostDO = allocatedMap.get(hostInfo.getHostName());
                    target.setCpu(hostInfo.getCpu() + instanceHostDO.getSize() * cpu);
                    target.setMemory(hostInfo.getMemory() + instanceHostDO.getSize() * memory);
                }

                preCurrent.add(target);
            });

            Map<HostInfo, Integer> allocate = allocate(preCurrent, cpu, memory, size);

            Map<Long, Integer> hostMap = new HashMap<>();
            allocate.forEach((hostInfo, hostSize) -> {
                result.put(hostInfo.getHostName(), hostSize);
                hostMap.put(hostInfo.getHostId(), hostSize);
            });
            instanceHostService.deleteInstanceHost(instanceId);
            instanceHostService.createInstanceHost(instanceId, hostMap);

            log.info("---------FINISHED reAllocate resources... instanceId: {}, result: {}", instanceId, result);
        });
        return result;
    }

    @Override
    public void destroyResource(Long instanceId) {
        curatorService.executeWithLock(client -> {
            log.info("---------destroying resources... instanceId: {}", instanceId);
            List<InstanceHostDO> hostInfosByInstance = instanceHostService.getHostInfosByInstance(instanceId);
            List<HostInfo> hosts = OrikaBeanMapper.INSTANCE.mapAsList(hostInfosByInstance, HostInfo.class);

            List<HostInfo> current = getCurrentHostResource(hosts);

            Map<HostInfo, Integer> allocate = new HashMap<>();
            hostInfosByInstance.forEach(
                    instanceHostDO -> {
                        int size = instanceHostDO.getSize();
                        HostInfo map = OrikaBeanMapper.INSTANCE.map(instanceHostDO, HostInfo.class);
                        map.setCpu(instanceHostDO.getCpu() * size);
                        map.setMemory(instanceHostDO.getMemory() * size);
                        allocate.put(map, size);
                    }
            );
            instanceHostService.deleteInstanceHost(instanceId);
            log.info("---------FINISHED destroy resources... instanceId: {}", instanceId);
        });

    }

    @Override
    public boolean checkResourceExist(Long projectId) {
        ResourceInner resourceInner = resourceInnerService.getByProjectId(projectId);
        if (resourceInner != null) {
            return true;
        }
        return false;

    }


    private Map<HostInfo, Integer> allocate(List<HostInfo> current, Integer cpu, Integer memory, Integer size) {
        Map<HostInfo, Integer> result = new HashMap<>();
        Collections.sort(current, (hostInfo1, hostInfo2) ->
                hostInfo2.getCpu() - hostInfo1.getCpu()
        );
        int left = size;
        for (HostInfo host : current) {
            int core = host.getCpu() / cpu;
            int mem = host.getMemory() / memory;
            if (core > 0 && mem > 0) {
                HostInfo hostInfo = new HostInfo();
                hostInfo.setHostName(host.getHostName());
                hostInfo.setHostId(host.getHostId());
                int min = Math.min(core, mem);
                if (left == 0) {
                    break;
                }
                if (min >= left) {
                    hostInfo.setCpu(cpu * left);
                    hostInfo.setMemory(memory * left);
                    result.put(hostInfo, left);
                    left = 0;
                    break;
                } else {
                    hostInfo.setCpu(cpu * min);
                    hostInfo.setMemory(memory * min);
                    result.put(hostInfo, min);
                    left = left - min;
                }
            } else {
                log.info("host {} do not have enough resource request for cpu {} memory {} "
                        , host.getHostName(), cpu, memory);
            }
        }

        if (left > 0) {
            throw new ServiceException(StatusCode.EXCEPTION_RESOURCENOTENOUGH);
        }
        return result;
    }

}

interface Operate {
    int op(int a, int b);
}

class Add implements Operate {
    @Override
    public int op(int a, int b) {
        return a + b;
    }
}

class Minus implements Operate {
    @Override
    public int op(int a, int b) {
        return a - b;
    }
}