package cn.rongcapital.chorus.resourcemanager.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.yarn.boot.actuate.endpoint.YarnContainerClusterEndpoint;
import org.springframework.yarn.boot.actuate.endpoint.mvc.AbstractContainerClusterRequest.ProjectionDataType;
import org.springframework.yarn.boot.actuate.endpoint.mvc.ContainerClusterCreateRequest;
import org.springframework.yarn.boot.actuate.endpoint.mvc.ContainerClusterModifyRequest;
import org.springframework.yarn.boot.actuate.endpoint.mvc.domain.ContainerClusterResource;
import org.springframework.yarn.boot.actuate.endpoint.mvc.domain.YarnContainerClusterEndpointResource;
import org.springframework.yarn.boot.app.YarnContainerClusterOperations;
import org.springframework.yarn.boot.app.YarnContainerClusterTemplate;
import org.springframework.yarn.support.console.ContainerClusterReport.ClustersInfoReportData;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.resourcemanager.service.YarnService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by alan on 11/25/16.
 */
@Slf4j
@Service
public class YarnServiceImpl implements YarnService {

    private static final String XD_APPLICATION_TYPE = "XD";
    @Autowired
    private YarnClient yarnClient;
    private static final String DEFAULT_PROJECTION_TYPE = "default";

    private HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    private RestTemplate restTemplate = new RestTemplate(requestFactory);

    // @Cacheable(cacheNames = "XD_APPLICATION_ID_CACHE_KEY")
    @Override
    public ApplicationId getXdApplicationId() {
        log.info("Going to refresh xd applicationId...");
        List<ApplicationReport> runningApps = getRunningApps(XD_APPLICATION_TYPE);
        if (runningApps == null || runningApps.size() != 1) {
            throw new ServiceException(StatusCode.XD_STATUS_ERROR);
        }
        return runningApps.get(0).getApplicationId();
    }

