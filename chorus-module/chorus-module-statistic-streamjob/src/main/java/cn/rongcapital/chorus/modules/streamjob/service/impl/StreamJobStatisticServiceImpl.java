package cn.rongcapital.chorus.modules.streamjob.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

import cn.rongcapital.chorus.modules.streamjob.dao.ChorusDao;
import cn.rongcapital.chorus.modules.streamjob.entity.Job;
import cn.rongcapital.chorus.modules.streamjob.entity.StreamExecStatistic;
import cn.rongcapital.chorus.modules.streamjob.service.StreamJobStatisticService;
import lombok.extern.slf4j.Slf4j;

/**
 * 流式任务统计实现类
 * @author kevin.gong
 * @Time 2017年8月9日 上午10:07:25
 */
@Service(value = "StreamJobStatisticService")
@Slf4j
public class StreamJobStatisticServiceImpl implements StreamJobStatisticService {
    
    private static final String STREAM_STATUS_UNDEPLOY = "UNDEPLOY";
    
    private static final String STREAM_STATUS_DEPLOY = "deployed";
    
    @Autowired
    private ChorusDao chorusDao;
    
    private Integer retryTime = 1000;

    private Integer retryCount = 1000;

    private CuratorFramework client;
    
    /**
     * 流式任务统计
     * @throws SQLException 
     */
    @Override
    public void jobStatistic(Connection conn, String monitorSpringXdZkPath, String zookeeperAddress, int zookeeperTimeout) throws SQLException{
        log.info("start streamJob statistic...");
        List<Job> streamJobStatuslist = chorusDao.getAllStreamJob(conn);
        if(!CollectionUtils.isEmpty(streamJobStatuslist)) {
            addStreamJobExecInfoYest(conn, streamJobStatuslist, monitorSpringXdZkPath, zookeeperAddress, zookeeperTimeout);
        }
        log.info("end streamJob statistic...");
    }
    
    /**
     * 整理流式任务昨天的统计结果，并添加到数据库
     * @param streamJobStatuslist 数据库取出的chorus流式任务记录
     * @throws SQLException 
     */
    private void addStreamJobExecInfoYest(Connection conn, List<Job> streamJobStatuslist, String monitorSpringXdZkPath, String zookeeperAddress, int zookeeperTimeout) throws SQLException{
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
                addStreamJobStatisticToDB(conn, projectId, noExecNum, failedNum, runningNum);
                projectId = proId;
                noExecNum = 0;
                failedNum = 0;
                runningNum = 0;
            }
            
            String status = job.getStatus();
            if(STREAM_STATUS_UNDEPLOY.equals(status)){
                noExecNum ++;
                continue;
            }
            
            String jobName = job.getJobName();
            status = getStreamJobStatus(jobName, monitorSpringXdZkPath, zookeeperAddress, zookeeperTimeout);
            if(StringUtils.isEmpty(status) || STREAM_STATUS_DEPLOY.equals(status)){
                runningNum ++;
            } else {
                failedNum ++;
            }
        }
        
        addStreamJobStatisticToDB(conn, projectId, noExecNum, failedNum, runningNum);
    }
    
    /**
     * 将Stream统计数据添加到list中
     * @param projectId 项目编号
     * @param noExecNum 未执行任务数量
     * @param failedNum 失败任务数量
     * @param runningNum 正在任务执行数量
     * @throws SQLException 
     */
    private void addStreamJobStatisticToDB(Connection conn, long projectId, int noExecNum, int failedNum, int runningNum) throws SQLException{
        StreamExecStatistic streamExecStatistic = new StreamExecStatistic();
        streamExecStatistic.setProjectId(projectId);
        streamExecStatistic.setNoExecNum(noExecNum);
        streamExecStatistic.setFailedNum(failedNum);
        streamExecStatistic.setRunningNum(runningNum);
        chorusDao.addStreamJobStatistic(conn, streamExecStatistic);
    }
    
    /**
     * 到zookeeper上抓取stream的状态
     * @param jobName 任务名
     * @return 任务状态
     */
    private String getStreamJobStatus(String jobName, String monitorSpringXdZkPath, String zookeeperAddress, int zookeeperTimeout){
        String status = null;
        String nodePath = getStreamStatusNode(jobName, monitorSpringXdZkPath);
        try {
            if(getClient(zookeeperAddress, zookeeperTimeout).checkExists().forPath(nodePath) != null){
                String nodeValue = new String(getClient(zookeeperAddress, zookeeperTimeout).getData().forPath(nodePath));
                if(!StringUtils.isEmpty(nodeValue)) {
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
    private String getStreamStatusNode(String jobName, String monitorSpringXdZkPath){
        StringBuilder nodePath = new StringBuilder(monitorSpringXdZkPath);
        nodePath.append("/deployments/streams/");
        nodePath.append(jobName);
        nodePath.append("/status");
        return nodePath.toString();
    }
    
    private CuratorFramework getClient(String zookeeperAddress, int zookeeperTimeout){
        if(client==null){
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(retryTime, retryCount);
            client = CuratorFrameworkFactory.builder().connectString(zookeeperAddress)
              .sessionTimeoutMs(zookeeperTimeout).retryPolicy(retryPolicy).build();

            client.start();
        }
        return client;
    }

}
