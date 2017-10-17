package cn.rongcapital.chorus.das.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.rongcapital.chorus.common.ambari.conf.model.AmbariQueueConf;
import cn.rongcapital.chorus.common.ambari.conf.model.AmbariSchedulerCapacity;
import cn.rongcapital.chorus.common.ambari.conf.model.Clusters;
import cn.rongcapital.chorus.common.ambari.conf.model.QConfig;
import cn.rongcapital.chorus.common.constant.HttpMethodType;
import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.HttpUtil;
import cn.rongcapital.chorus.common.util.JsonUtils;
import cn.rongcapital.chorus.das.dao.ProjectInfoMapper;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.ResourceOperation;
import cn.rongcapital.chorus.das.entity.TotalResource;
import cn.rongcapital.chorus.das.service.QueueService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lovett
 */
@Slf4j
@Service
public class QueueServiceImpl implements QueueService{
    @Autowired
    private YarnClient yarnClient;
    @Autowired
    private ProjectInfoMapper projectMapper;
    @Value("${ambari.host.address}")
    private String ambariAddress;
    @Value("${ambari.user.name}")
    private String username;
    @Value("${ambari.user.password}")
    private String password;
    @Value("${ambari.host.name}")
    private String ambariHostName;
    @Value("${yarn.resource.manager}")
    private String yarnResourceManager;

    private final static String CHORUS_QUEUE = "chorus";
    //集群预留队列用于分配
    private final static String RESERVED_QUEUE = "reserved";

