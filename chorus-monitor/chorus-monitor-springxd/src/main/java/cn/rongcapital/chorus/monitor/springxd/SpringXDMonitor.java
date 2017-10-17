package cn.rongcapital.chorus.monitor.springxd;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.das.entity.InstanceInfo;
import cn.rongcapital.chorus.das.service.InstanceInfoService;
import cn.rongcapital.chorus.resourcemanager.service.RescueService;
import cn.rongcapital.chorus.resourcemanager.service.YarnService;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Spring XD 监控
 * Created by shicheng on 2017/4/11.
 *
 * @author wenbin
 * change modify logic
 */
@Component
@Slf4j
public class SpringXDMonitor {

    @Value(value = "${xd.zkBasePath}")
    private String monitorSpringXdZkPath;

    @Value(value = "${zookeeper.address}")
    private String zookeeperAddress;

    @Value(value = "${zookeeper.timeout}")
    private Integer zookeeperTimeout;

    @Value(value = "${monitor.spring.xd.zk.connect.retry.time}")
    private Integer retryTime = 1000;

    @Value(value = "${monitor.spring.xd.zk.connect.retry.count}")
    private Integer retryCount = 5;

    @Value(value = "${datasource.xd.driverClassName}")
    private String xdDriverClassName;

    @Value(value = "${datasource.xd.url}")
    private String xdUrl;

    @Value(value = "${datasource.xd.username}")
    private String xdUserName;

    @Value(value = "${datasource.xd.password}")
    private String xdPassword;

    @Autowired
    private InstanceInfoService instanceInfoService;

    @Autowired
    private TicketController controller;

    private final String zkSyncContainerStatusLockPath = "/chorus/xd/monitor";

    private final String zkSyncJobStatusLockPath = "/chorus/xd/job-monitor";

    //private final String zkSyncContainerClusterStatusLockPath = "/chorus/xd/cc-monitor";

//    private final String zkApplicationPath = "/chorus/xd/application";

    private final String zkAttemptPath = "/chorus/xd/attempt";

    private Boolean shouldCutTicket = false;

    private StringBuilder ticketDetail = new StringBuilder();

    private  ExecutorService pool;

    private CuratorFramework client;

    @Autowired
    private RescueService rescueService;

    @Autowired
    private YarnService yarnService;

    @Scheduled(cron = "${monitor.spring.xd.cron}")
    public void springMonitor() {
        InterProcessMutex interProcessMutex = null;

        log.info("start try to get sync lock .......");

        try {

            if((interProcessMutex = getJobLock())==null){
                log.warn("fail to get job lock");
                return;
            }

            log.info("start sync .......");

            shouldCutTicket = false;
            ticketDetail.setLength(0);

            autoRescueContainer();

            syncContainerStatus();

            //syncJobDeployStatus();

            //开 ticket
            if(shouldCutTicket){
                log.info("cut ticket......");
                log.info(ticketDetail.toString());
                controller.createTicket();
            }

            TimeUnit.SECONDS.sleep(10);

            log.info("sync end.......");
        } catch (Exception e) {
            log.error("sync container and job status exception.",e);
        }finally {
            if(interProcessMutex !=null){
                try {
                    interProcessMutex.release();
                } catch (Exception e) {
                    log.error("release lock error");
                }
            }
        }
    }


