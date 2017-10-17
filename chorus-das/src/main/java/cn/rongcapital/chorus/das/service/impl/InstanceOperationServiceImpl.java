package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.dao.InstanceOperationMapper;
import cn.rongcapital.chorus.das.entity.InstanceOperation;
import cn.rongcapital.chorus.das.service.InstanceOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by alan on 11/28/16.
 */
@Service
public class InstanceOperationServiceImpl implements InstanceOperationService {

    @Autowired
    private InstanceOperationMapper instanceOperationMapper;

    @Transactional
    @Override
    public void insert(Long instanceId, String userId,String userName, StatusCode statusCode) {
        InstanceOperation instanceOperation = new InstanceOperation();
        instanceOperation.setInstanceId(instanceId);
        instanceOperation.setUserId(userId);
        instanceOperation.setUserName(userName);
        instanceOperation.setCreateTime(new Date());
        instanceOperation.setStatusCode(statusCode.getCode());
        instanceOperationMapper.insert(instanceOperation);
    }
}
