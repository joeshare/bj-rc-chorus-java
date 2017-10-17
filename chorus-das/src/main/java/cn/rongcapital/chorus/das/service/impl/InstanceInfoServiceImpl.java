package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.dao.InstanceInfoDOMapper;
import cn.rongcapital.chorus.das.dao.InstanceInfoMapper;
import cn.rongcapital.chorus.das.entity.InstanceInfo;
import cn.rongcapital.chorus.das.entity.InstanceInfoDO;
import cn.rongcapital.chorus.das.entity.InstanceInfoWithHostsDO;
import cn.rongcapital.chorus.das.service.InstanceInfoService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alan on 11/27/16.
 */
@Service
public class InstanceInfoServiceImpl implements InstanceInfoService {

    @Autowired
    private InstanceInfoMapper instanceInfoMapper;
    @Autowired
    private InstanceInfoDOMapper instanceInfoDOMapper;

    @Override
    public List<InstanceInfo> listByProject(Long projectId) {
        return instanceInfoDOMapper.selectByProjectId(projectId);
    }

    @Override
    public List<InstanceInfoDO> listDOByProject(Long projectId) {
        return instanceInfoDOMapper.selectDOByProjectId(projectId);
    }

    @Override
    public List<InstanceInfoDO> listDOByProject(Long projectId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return instanceInfoDOMapper.selectDOByProjectId(projectId);
    }

    @Override
    public List<InstanceInfoWithHostsDO> listByStatusCode(StatusCode... statusCodeArray) {
        List<String> list = Arrays.stream(statusCodeArray).map(StatusCode::getCode).collect(Collectors.toList());
        return instanceInfoDOMapper.selectByStatusCode(list);
    }

    @Override
    public List<InstanceInfoWithHostsDO> listByProjectIdAndStatusCode(Long projectId, StatusCode... statusCodeArray) {
        List<String> list = Arrays.stream(statusCodeArray).map(StatusCode::getCode).collect(Collectors.toList());
        return instanceInfoDOMapper.selectByProjectIdAndStatusCode(projectId, list);
    }


    @Override
    public List<InstanceInfo> getInstanceInfoByStatusCode(StatusCode statusCode){
        InstanceInfo instanceInfo= new InstanceInfo();
        instanceInfo.setStatusCode(statusCode.getCode());
        return instanceInfoMapper.selectByStatus(instanceInfo);
    }

    @Override
    public InstanceInfo listByProjectIdAndGroup(Long projectId, String group){
        InstanceInfo instanceInfo= new InstanceInfo();
        instanceInfo.setProjectId(projectId);
        instanceInfo.setGroupName(group);
        return instanceInfoMapper.selectByProjectIdAndGroupName(instanceInfo);
    }

    @Override
    public InstanceInfo getById(Long instanceId) {
        return instanceInfoMapper.selectByPrimaryKey(instanceId);
    }

    @Override
    public InstanceInfoDO getDOById(Long instanceId) {
        return instanceInfoDOMapper.getDOById(instanceId);
    }

    @Transactional
    @Override
    public boolean insert(InstanceInfo instanceInfo) {
        return instanceInfoMapper.insertSelective(instanceInfo) == 1;
    }

    @Transactional
    @Override
    public boolean modify(InstanceInfo instanceInfo) {
        return instanceInfoMapper.updateByPrimaryKeySelective(instanceInfo) == 1;
    }

    @Transactional
    @Override
    public boolean delete(Long instanceId) {
        return instanceInfoMapper.deleteByPrimaryKey(instanceId) == 1;
    }

    @Override
    public List<InstanceInfo> selectAll() {
        return instanceInfoDOMapper.selectAll();
    }

}
