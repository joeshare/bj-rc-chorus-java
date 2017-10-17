package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.dao.InstanceInfoDOMapper;
import cn.rongcapital.chorus.das.dao.ResourceInnerDOMapper;
import cn.rongcapital.chorus.das.dao.ResourceInnerMapper;
import cn.rongcapital.chorus.das.entity.InstanceInfoDO;
import cn.rongcapital.chorus.das.entity.ResourceInner;
import cn.rongcapital.chorus.das.service.ResourceInnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alan on 12/2/16.
 */
@Service
public class ResourceInnerServiceImpl implements ResourceInnerService {
    @Autowired
    private ResourceInnerDOMapper resourceInnerDOMapper;
    @Autowired
    private InstanceInfoDOMapper instanceInfoDOMapper;
    @Autowired
    private ResourceInnerMapper resourceInnerMapper;

    @Override
    public ResourceInner getByProjectId(Long projectId) {
        return resourceInnerDOMapper.getByProjectId(projectId);
    }

    @Override
    public ResourceInner getLeftByProjectId(Long projectId) {
        ResourceInner byProjectId = getByProjectId(projectId);
        if (byProjectId != null && !StatusCode.RESOURCE_DESTROY.getCode().equals(byProjectId.getStatusCode())) {
            List<InstanceInfoDO> instanceInfoDOList = instanceInfoDOMapper.selectDOByProjectId(projectId);
            instanceInfoDOList.stream().filter(infoDO -> !infoDO.getCommonStatus().getStatusCode()
                    .equals(StatusCode.EXECUTION_UNIT_DESTROYED.getCode())).forEach(infoDO -> {
                        int instanceCpu = infoDO.getInstanceSize() * infoDO.getResourceTemplate().getResourceCpu();
                        int cpu = byProjectId.getResourceCpu() - instanceCpu;
                        byProjectId.setResourceCpu(cpu);
                        int instanceMem = infoDO.getInstanceSize() * infoDO.getResourceTemplate().getResourceMemory();
                        int memory = byProjectId.getResourceMemory() - instanceMem;
                        byProjectId.setResourceMemory(memory);
                    });

            return byProjectId;
        }

        return null;
    }

    @Override
    public int updateByPrimaryKeySelective(ResourceInner record){
        return resourceInnerMapper.updateByPrimaryKeySelective(record);
    }

}