    @Override
    public void createQueue(ResourceOperation resourceOperation,TotalResource totalResource){
        QueueInfo chorus_queue = null;
        QueueInfo reserved_queue = null;
        QueueInfo projectQueue = null;
        ProjectInfo project = projectMapper.selectByPrimaryKey(resourceOperation.getProjectId());
        String projectCode = project.getProjectCode();
        try {
            chorus_queue = yarnClient.getQueueInfo(CHORUS_QUEUE);
            reserved_queue = yarnClient.getQueueInfo(RESERVED_QUEUE);
            projectQueue = yarnClient.getQueueInfo(projectCode);
        } catch (Exception e) {
            log.error(StatusCode.SYSTEM_ERR.getDesc(), e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        }

        if (chorus_queue != null && reserved_queue != null) {
            float chorus_cap = chorus_queue.getCapacity();
            float reserved_cap = reserved_queue.getCapacity();

            Integer t_cpu = totalResource.getCpu();
            Integer t_memory = totalResource.getMemory();

            Integer r_cpu = resourceOperation.getCpu();
            Integer r_memory = resourceOperation.getMemory();
            float c = (float) r_cpu / t_cpu;
            float m = (float) r_memory / t_memory;
            //比例取大的（确保用户资源够用）向上取整
            int q_percent = (int) Math.ceil((c >= m ? c * 100 : m * 100));

            if (q_percent <= reserved_cap * 100) {
                try {
                    if(null != projectQueue){
                        reAssignQueueConfig(projectCode,q_percent,(int) (reserved_cap * 100));
                    }else{
                        addQueueConfig(projectCode, q_percent, (int) (reserved_cap * 100));
                    }
                } catch (Exception e) {
                    log.error(StatusCode.QUEUE_CREATE_ERROR.getDesc());
                    throw new ServiceException(StatusCode.SYSTEM_ERR);
                }
            } else {
                log.error(StatusCode.EXCEPTION_RESOURCENOTENOUGH.getDesc());
                throw new ServiceException(StatusCode.EXCEPTION_RESOURCENOTENOUGH);
            }
        }else{
            ServiceException exception = new ServiceException(StatusCode.SYSTEM_ERR);
            log.error("Missing default chorus or reserved ",exception);
            throw exception;
        }

    }

    public void canceledQueue(Long projectId){
        QueueInfo chorus_queue = null;
        QueueInfo reserved_queue = null;
        QueueInfo projectQueue = null;
        ProjectInfo project = projectMapper.selectByPrimaryKey(projectId);
        String projectCode = project.getProjectCode();
        try {
            chorus_queue = yarnClient.getQueueInfo(CHORUS_QUEUE);
            reserved_queue = yarnClient.getQueueInfo(RESERVED_QUEUE);
            projectQueue = yarnClient.getQueueInfo(projectCode);
        } catch (Exception e) {
            log.error(StatusCode.SYSTEM_ERR.getDesc(), e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        }

        if (projectQueue == null) {
            return;
        }

        if (chorus_queue != null && reserved_queue != null) {
            float reserved_cap = reserved_queue.getCapacity();
            float project_cap = projectQueue.getCapacity();

            if (0 != project_cap) {
                // 将项目队列比例置为 0
                int q_percent = 0;
                int reserved_percent = (int) (reserved_cap * 100) + (int) (project_cap * 100);

                try {
                    emptyQueueConfig(projectCode, q_percent, reserved_percent);
                } catch (Exception e) {
                    log.error(StatusCode.QUEUE_CREATE_ERROR.getDesc());
                    throw new ServiceException(StatusCode.SYSTEM_ERR);
                }
            }
        }else{
            ServiceException exception = new ServiceException(StatusCode.SYSTEM_ERR);
            log.error("Missing default chorus or reserved ",exception);
            throw exception;
        }
    }

    private void emptyQueueConfig(String projectCode, int q_percent, int reserved_cap) throws Exception{
        String p_url = "http://"+ ambariAddress +"/api/v1/views/CAPACITY-SCHEDULER/versions/1.0.0/instances/AUTO_CS_INSTANCE/resources/scheduler/configuration";
        String response = HttpUtil.doPostAmbariWithAuthorization(p_url, HttpMethodType.GET, null, username, password);

        AmbariSchedulerCapacity schedulerCapacity = JsonUtils.Json2Object(response, AmbariSchedulerCapacity.class);
        Map<String, String> properties = schedulerCapacity.getItems().get(0).getProperties();

        String reserved_capacity = String.valueOf(reserved_cap);
        String q_capacity = String.valueOf(q_percent);
        String q_maximum_capacity = "0";

        properties.put("yarn.scheduler.capacity.root.chorus." + RESERVED_QUEUE + ".capacity", reserved_capacity);
        properties.put("yarn.scheduler.capacity.root.chorus." + projectCode + ".capacity",q_capacity);
        properties.put("yarn.scheduler.capacity.root.chorus."+ projectCode +".maximum-capacity", q_maximum_capacity);

        generateConfigJson(properties);
    }

    private void reAssignQueueConfig(String projectCode, int q_percent, int reserved_cap) throws Exception{
        String p_url = "http://"+ ambariAddress +"/api/v1/views/CAPACITY-SCHEDULER/versions/1.0.0/instances/AUTO_CS_INSTANCE/resources/scheduler/configuration";
        String response = HttpUtil.doPostAmbariWithAuthorization(p_url, HttpMethodType.GET, null, username, password);

        AmbariSchedulerCapacity schedulerCapacity = JsonUtils.Json2Object(response, AmbariSchedulerCapacity.class);
        Map<String, String> properties = schedulerCapacity.getItems().get(0).getProperties();

        String reserved_capacity = String.valueOf(reserved_cap - q_percent);
        String current_cap = properties.get("yarn.scheduler.capacity.root.chorus." + projectCode + ".capacity");
        String q_capacity = String.valueOf(Integer.valueOf(current_cap) + q_percent);
        String q_maximum_capacity = String.valueOf( (Integer.valueOf(q_capacity) < 50) ? Integer.valueOf(q_capacity)*2 : 100 );

        properties.put("yarn.scheduler.capacity.root.chorus." + RESERVED_QUEUE + ".capacity", reserved_capacity);
        properties.put("yarn.scheduler.capacity.root.chorus." + projectCode + ".capacity",q_capacity);
        properties.put("yarn.scheduler.capacity.root.chorus."+ projectCode +".maximum-capacity", q_maximum_capacity);

        generateConfigJson(properties);
    }

    private void addQueueConfig(String projectCode, int q_percent,int reserved_cap) throws Exception {
        String p_url = "http://"+ ambariAddress +"/api/v1/views/CAPACITY-SCHEDULER/versions/1.0.0/instances/AUTO_CS_INSTANCE/resources/scheduler/configuration";
        String response = HttpUtil.doPostAmbariWithAuthorization(p_url, HttpMethodType.GET, null, username, password);

        AmbariSchedulerCapacity schedulerCapacity = JsonUtils.Json2Object(response, AmbariSchedulerCapacity.class);
        Map<String, String> properties = schedulerCapacity.getItems().get(0).getProperties();

        String queuesName = properties.get("yarn.scheduler.capacity.root.chorus.queues") + "," + projectCode;
        String q_capacity = String.valueOf(q_percent);
        String q_maximum_capacity = String.valueOf( (q_percent < 50) ? q_percent*2 : 100 );
        String update_capacity = String.valueOf(reserved_cap - q_percent);

        properties.put("yarn.scheduler.capacity.root.chorus.queues", queuesName);
        properties.put("yarn.scheduler.capacity.root.chorus."+ RESERVED_QUEUE +".capacity", update_capacity);
        properties.put("yarn.scheduler.capacity.root.chorus."+ projectCode +".acl_administer_queue", "*");
        properties.put("yarn.scheduler.capacity.root.chorus."+ projectCode +".acl_submit_applications", "*");
        properties.put("yarn.scheduler.capacity.root.chorus."+ projectCode +".capacity", q_capacity);
        properties.put("yarn.scheduler.capacity.root.chorus."+ projectCode +".maximum-capacity", q_maximum_capacity);
        properties.put("yarn.scheduler.capacity.root.chorus."+ projectCode +".minimum-user-limit-percent", "100");
        properties.put("yarn.scheduler.capacity.root.chorus."+ projectCode +".ordering-policy", "fifo");
        properties.put("yarn.scheduler.capacity.root.chorus."+ projectCode +".state", "RUNNING");
        properties.put("yarn.scheduler.capacity.root.chorus."+ projectCode +".user-limit-factor", "1");

        generateConfigJson(properties);
    }

    private void generateConfigJson(Map<String, String> properties) throws Exception {
        String tag = "version" + String.valueOf(System.currentTimeMillis());

        AmbariQueueConf ambariQueueConf = new AmbariQueueConf();
        Clusters Clusters = new Clusters();
        QConfig qconfig = new QConfig();

        qconfig.setTag(tag);
        qconfig.setType("capacity-scheduler");
        qconfig.setProperties(properties);

        List<QConfig> desiredConfigs = new ArrayList<>();
        desiredConfigs.add(qconfig);
        Clusters.setDesired_config(desiredConfigs);
        ambariQueueConf.setClusters(Clusters);

        String jsonString = JsonUtils.convet2Json(ambariQueueConf);

        allocateQueue(jsonString);

        log.info("--------update queue scheduler capacity configuration :" + jsonString);
    }

    private void allocateQueue(String payLoad) {
        String p_url = "http://" + ambariAddress + "/api/v1/clusters/" + ambariHostName;

        HttpUtil.doPostAmbariWithAuthorization(p_url, HttpMethodType.PUT, payLoad, username, password);
        refreshQueueConfig();
    }

    private void refreshQueueConfig() {
        String p_url = "http://"+ambariAddress+"/api/v1/clusters/"+ambariHostName+"/requests";
        String payLoad = new String(""
                +"{ "
                +"\"RequestInfo\" : {"
                +"\"command\" : \"REFRESHQUEUES\","
                +"\"context\" : \"Refresh YARN Capacity Scheduler\","
                +"\"forceRefreshConfigTags\" : \"capacity-scheduler\""
                +"},"
                +"\"Requests/resource_filters\": ["
                +"{"
                +"\"service_name\" : \"YARN\","
                +"\"component_name\" : \"RESOURCEMANAGER\","
                +"\"hosts\" : \""+yarnResourceManager+"\""
                +"}"
                +"]"
                +"}"
                );
        HttpUtil.doPostAmbariWithAuthorization(p_url, HttpMethodType.POST, payLoad, username, password);
    }

    @Override
    public QueueInfo getChorusQueue() throws Exception {
        return yarnClient.getQueueInfo(CHORUS_QUEUE);
    }

    @Override
    public QueueInfo getReservedQueue() throws Exception {
        return yarnClient.getQueueInfo(RESERVED_QUEUE);
    }

}
