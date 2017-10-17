package cn.rongcapital.chorus.das.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.HadoopPathException;
import cn.rongcapital.chorus.common.exception.NoSuchStatusCodeException;
import cn.rongcapital.chorus.common.exception.ResourceNotEnoughException;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.hadoop.DefaultHadoopClient;
import cn.rongcapital.chorus.common.hadoop.HadoopClient;
import cn.rongcapital.chorus.common.hadoop.HadoopUtil;
import cn.rongcapital.chorus.common.util.OrikaBeanMapper;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.dao.ProjectInfoMapper;
import cn.rongcapital.chorus.das.dao.ResourceInnerDOMapper;
import cn.rongcapital.chorus.das.dao.ResourceInnerMapper;
import cn.rongcapital.chorus.das.dao.ResourceOperationDOMapper;
import cn.rongcapital.chorus.das.dao.ResourceOperationMapper;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.ResourceApproveDO;
import cn.rongcapital.chorus.das.entity.ResourceInner;
import cn.rongcapital.chorus.das.entity.ResourceOperation;
import cn.rongcapital.chorus.das.entity.ResourceOperationDO;
import cn.rongcapital.chorus.das.entity.TotalResource;
import cn.rongcapital.chorus.das.service.QueueService;
import cn.rongcapital.chorus.das.service.ResourceOperationService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by abiton on 11/11/2016.
 */

@Service
@Slf4j
public class ResourceOperationServiceImpl implements ResourceOperationService {
    @Autowired
    ResourceOperationMapper operationMapper;
    @Autowired
    ResourceOperationDOMapper operationDOMapper;
    @Autowired
    ResourceInnerMapper resourceInnerMapper;
    @Autowired
    ResourceInnerDOMapper resourceInnerDOMapper;

    @Autowired
    HadoopClient hadoopClient;
    @Autowired
    QueueService queueService;

    @Autowired
    ProjectInfoMapper projectMapper;

    @Override
    public void applyResourceForProject(ResourceOperation operation, ResourceInner resource) {
        Date createDate = new Date();
        Long projectId = operation.getProjectId();
        ResourceInner byProject = resourceInnerDOMapper.getByProjectId(projectId);
        if (byProject != null) {
            operation.setResourceId(byProject.getResourceInnerId());
        }
        operation.setStatusCode(StatusCode.RESOURCE_OPERATE_APPLY.getCode());
        operation.setCreateTime(createDate);
        operationMapper.insertSelective(operation);
    }

    @Override
    @Transactional
    public void approveResource(ResourceApproveDO approveDO)
            throws NoSuchStatusCodeException, ResourceNotEnoughException {
        ResourceOperation operation = operationMapper.selectByPrimaryKey(approveDO.getOperationId());
        if (operation == null) {
            throw new ServiceException(StatusCode.RECORD_NOT_EXISTS);
        }
        Date updateDate = new Date();
        operation.setUpdateTime(updateDate);
        StatusCode code;
        if (approveDO.isApprove()) {
            code = StatusCode.RESOURCE_OPERATE_APPROVE;
        } else {
            code = StatusCode.RESOURCE_OPERATE_DENY;
        }
        operation.setStatusCode(code.getCode());
        operation.setUpdateUserId(approveDO.getUserId());
        operation.setUpdateUserName(approveDO.getUserName());
        operation.setNotes(approveDO.getNotes());
        ResourceInner resource = null;

        TotalResource leftResource = approveDO.getLeftResource();
        switch (code) {
        case RESOURCE_OPERATE_APPROVE:
            if (leftResource.getCpu() < operation.getCpu() || leftResource.getMemory() < operation.getMemory()
                    || leftResource.getStorage() < operation.getStorage()) {
                throw new ResourceNotEnoughException();
            }
            // 如果新项目同时多个资源申请，所有申请的resourceid都为null。一个审批通过，其他的resourceid还是为null，但是resource_inner中已经存在记录。
            if (operation.getResourceId() == null) {
                resource = resourceInnerMapper.selectByProjectId(operation.getProjectId());
            } else {
                resource = resourceInnerMapper.selectByPrimaryKey(operation.getResourceId());
            }
            if (resource != null) {
                resource.setResourceCpu(resource.getResourceCpu() + operation.getCpu());
                resource.setResourceMemory(resource.getResourceMemory() + operation.getMemory());
                resource.setResourceStorage(resource.getResourceStorage() + operation.getStorage());
                log.info("resource update for project {}" + resource.getProjectId());
                resourceInnerMapper.updateByPrimaryKeySelective(resource);
            } else {
                resource = OrikaBeanMapper.INSTANCE.map(operation, ResourceInner.class);
                resource.setStatusCode(StatusCode.RESOURCE_APPROVE.getCode());
                resource.setCreateTime(new Date());
                resource.setResourceCpu(operation.getCpu());
                resource.setResourceMemory(operation.getMemory());
                resource.setResourceStorage(operation.getStorage());
                log.info("resource new for project {}", resource.getProjectId());
                resourceInnerDOMapper.insertSelective(resource);
                log.info("resource id is {}", resource.getResourceInnerId());
                operation.setResourceId(resource.getResourceInnerId());
            }
            break;
        case RESOURCE_OPERATE_DENY:
            break;
        default:
            throw new NoSuchStatusCodeException();
        }
        log.info("operation {} update for project {}", operation.getOperationId(), operation.getProjectId());
        if (resource != null && operation.getResourceId() == null) {
            operation.setResourceId(resource.getResourceInnerId());
        }
        operationMapper.updateByPrimaryKeySelective(operation);

        if (StatusCode.RESOURCE_OPERATE_APPROVE == code) {
            ProjectInfo project = projectMapper.selectByPrimaryKey(resource.getProjectId());
            String dir = HadoopUtil.formatPath(project.getProjectCode());

            if (!hadoopClient.setSpaceQuota(dir, resource.getResourceStorage(), "G")) {
                throw new HadoopPathException(StatusCode.PROJECT_HDFS_PATH_QUOTA_ERROR);
            }

            queueService.createQueue(operation, approveDO.getTotalResource());
        }
    }

    @Override
    public List<ResourceOperationDO> getResourceOperationByStatus(String statusCode, int pageNum, int pageSize) {
        if (StringUtils.isEmpty(statusCode)) {
            return Collections.emptyList();
        }
        PageHelper.startPage(pageNum, pageSize);
        return operationDOMapper.selectByStatus(statusCode);
    }

    @Override
    public List<ResourceOperationDO> getResourceOperationByProjectId(Long projectId) {
        if (projectId == null) {
            return Collections.emptyList();
        }

        List<ResourceOperationDO> operationDOList = operationDOMapper.selectByCreateProjectId(projectId);
        operationDOList.stream().forEach(operation -> {
            StatusCode statusCode = StatusCode.fromCode(operation.getStatusCode());
            operation.setStatusDesc(statusCode.getDesc());
        });
        return operationDOList;
    }

    @Override
    public ResourceOperation getResourceOperationById(Long operationId) {
        ResourceOperation operation = operationMapper.selectByPrimaryKey(operationId);
        if (operation == null) {
            throw new ServiceException(StatusCode.RECORD_NOT_EXISTS);
        }

        return operation;
    }
}
