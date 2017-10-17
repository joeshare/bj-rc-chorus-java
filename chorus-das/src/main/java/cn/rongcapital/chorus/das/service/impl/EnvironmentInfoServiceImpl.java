package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.EnvironmentInfoDOMapper;
import cn.rongcapital.chorus.das.entity.EnvironmentInfo;
import cn.rongcapital.chorus.das.service.EnvironmentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alan on 12/2/16.
 */
@Service
public class EnvironmentInfoServiceImpl implements EnvironmentInfoService {
    @Autowired
    private EnvironmentInfoDOMapper environmentInfoDOMapper;

    @Override
    public List<EnvironmentInfo> listByInstanceId(Long instanceId) {
        return environmentInfoDOMapper.selectByInstanceInfoId(instanceId);
    }

    @Override
    public List<EnvironmentInfo> listAll() {
        return environmentInfoDOMapper.selectAll();
    }
}
