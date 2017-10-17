package cn.rongcapital.chorus.das.service;

import java.util.List;

import cn.rongcapital.chorus.common.exception.NoSuchStatusCodeException;
import cn.rongcapital.chorus.common.exception.ResourceNotEnoughException;
import cn.rongcapital.chorus.das.entity.ResourceApproveDO;
import cn.rongcapital.chorus.das.entity.ResourceInner;
import cn.rongcapital.chorus.das.entity.ResourceOperation;
import cn.rongcapital.chorus.das.entity.ResourceOperationDO;

/**
 * Created by abiton on 11/11/2016.
 */
public interface ResourceOperationService {
    //为项目申请资源
    void applyResourceForProject(ResourceOperation operation, ResourceInner resource);

    //审批资源
    void approveResource(ResourceApproveDO resourceApproveDO) throws ResourceNotEnoughException, NoSuchStatusCodeException;

    //根据状态列出资源操作
    List<ResourceOperationDO> getResourceOperationByStatus(String statusCode,int pageNum,int pageSize);

    //根据项目列出资源操作
    List<ResourceOperationDO> getResourceOperationByProjectId(Long projectId);

    //根据操作id获取资源操作对象
    ResourceOperation getResourceOperationById(Long operationId);
}
