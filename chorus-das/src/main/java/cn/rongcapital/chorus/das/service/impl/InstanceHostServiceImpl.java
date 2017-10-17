package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.InstanceHostDOMapper;
import cn.rongcapital.chorus.das.dao.InstanceHostMapper;
import cn.rongcapital.chorus.das.entity.InstanceHost;
import cn.rongcapital.chorus.das.entity.InstanceHostDO;
import cn.rongcapital.chorus.das.service.InstanceHostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by abiton on 28/11/2016.
 */
@Service
@Slf4j
public class InstanceHostServiceImpl implements InstanceHostService {

    @Autowired
    private InstanceHostMapper instanceHostMapper;
    @Autowired
    private InstanceHostDOMapper instanceHostDOMapper;


    @Override
    public List<InstanceHostDO> getHostInfosByInstance(Long instanceId) {
        if (instanceId == null) {
            return Collections.EMPTY_LIST;
        }

        return instanceHostDOMapper.selectByInstanceId(instanceId);
    }

    @Override
    @Transactional
    public void createInstanceHost(Long instanceId, Map<Long, Integer> hosts) {
        hosts.forEach((hostId, size) -> {
            InstanceHost record = new InstanceHost();
            record.setHostId(hostId);
            record.setInstanceId(instanceId);
            record.setSize(size);
            instanceHostMapper.insert(record);
        });
    }

    @Override
    public void deleteInstanceHost(Long instanceId) {
        if (instanceId != null) {
            instanceHostDOMapper.deleteByInstanceId(instanceId);
        }
    }
}
