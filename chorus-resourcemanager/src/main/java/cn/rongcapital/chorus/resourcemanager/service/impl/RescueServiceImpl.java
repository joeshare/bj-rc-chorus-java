package cn.rongcapital.chorus.resourcemanager.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.entity.InstanceInfoWithHostsDO;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.service.InstanceInfoService;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.resourcemanager.service.ExecutionUnitService;
import cn.rongcapital.chorus.resourcemanager.service.RescueService;
import cn.rongcapital.chorus.resourcemanager.service.YarnService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.yarn.boot.app.YarnContainerClusterClientException;
import org.springframework.yarn.support.console.ContainerClusterReport;

import java.util.List;

/**
 * Created by alan on 12/8/16.
 */
@Slf4j
@Service
public class RescueServiceImpl implements RescueService {
    @Autowired
    private InstanceInfoService instanceInfoService;
    @Autowired
    private ExecutionUnitService executionUnitService;
    @Autowired
    private YarnService yarnService;
    @Autowired
    private ProjectInfoService projectInfoService;

    @Override
    public void rescueAfterXdRestarted() {
            log.info("rescue after restart");
            ApplicationId applicationId = yarnService.getXdApplicationId();
            List<InstanceInfoWithHostsDO> instances = instanceInfoService.listByStatusCode(
                    StatusCode.EXECUTION_UNIT_CREATED,
                    StatusCode.EXECUTION_UNIT_STARTED,
                    StatusCode.EXECUTION_UNIT_STOPPED,
                    StatusCode.EXECUTION_UNIT_STARTING);
            instances.forEach(instance -> {
                String clusterId = executionUnitService.assembleClusterId(instance.getProjectId(),
                        instance.getGroupName());

                ContainerClusterReport.ClustersInfoReportData reportData = null;
                try {

                    reportData = yarnService.getClusterInfo(applicationId,clusterId);

                    log.info("project {},group {}, db status {}, yarn status",instance.getProjectId(),instance.getGroupName(),instance.getStatusCode(),reportData.getState());


                } catch (YarnContainerClusterClientException e) {
                    log.error("get container cluster error.",e);
                    log.error("err message {}",e.getMessage());
                    if(e.getMessage().contains("404")) {
                        log.error("container cluster does not exist,rescue");
                        log.warn("rescue containers of project {}, group {}", instance.getProjectId(), instance.getGroupName());
                        ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(instance.getProjectId());
                        yarnService.doClusterCreate(applicationId, clusterId, instance.getResourceTemplate().getResourceName(),
                         instance.getInstanceSize(), null,projectInfo.getUserName());

                        if (instance.getStatusCode().equals(StatusCode.EXECUTION_UNIT_STARTED.getCode()) ||
                                instance.getStatusCode().equals(StatusCode.EXECUTION_UNIT_STARTING.getCode())) {
                            yarnService.doClusterStart(applicationId, clusterId);
                        }
                    }else{
                        throw new RuntimeException("get cluster info error."+e.getMessage());
                    }
                }


            });
    }

    @Override
    public void rescueAfterXdRestarted(Long projectId) {
        ApplicationId applicationId = yarnService.getXdApplicationId();
        List<InstanceInfoWithHostsDO> instances = instanceInfoService.listByProjectIdAndStatusCode(
                projectId,
                StatusCode.EXECUTION_UNIT_CREATED,
                StatusCode.EXECUTION_UNIT_STARTED,
                StatusCode.EXECUTION_UNIT_STOPPED,
                StatusCode.EXECUTION_UNIT_STARTING);
        instances.forEach(instance -> {
            String clusterId = executionUnitService.assembleClusterId(instance.getProjectId(),
                    instance.getGroupName());
            ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(instance.getProjectId());
            yarnService.doClusterCreate(applicationId, clusterId, instance.getResourceTemplate().getResourceName(),
                    instance.getInstanceSize(),null,projectInfo.getUserName());

            if (instance.getStatusCode().equals(StatusCode.EXECUTION_UNIT_STARTED.getCode()) ||
                    instance.getStatusCode().equals(StatusCode.EXECUTION_UNIT_STARTING.getCode())) {
                yarnService.doClusterStart(applicationId, clusterId);
            }
        });
    }

}
