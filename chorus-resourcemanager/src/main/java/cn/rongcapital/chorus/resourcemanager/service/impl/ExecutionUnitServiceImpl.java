package cn.rongcapital.chorus.resourcemanager.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.CollectionUtils;
import cn.rongcapital.chorus.common.xd.SpringXDTemplateDecorator;
import cn.rongcapital.chorus.das.entity.*;
import cn.rongcapital.chorus.das.service.*;
import cn.rongcapital.chorus.resourcemanager.ExecutionUnitGroup;
import cn.rongcapital.chorus.resourcemanager.service.ExecutionUnitService;
import cn.rongcapital.chorus.resourcemanager.service.ResourceManagerService;
import cn.rongcapital.chorus.resourcemanager.service.YarnService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.xd.rest.domain.DetailedContainerResource;
import org.springframework.yarn.boot.actuate.endpoint.YarnContainerClusterEndpoint;
import org.springframework.yarn.boot.actuate.endpoint.mvc.ContainerClusterCreateRequest;
import org.springframework.yarn.boot.actuate.endpoint.mvc.ContainerClusterModifyRequest;
import org.springframework.yarn.boot.actuate.endpoint.mvc.domain.ContainerClusterResource;
import org.springframework.yarn.boot.app.YarnContainerClusterOperations;
import org.springframework.yarn.boot.app.YarnContainerClusterTemplate;
import org.springframework.yarn.support.console.ContainerClusterReport.ClustersInfoReportData;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.yarn.boot.actuate.endpoint.mvc.AbstractContainerClusterRequest.ProjectionDataType;


/**
 * Created by alan on 11/27/16.
 */
@Slf4j
@Service
public class ExecutionUnitServiceImpl implements ExecutionUnitService {

    private static final String DEFAULT_PROJECTION_TYPE = "default";

    private static final long EXECUTION_UNIT_STARTED_TIMEOUT = 60 * 1000;

    @Autowired
    private YarnService yarnService;
    @Autowired
    private YarnClient yarnClient;
    @Autowired
    private ResourceManagerService resourceManagerService;
    @Autowired
    private HostInfoService hostInfoService;
    @Autowired
    private InstanceInfoService instanceInfoService;
    @Autowired
    private InstanceOperationService instanceOperationService;
    @Autowired
    private EnvironmentInfoService environmentInfoService;
    @Autowired
    private InstanceEnvironmentMappingService instanceEnvironmentMappingService;
    @Autowired
    private ResourceInnerService resourceInnerService;
    @Autowired
    private JobService jobService;
    @Autowired
    private SpringXDTemplateDecorator springXDOperations;

    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    private RestTemplate restTemplate = new RestTemplate(requestFactory);

    @Override
    public ClustersInfoReportData executionUnitGroupInfo(Long instanceId) {
        ApplicationId applicationId = yarnService.getXdApplicationId();
        InstanceInfo instanceInfo = instanceInfoService.getById(instanceId);
        if (instanceInfo == null) {
            throw new ServiceException(StatusCode.INSTANCE_INFO_NOT_EXISTS);
        }
        YarnContainerClusterOperations operations = buildClusterOperations(restTemplate, yarnClient, applicationId);
        String clusterId = assembleClusterId(instanceInfo);
        ContainerClusterResource response = operations.clusterInfo(clusterId);

        Integer pany = response.getGridProjection().getProjectionData().getAny();
        Map<String, Integer> phosts = response.getGridProjection().getProjectionData().getHosts();
        Map<String, Integer> pracks = response.getGridProjection().getProjectionData().getRacks();
        Integer sany = response.getGridProjection().getSatisfyState().getAllocateData().getAny();
        Map<String, Integer> shosts = response.getGridProjection().getSatisfyState().getAllocateData().getHosts();
        Map<String, Integer> sracks = response.getGridProjection().getSatisfyState().getAllocateData().getRacks();

        return new ClustersInfoReportData(
                response.getContainerClusterState().getClusterState().toString(),
                response.getGridProjection().getMembers().size(), pany, phosts, pracks, sany, shosts, sracks);
    }

