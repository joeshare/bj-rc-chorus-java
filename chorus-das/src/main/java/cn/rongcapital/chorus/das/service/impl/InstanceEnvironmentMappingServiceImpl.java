package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.InstanceEnvironmentMappingMapper;
import cn.rongcapital.chorus.das.entity.InstanceEnvironmentMapping;
import cn.rongcapital.chorus.das.service.InstanceEnvironmentMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by alan on 12/5/16.
 */
@Service
public class InstanceEnvironmentMappingServiceImpl implements InstanceEnvironmentMappingService {
    @Autowired
    private InstanceEnvironmentMappingMapper instanceEnvironmentMappingMapper;

    @Transactional
    @Override
    public void save(Long instanceId, List<Long> environmentIdList) {
        for (Long environmentId : environmentIdList) {
            InstanceEnvironmentMapping mapping = new InstanceEnvironmentMapping();
            mapping.setInstanceId(instanceId);
            mapping.setEnvironmentId(environmentId);
            instanceEnvironmentMappingMapper.insert(mapping);
        }
    }
}