    @Override
    public List<ApplicationReport> getRunningApps(String applicationType) {
        log.info("Going to get running applications from yarn, applicationType: {}.", applicationType);
        Set<String> types = new HashSet<>();
        types.add(applicationType);
        EnumSet<YarnApplicationState> status = EnumSet.of(YarnApplicationState.RUNNING);
        try {
            return yarnClient.getApplications(types, status);
        } catch (YarnException | IOException e) {
            log.error("yarn exception ", e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        }
    }

    @Override
    public List<ApplicationAttemptReport> getAttemptIds(ApplicationId applicationId){
        log.info("Going to get application attemptId from applicationId, applicationId: {}.", Integer.toString(applicationId.getId()));
        try {
            return yarnClient.getApplicationAttempts(applicationId);
        } catch (YarnException | IOException e) {
            log.error("yarn exception ", e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        }
    }

    @Override
    public List<ApplicationReport> getAllApps(String applicationType) {
        log.info("Going to get all applications from yarn, applicationType: {}.", applicationType);
        Set<String> types = new HashSet<>();
        types.add(applicationType);
        try {
            return yarnClient.getApplications(types);
        } catch (YarnException | IOException e) {
            log.error("yarn exception ", e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        }
    }

    @Override
    public List<NodeReport> getNodeReport() {
        log.info("Going to get nodes' report from yarn.");
        try {
            return yarnClient.getNodeReports(NodeState.RUNNING);
        } catch (YarnException | IOException e) {
            log.error("yarn exception ", e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        }
    }

    @Override
    public ClustersInfoReportData getClusterInfo(ApplicationId applicationId, String clusterId) {
        YarnContainerClusterOperations operations = buildClusterOperations(restTemplate, yarnClient, applicationId);
        ContainerClusterResource response = operations.clusterInfo(clusterId);

        Integer pany = response.getGridProjection().getProjectionData().getAny();
        Map<String, Integer> phosts = response.getGridProjection().getProjectionData().getHosts();
        Map<String, Integer> pracks = response.getGridProjection().getProjectionData().getRacks();
        Integer sany = response.getGridProjection().getSatisfyState().getAllocateData().getAny();
        Map<String, Integer> shosts = response.getGridProjection().getSatisfyState().getAllocateData().getHosts();
        Map<String, Integer> sracks = response.getGridProjection().getSatisfyState().getAllocateData().getRacks();

        return new ClustersInfoReportData(response.getContainerClusterState().getClusterState().toString(),
                response.getGridProjection().getMembers().size(), pany, phosts, pracks, sany, shosts, sracks);
    }

    @Override
    public void doClusterCreate(ApplicationId applicationId, String clusterId, String resourceName, int instanceSize,
            Map<String, Integer> hosts, String containerUser) {
        YarnContainerClusterOperations operations = buildClusterOperations(restTemplate, yarnClient, applicationId);
        ContainerClusterCreateRequest request = new ContainerClusterCreateRequest();
        request.setClusterId(clusterId);
        request.setClusterDef(resourceName);
        request.setProjection(DEFAULT_PROJECTION_TYPE);
        Map<String, Object> extraProperties = new HashMap<>();
        extraProperties.put("containerGroups", clusterId);
        extraProperties.put("containerUser", containerUser);
        request.setExtraProperties(extraProperties);
        ProjectionDataType projectionData = getProjectionDataType(instanceSize, hosts);
        request.setProjectionData(projectionData);
        log.info(
                "Going to invoke yarn to <<CREATE>> cluster...,"
                        + " applicationId: {}, clusterId: {}, template: {}, projectionType: {}, projectionData.any: {},"
                        + " projectionData.hosts: {}, extraProperties: {}",
                applicationId, clusterId, resourceName, DEFAULT_PROJECTION_TYPE, projectionData.getAny(),
                projectionData.getHosts(), extraProperties);
        operations.clusterCreate(request);
    }

    @Override
    public void doClusterModify(ApplicationId applicationId, String clusterId, int instanceSize,
            Map<String, Integer> hosts) {
        YarnContainerClusterOperations operations = buildClusterOperations(restTemplate, yarnClient, applicationId);
        ContainerClusterCreateRequest request = new ContainerClusterCreateRequest();
        request.setClusterId(clusterId);
        ProjectionDataType projectionData = getProjectionDataType(instanceSize, hosts);
        request.setProjectionData(projectionData);
        log.info(
                "Going to invoke yarn to <<MODIFY>> cluster...,"
                        + " applicationId: {}, clusterId: {}, projectionType: {}, projectionData.any: {},"
                        + " projectionData.hosts: {}",
                applicationId, clusterId, DEFAULT_PROJECTION_TYPE, projectionData.getAny(), projectionData.getHosts());
        operations.clusterModify(clusterId, request);
    }

    @Override
    public void doClusterStart(ApplicationId applicationId, String clusterId) {
        log.info("Going to invoke yarn to <<START>> cluster..., applicationId: {}, clusterId: {}", applicationId,
                clusterId);
        YarnContainerClusterOperations operations = buildClusterOperations(restTemplate, yarnClient, applicationId);
        ContainerClusterModifyRequest request = new ContainerClusterModifyRequest();
        request.setAction("start");
        operations.clusterStart(clusterId, request);
    }

    @Override
    public void doClusterStop(ApplicationId applicationId, String clusterId) {
        log.info("Going to invoke yarn to <<STOP>> cluster..., applicationId: {}, clusterId: {}", applicationId,
                clusterId);
        YarnContainerClusterOperations operations = buildClusterOperations(restTemplate, yarnClient, applicationId);
        ContainerClusterModifyRequest request = new ContainerClusterModifyRequest();
        request.setAction("stop");
        operations.clusterStop(clusterId, request);
    }

    @Override
    public void doClusterDestroy(ApplicationId applicationId, String clusterId) {
        log.info("Going to invoke yarn to <<DESTROY>> cluster..., applicationId: {}, clusterId: {}", applicationId,
                clusterId);
        YarnContainerClusterOperations operations = buildClusterOperations(restTemplate, yarnClient, applicationId);
        operations.clusterDestroy(clusterId);

        boolean destroyed = false;
        Long endTime = System.currentTimeMillis() + 60 * 1000L;
        long currentTime = System.currentTimeMillis();
        // 查询所有的container，如果结果中包含正在destroy的container，则认为destroy失败。超时等待一分钟
        while (currentTime < endTime) {
            try {
                YarnContainerClusterEndpointResource clustersResource = operations.getClusters();
                final Collection<String> clusters = clustersResource.getClusters();
                if (!CollectionUtils.isEmpty(clusters)) {
                    Set<String> clusterSet = new HashSet<String>(clusters);
                    if (clusterSet.contains(clusterId)) {
                        log.debug("waiting cluster {} to destroy.", clusterId);
                    } else {
                        log.debug("success to destroy cluster {}.", clusterId);
                        destroyed = true;
                        break;
                    }
                } else {
                    log.info("There is no active cluster.");
                    destroyed = true;
                    break;
                }
            } catch (Exception e1) {
                log.warn("Failed to get destroying cluster [" + clusterId + "] info.", e1);
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
            }
            currentTime = System.currentTimeMillis();
        }
        if (!destroyed) {
            log.error("Failed to destroy cluster {}", clusterId);
            throw new ServiceException(StatusCode.EXECUTION_UNIT_DESTROY_ERROR);
        }
    }

    private YarnContainerClusterOperations buildClusterOperations(RestTemplate restTemplate, YarnClient client,
            ApplicationId applicationId) {
        ApplicationReport report;
        try {
            report = client.getApplicationReport(applicationId);
        } catch (YarnException | IOException e) {
            log.error("yarn exception ", e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        }
        String trackingUrl = report.getOriginalTrackingUrl();
        return new YarnContainerClusterTemplate(trackingUrl + "/" + YarnContainerClusterEndpoint.ENDPOINT_ID,
                restTemplate);
    }

    private ProjectionDataType getProjectionDataType(int instanceSize, Map<String, Integer> hosts) {
        ProjectionDataType projectionData = new ProjectionDataType();
        projectionData.setAny(instanceSize);
        projectionData.setHosts(hosts);
        projectionData.setRacks(null);
        projectionData.setProperties(null);
        return projectionData;
    }

}