    @Transactional
    @Override
    public Long executionUnitGroupCreate(ExecutionUnitGroup unit, String userId, String userName) {

        ResourceInner resourceInner = resourceInnerService.getLeftByProjectId(unit.getProjectId());
        if (resourceInner == null) {
            throw new ServiceException(StatusCode.RESOURCE_INNER_NOT_EXISTS);
        }

        if (resourceInner.getResourceCpu() < unit.getInstanceSize() * unit.getResourceTemplate().getResourceCpu()
                || resourceInner.getResourceMemory() < unit.getInstanceSize() * unit.getResourceTemplate().getResourceMemory()
                ) {
            throw new ServiceException(StatusCode.EXCEPTION_RESOURCENOTENOUGH);
        }

        String clusterId = assembleClusterId(unit);
        ApplicationId applicationId = yarnService.getXdApplicationId();

        // 新建Instance
        InstanceInfo instanceInfo = new InstanceInfo();
        instanceInfo.setProjectId(unit.getProjectId());
        instanceInfo.setGroupName(unit.getGroupName());
        instanceInfo.setInstanceSize(unit.getInstanceSize());
        instanceInfo.setResourceTemplateId(unit.getResourceTemplate().getResourceTemplateId());
        instanceInfo.setStatusCode(StatusCode.EXECUTION_UNIT_CREATED.getCode());
        instanceInfo.setResourceInnerId(unit.getResourceInnerId());
        instanceInfo.setCreateTime(new Date());
        instanceInfo.setUpdateTime(new Date());
        instanceInfo.setInstanceDesc(unit.getInstanceDesc());
        instanceInfoService.insert(instanceInfo);
        Long instanceId = instanceInfo.getInstanceId();

        // 调用xd, 创建执行单元组
        yarnService.doClusterCreate(applicationId,clusterId,unit.getResourceTemplate().getResourceName()
                ,unit.getInstanceSize(),null,userName);

        // 保存Instance与Environment mapping关系
        instanceEnvironmentMappingService.save(instanceId, unit.getEnvironmentIdList());

        // 写OpLog
        instanceOperationService.insert(instanceId, userId, userName, StatusCode.EXECUTION_UNIT_OP_CREATE);
        return instanceId;
    }

    @Transactional
    @Override
    public void executionUnitGroupModify(Long instanceId, Integer instanceSize, String instanceDesc, String userId, String userName) {
        InstanceInfoDO instanceInfoDO = instanceInfoService.getDOById(instanceId);
        ResourceInner left = resourceInnerService.getLeftByProjectId(instanceInfoDO.getProjectId());
        if (left == null) {
            throw new ServiceException(StatusCode.RESOURCE_INNER_NOT_EXISTS);
        }
        if (left.getResourceCpu() < (instanceSize - instanceInfoDO.getInstanceSize()) * instanceInfoDO.getResourceTemplate().getResourceCpu()
                || left.getResourceMemory() < (instanceSize - instanceInfoDO.getInstanceSize()) * instanceInfoDO.getResourceTemplate().getResourceMemory()
                ) {
            throw new ServiceException(StatusCode.EXCEPTION_RESOURCENOTENOUGH);
        }

        ApplicationId applicationId = yarnService.getXdApplicationId();

        InstanceInfo instanceInfo = instanceInfoService.getById(instanceId);
        // 根据instanceId反查出InstanceInfo
        if (instanceInfo == null) {
            throw new ServiceException(StatusCode.INSTANCE_INFO_NOT_EXISTS);
        }
        // 执行单元组当前处于'启动'或'销毁'状态时, 不允许修改
        if (!instanceInfo.getStatusCode().equals(StatusCode.EXECUTION_UNIT_STARTED.getCode())) {
            throw new ServiceException(StatusCode.EXECUTION_UNIT_CANNOT_MODIFY);
        }

        // 调用xd, 修改执行单元组
        String clusterId = assembleClusterId(instanceInfo);
        yarnService.doClusterModify(applicationId,clusterId,instanceSize,null);

        // 更新Instance, 写OpLog
        instanceInfo.setInstanceSize(instanceSize);
        instanceInfo.setInstanceDesc(instanceDesc);
        instanceInfoService.modify(instanceInfo);
        instanceOperationService.insert(instanceId, userId, userName, StatusCode.EXECUTION_UNIT_OP_MODIFY);
    }