    private void syncContainerStatus(){
        log.info("starting sync containers' status....");

        Map<String, Integer> projectGroupInstanceCountMap = new HashMap<>();

        try {
            ApplicationId applicationId = yarnService.getXdApplicationId();
            log.info("current application id {}", applicationId.getId());

            getClient().checkExists().creatingParentContainersIfNeeded().forPath(zkAttemptPath);

            String attemptIds = getAttemptIdStr(applicationId);
            log.info("current attempt id {}", attemptIds);

            byte[] attemptIdBytes = getClient().getData().forPath(zkAttemptPath);
            String previousAttemptIds = new String(attemptIdBytes);
            log.info("previous attempt id {}", previousAttemptIds);

            if (!previousAttemptIds.equals(attemptIds)) {
                log.info("previous attempt id is not equals current attempt id, waiting for rescue container");
                return;
            }

            // 获取子节点
            String containers = monitorSpringXdZkPath + "/containers";
            // 获取所有 containers

            List<String> children = getClient().getChildren().forPath(containers);
            for(String child : children){
                log.info("get child : " +child);
                try {
                    // 获取每个 containers 信息
                    String result = new String(getClient().getData().storingStatIn(new Stat()).forPath(containers + "/" + child));
                    JSONObject object = JSONObject.parseObject(result);
                    String projectIdAndGroupName = object.getString("groups");

                    if (projectGroupInstanceCountMap.containsKey(projectIdAndGroupName)) {
                        projectGroupInstanceCountMap.put(projectIdAndGroupName,projectGroupInstanceCountMap.get(projectIdAndGroupName)+1);
                    } else {
                        projectGroupInstanceCountMap.put(projectIdAndGroupName,1);
                    }
                } catch (Exception e) {
                    log.error("analysis children error", e);
                }
            }
        } catch (ServiceException e){
            log.error("service exception ",e);
        } catch (Exception e) {
            log.error("get children error", e);
        }


        //把所有数据库中已启动的 instanc 和 zk 中的作对比
        //如果在 zk 中不存在,则更新数据库执行单元组为失败状态
        List<InstanceInfo> instanceInfos = instanceInfoService.getInstanceInfoByStatusCode(StatusCode.EXECUTION_UNIT_STARTED);
        if(instanceInfos!=null && !instanceInfos.isEmpty()){

            for(InstanceInfo info : instanceInfos){
                String projectId = info.getProjectId().toString();
                String groupName = info.getGroupName();
                Integer count = info.getInstanceSize();
                String combineKey = projectId+"_"+groupName;

                if(projectGroupInstanceCountMap.containsKey(combineKey)){
                    ticketDetail.append("<br/>执行单元组状态不一致<br/>");

                    Integer actualCount = projectGroupInstanceCountMap.get(combineKey);
                    //在 zk 和 数据库中都存在记录,但是执行单元组个数不一致
                    //暂时不修改数据库状态,等待 yarn 恢复
                    if(!actualCount.equals(count)){

                        StringBuilder temp = new StringBuilder();
                        temp.append("<br/>执行单元组:");
                        temp.append(projectId).append("_");
                        temp.append(groupName);
                        temp.append(" 数量不一致,等待 yarn 恢复,请调查原因<br/>");
                        temp.append(" 期望启动").append(count).append("个");
                        temp.append(" ,事件启动").append(actualCount).append("个<br/>");
                        log.info(temp.toString());

                        ticketDetail.append(temp.toString());

                        shouldCutTicket = true;
                    }
                }else{
                    //在 zk 中不存在记录,更新数据库记录, 执行单元组为失败状态

                    ticketDetail.append("<br/>执行单元组状态不一致<br/>");

                    InstanceInfo instanceInfo = new InstanceInfo();
                    instanceInfo.setInstanceId(info.getInstanceId());
                    instanceInfo.setStatusCode(StatusCode.EXECUTION_UNIT_STOPPED.getCode());
                    instanceInfoService.modify(instanceInfo);
                    StringBuilder temp = new StringBuilder();
                    temp.append("<br/>执行单元组:");
                    temp.append(projectId).append("_");
                    temp.append(groupName);
                    temp.append(" 已经失败,更新数据库为失败状态,请调查原因<br/>");
                    log.info(temp.toString());

                    ticketDetail.append(temp.toString());

                    shouldCutTicket = true;
                }
            }
        }

        //处理 zk 中有记录的 container,数据库的状态不是已启动的 container
        Set<Map.Entry<String, Integer>> entries = projectGroupInstanceCountMap.entrySet();
        for(Map.Entry<String, Integer> entry : entries){
            String projectIdAndGroupName = entry.getKey();
            if(projectIdAndGroupName.contains("_")){
                String project = projectIdAndGroupName.substring(0,projectIdAndGroupName.indexOf("_"));
                String groupName = projectIdAndGroupName.substring(projectIdAndGroupName.indexOf("_")+1);

                InstanceInfo instanceInfo = instanceInfoService.listByProjectIdAndGroup(Long.parseLong(project),groupName);
                if(instanceInfo==null){
                    log.warn("can not find record from data base about project {},group {}",project,groupName);
                    continue;
                }
                String statusCode = instanceInfo.getStatusCode();


                //更新数据库记录状态
                if(!statusCode.equals(StatusCode.EXECUTION_UNIT_STARTED.getCode())){
                    ticketDetail.append("<br/>执行单元组状态不一致<br/>");

                    StringBuilder temp = new StringBuilder();
                    temp.append("<br/>执行单元组:");
                    temp.append(project).append("_");
                    temp.append(groupName);
                    temp.append(" 已经启动,更新数据库为启动状态,请调查原因<br/>");
                    log.info(temp.toString());

                    InstanceInfo instanceInfoNew = new InstanceInfo();
                    instanceInfoNew.setInstanceId(instanceInfo.getInstanceId());
                    instanceInfoNew.setStatusCode(StatusCode.EXECUTION_UNIT_STARTED.getCode());
                    instanceInfoService.modify(instanceInfoNew);

                    ticketDetail.append(temp.toString());

                    shouldCutTicket = true;
                }

            }

        }
    }

