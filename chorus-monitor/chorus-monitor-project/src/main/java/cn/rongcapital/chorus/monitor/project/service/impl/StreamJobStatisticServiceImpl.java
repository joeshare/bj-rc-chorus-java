package cn.rongcapital.chorus.monitor.project.service.impl;

import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import cn.rongcapital.chorus.common.util.CollectionUtils;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.StreamExecStatistic;
import cn.rongcapital.chorus.das.service.JobService;
import cn.rongcapital.chorus.das.service.StreamExecStatisticService;
import cn.rongcapital.chorus.monitor.project.constant.Const;
import cn.rongcapital.chorus.monitor.project.service.StreamJobStatisticService;
import lombok.extern.slf4j.Slf4j;

/**
 * 流式任务统计实现类
 * @author kevin.gong
 * @Time 2017年6月22日 上午10:07:25
 */
@Service(value = "StreamJobStatisticService")
@Slf4j
public class StreamJobStatisticServiceImpl implements StreamJobStatisticService {
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private StreamExecStatisticService streamExecStatisticService;
    
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

    private CuratorFramework client;
    
    /**
     * 流式任务统计
     */
    @Override
    public void jobStatistic(){
        log.info("start streamJob statistic...");
        List<Job> streamJobStatuslist = jobService.getAllStreamJob();
        if(CollectionUtils.isNotEmpty(streamJobStatuslist)) {
            addStreamJobExecInfoYest(streamJobStatuslist);
        }
        log.info("end streamJob statistic...");
    }
    
    /**
     * 整理流式任务昨天的统计结果，并添加到数据库
     * @param streamJobStatuslist 数据库取出的chorus流式任务记录
     */
    private void addStreamJobExecInfoYest(List<Job> streamJobStatuslist){
        //项目编号
        long projectId = streamJobStatuslist.get(0).getProjectId();
        //项目对应未执行次数
        int noExecNum = 0;
        //项目对应执行失败次数
        int failedNum = 0;
        //项目对应正在执行次数
        int runningNum = 0;
        for (Job job : streamJobStatuslist) {
            int proId = job.getProjectId();
            //streamJobStatuslist内部按project排序。所以不相等时，表示项目变更，保存已计算数据。然后开始重新计算新项目数据
            if(projectId != proId) {
                addStreamJobStatisticToDB(projectId, noExecNum, failedNum, runningNum);
                projectId = proId;
                noExecNum = 0;
                failedNum = 0;
                runningNum = 0;
            }
            
            String status = job.getStatus();
            if(Const.STREAM_STATUS_UNDEPLOY.equals(status)){
                noExecNum ++;
                continue;
            }
            
            String jobName = job.getJobName();
            status = getStreamJobStatus(jobName);
            if(StringUtils.isEmpty(status) || Const.STREAM_STATUS_DEPLOY.equals(status)){
                runningNum ++;
            } else {
                failedNum ++;
            }
        }
        
        addStreamJobStatisticToDB(projectId, noExecNum, failedNum, runningNum);
    }
    
    /**
     * 将Stream统计数据添加到list中
     * @param projectId 项目编号
     * @param noExecNum 未执行任务数量
     * @param failedNum 失败任务数量
     * @param runningNum 正在任务执行数量
     */
    private void addStreamJobStatisticToDB(long projectId, int noExecNum, int failedNum, int runningNum){
        StreamExecStatistic streamExecStatistic = new StreamExecStatistic();
        streamExecStatistic.setProjectId(projectId);
        streamExecStatistic.setNoExecNum(noExecNum);
        streamExecStatistic.setFailedNum(failedNum);
        streamExecStatistic.setRunningNum(runningNum);
        streamExecStatisticService.addStreamJobExecStatistic(streamExecStatistic);
    }
    
    /**
     * 到zookeeper上抓取stream的状态
     * @param jobName 任务名
     * @return 任务状态
     */
    private String getStreamJobStatus(String jobName){
        String status = null;
        String nodePath = getStreamStatusNode(jobName);
        try {
            if(getClient().checkExists().forPath(nodePath) != null){
                String nodeValue = new String(getClient().getData().forPath(nodePath));
                if(StringUtils.isNotEmpty(nodeValue)) {
                    JSONObject object = JSONObject.parseObject(nodeValue);
                    status = object.getString("state");
                }
            } 
        } catch (Exception e) {
            log.error("JOB STATISTIC - GET ZOOKEEPER NODE EXCEPTION! nodePath = {}", nodePath, e);
        } 
        return status;
    }
    
    /**
     * 获取zookeeper上任务状态节点路径
     * @param jobName 任务名
     * @return 节点路径
     */
    private String getStreamStatusNode(String jobName){
        StringBuilder nodePath = new StringBuilder(monitorSpringXdZkPath);
        nodePath.append("/deployments/streams/");
        nodePath.append(jobName);
        nodePath.append("/status");
        return nodePath.toString();
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