    @Override
    public void executionUnitGroupStart(Long instanceId, String userId, String userName) {
        ApplicationId applicationId = yarnService.getXdApplicationId();

        // 根据instanceId反查出InstanceInfo
        InstanceInfo instanceInfo = instanceInfoService.getById(instanceId);
        if (instanceInfo == null) {
            throw new ServiceException(StatusCode.INSTANCE_INFO_NOT_EXISTS);
        }

        // 调用xd, 启动执行单元组
        String clusterId = assembleClusterId(instanceInfo);
        yarnService.doClusterStart(applicationId,clusterId);

        // 更新Instance为启动中状态, 写OpLog
        instanceInfo.setStatusCode(StatusCode.EXECUTION_UNIT_STARTING.getCode());
        instanceInfoService.modify(instanceInfo);
        instanceOperationService.insert(instanceId, userId, userName, StatusCode.EXECUTION_UNIT_OP_START);

        // yarn启动执行单元组是异步的，这里进行轮询查看xd上是否实际已启动容器，对于这个请求来说已变为同步的
        long startTime = System.currentTimeMillis();
        boolean continueFind = true;
        // 从xd上寻找已启动的执行单元组，找到则认为已启动成功
        while (continueFind) {
            // 设置启动超时时间
            if (System.currentTimeMillis() - startTime > EXECUTION_UNIT_STARTED_TIMEOUT) {
                instanceInfo.setStatusCode(StatusCode.EXECUTION_UNIT_STOPPED.getCode());
                instanceInfoService.modify(instanceInfo);
                throw new ServiceException(StatusCode.EXECUTION_UNIT_STARTED_TIMEOUT);
            }

            PagedResources<DetailedContainerResource> containers = springXDOperations.runtimeOperations().listContainers();

            for (DetailedContainerResource container : containers) {
                if (clusterId.equals(container.getGroups())) {
                    continueFind = false;
                    break;
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("", e);
            }
        }

        // 更新Instance启动完成状态, 写OpLog
        instanceInfo.setStatusCode(StatusCode.EXECUTION_UNIT_STARTED.getCode());
        instanceInfoService.modify(instanceInfo);
    }

    @Transactional
    @Override
    public void executionUnitGroupStop(Long instanceId, String userId, String userName) {
        //检查是否有正在执行的任务
        List<ExecutingJobInfo> executingJobInfos = jobService.getExecutingBatchJobByInstanceId(instanceId);
        if (CollectionUtils.isNotEmpty(executingJobInfos)){
            throw new ServiceException(StatusCode.EXECUTION_UNIT_HAS_EXECUTING_BATCHJOB);
        }
        List<ExecutingJobInfo> executingStreams = jobService.getExecutingStreamByInstanceId(instanceId);
        if (CollectionUtils.isNotEmpty(executingStreams)){
            throw new ServiceException(StatusCode.EXECUTION_UNIT_HAS_EXECUTING_STREAM);
        }
        ApplicationId applicationId = yarnService.getXdApplicationId();

        // 根据instanceId反查出InstanceInfo
        InstanceInfo instanceInfo = instanceInfoService.getById(instanceId);
        if (instanceInfo == null) {
            throw new ServiceException(StatusCode.INSTANCE_INFO_NOT_EXISTS);
        }

        // 调用xd, 停止执行单元组
        String clusterId = assembleClusterId(instanceInfo);
        yarnService.doClusterStop(applicationId,clusterId);

        // 更新Instance状态, 写OpLog
        instanceInfo.setStatusCode(StatusCode.EXECUTION_UNIT_STOPPED.getCode());
        instanceInfoService.modify(instanceInfo);
        instanceOperationService.insert(instanceId, userId, userName, StatusCode.EXECUTION_UNIT_OP_STOP);
    }

    @Transactional
    @Override
    public void executionUnitGroupDestroy(Long instanceId, String userId, String userName) {
        ApplicationId applicationId = yarnService.getXdApplicationId();

        // 根据instanceId反查出InstanceInfo
        InstanceInfo instanceInfo = instanceInfoService.getById(instanceId);
        if (instanceInfo == null) {
            throw new ServiceException(StatusCode.INSTANCE_INFO_NOT_EXISTS);
        }

        // 调用xd, 销毁执行单元组
        String clusterId = assembleClusterId(instanceInfo);
        log.info("Going to invoke yarn to <<DESTROY>> cluster..., applicationId: {}, clusterId: {}, instanceId: {}",
                applicationId, clusterId, instanceId);
        yarnService.doClusterDestroy(applicationId,clusterId);

        // 释放执行单元组资源
        resourceManagerService.destroyResource(instanceId);

        // 更新Instance状态, 写OpLog
        instanceInfo.setStatusCode(StatusCode.EXECUTION_UNIT_DESTROYED.getCode());
        instanceInfoService.modify(instanceInfo);
        instanceOperationService.insert(instanceInfo.getInstanceId(), userId, userName, StatusCode.EXECUTION_UNIT_OP_DESTROY);
    }

    public YarnContainerClusterOperations buildClusterOperations(RestTemplate restTemplate,
                                                                 YarnClient client, ApplicationId applicationId) {
        ApplicationReport report;
        try {
            report = client.getApplicationReport(applicationId);
        } catch (YarnException e) {
            log.error("yarn exception ", e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        } catch (IOException e) {
            log.error("yarn exception ", e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        }
        String trackingUrl = report.getOriginalTrackingUrl();
        return new YarnContainerClusterTemplate(trackingUrl + "/" +
                YarnContainerClusterEndpoint.ENDPOINT_ID, restTemplate);
    }

    public String assembleClusterId(ExecutionUnitGroup group) {
        return assembleClusterId(group.getProjectId(), group.getGroupName());
    }

    public String assembleClusterId(InstanceInfo instanceInfo) {
        return assembleClusterId(instanceInfo.getProjectId(), instanceInfo.getGroupName());
    }

    public String assembleClusterId(Long projectId, String groupName) {
        return String.format("%d_%s", projectId, groupName);
    }

}