    public void syncJobDeployStatus(){
        log.info("starting sync job deploy status....");

        // zookeeper数据和xd数据库数据同步 by maboxiao
        String deploymentJobs = monitorSpringXdZkPath + "/deployments/jobs";
        try {

            pool = Executors.newFixedThreadPool(1);

            final PathChildrenCache childrenCache = new PathChildrenCache(getClient(), deploymentJobs, true);
            childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

            childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            System.out.println("CHILD_ADDED: " + event.getData().getPath());
                            break;
                        case CHILD_REMOVED:
                            System.out.println("CHILD_REMOVED: " + event.getData().getPath());
                            deleteJobRegistry(event.getData().getPath());
                            break;
                        case CHILD_UPDATED:
                            System.out.println("CHILD_UPDATED: " + event.getData().getPath());
                            break;
                        default:
                            break;
                    }
                }
            }, pool);


        } catch (Exception e) {
            log.error("get children error", e);
        }
    }


    @PreDestroy
    public void cleanup(){
        if(pool!=null){
            pool.shutdown();
        }

        if(client!=null){
            CloseableUtils.closeQuietly(client);
        }
    }

    private void deleteJobRegistry(String jobName){
        Connection conn = null;
        Statement stmt = null;
        InterProcessMutex interProcessMutex =null;
        try {
            if((interProcessMutex = getLock(zkSyncJobStatusLockPath))==null){
                log.warn("fail to get lock at path {}",zkSyncJobStatusLockPath);
                return;
            }

            jobName = jobName.substring(jobName.lastIndexOf("/")+1);

            log.info("deleting job {} registry",jobName);

            Class.forName(xdDriverClassName);
            conn = DriverManager.getConnection(xdUrl, xdUserName, xdPassword);
            stmt = conn.createStatement();
            stmt.execute("DELETE FROM XD_JOB_REGISTRY WHERE JOB_NAME = " + "'" + jobName + "'");
            stmt.execute("DELETE FROM XD_JOB_REGISTRY_STEP_NAMES WHERE JOB_NAME = " +"'"+jobName + "'");
        } catch(ClassNotFoundException e) {
            log.error("找不到驱动程序类 ，加载驱动失败！",e);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        } finally {

            if(interProcessMutex!=null){
                try {
                    interProcessMutex.release();
                } catch (Exception e) {
                    log.error("release lock error",e);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    log.error("sql exception.",e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("sql exception.",e);
                }
            }
        }

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            log.error("thread interrupted",e);
        }
    }

    public void autoRescueContainer(){

        log.info("starting rescue container....");

        try {
            ApplicationId applicationId = yarnService.getXdApplicationId();
            log.info("current application id {}", applicationId.getId());

            getClient().checkExists().creatingParentContainersIfNeeded().forPath(zkAttemptPath);

            String currentAttemptIds = getAttemptIdStr(applicationId);
            log.info("current attempt id {}", currentAttemptIds);

            byte[] attemptIdBytes = getClient().getData().forPath(zkAttemptPath);
            String previousAttemptIds = new String(attemptIdBytes);
            log.info("previous attempt id {}", previousAttemptIds);

            if(!previousAttemptIds.equals(currentAttemptIds)){
                try {
                        log.info("rescue container....");

                        rescueService.rescueAfterXdRestarted();

                        getClient().setData().forPath(zkAttemptPath, currentAttemptIds.getBytes());

                } catch (Exception e) {
                    log.error("rescue container error",e);
                }
            }

        } catch (Exception e) {
            log.error("rescue container error",e);
        }

    }

    private String getAttemptIdStr(ApplicationId applicationId) throws Exception{
        List<ApplicationAttemptReport> applicationReports = yarnService.getAttemptIds(applicationId);
        List<String> list = new ArrayList<>();
        for(ApplicationAttemptReport report : applicationReports){
            list.add(report.getApplicationAttemptId().toString());
        }
        return Joiner.on(",").join(list);
    }

    @PostConstruct
    public void syncContainerAndJobStatus(){
        syncJobDeployStatus();
        //autoRescueContainer();
    }

    private InterProcessMutex getLock(String path){
        try {

            InterProcessMutex lock = new InterProcessMutex(getClient(),path);
            if(lock.acquire(5, TimeUnit.SECONDS))
                return lock;
        } catch (Throwable e) {
            log.error("get job lock exception.",e);
        }

        return null;

    }

    private InterProcessMutex getJobLock(){
        return getLock(zkSyncContainerStatusLockPath);
    }

    private CuratorFramework getClient(){
        if(client==null){
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(retryTime, retryCount);
            client = CuratorFrameworkFactory.builder().connectString(zookeeperAddress)
              .sessionTimeoutMs(zookeeperTimeout).retryPolicy(retryPolicy).build();

            client.start();
        }
        return client;
    }


}
