package cn.rongcapital.chorus.resourcemanager.service;

import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.springframework.yarn.support.console.ContainerClusterReport.ClustersInfoReportData;

import java.util.List;
import java.util.Map;

/**
 * Created by alan on 11/25/16.
 */
public interface YarnService {

    /**
     * 获取xd on yarn的applicationId.
     *
     * @return applicationId
     */
    ApplicationId getXdApplicationId();

    List<ApplicationReport> getRunningApps(String applicationType);

    List<ApplicationReport> getAllApps(String applicationType);

    List<NodeReport> getNodeReport();

    ClustersInfoReportData getClusterInfo(ApplicationId applicationId, String clusterId);

    void doClusterCreate(ApplicationId applicationId, String clusterId, String resourceName,int instanceSize, Map<String, Integer> hosts, String containerUser);

    void doClusterModify(ApplicationId applicationId, String clusterId,int instanceSize, Map<String, Integer> hosts);

    void doClusterStart(ApplicationId applicationId, String clusterId);

    void doClusterStop(ApplicationId applicationId, String clusterId);

    void doClusterDestroy(ApplicationId applicationId, String clusterId);

    List<ApplicationAttemptReport> getAttemptIds(ApplicationId applicationId);
}
