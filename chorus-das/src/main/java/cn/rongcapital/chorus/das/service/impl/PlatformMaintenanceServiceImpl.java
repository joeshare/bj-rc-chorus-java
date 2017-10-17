package cn.rongcapital.chorus.das.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.xd.rest.domain.JobExecutionInfoResource;

import com.google.gson.Gson;

import cn.rongcapital.chorus.common.constant.CommonAttribute;
import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.CollectionUtils;
import cn.rongcapital.chorus.common.util.DateUtils;
import cn.rongcapital.chorus.common.xd.XDClient;
import cn.rongcapital.chorus.das.dao.CommonInfoMapper;
import cn.rongcapital.chorus.das.dao.JobUnexecutedMapper;
import cn.rongcapital.chorus.das.entity.ApplicationInfo;
import cn.rongcapital.chorus.das.entity.CommonInfoDO;
import cn.rongcapital.chorus.das.entity.JobUnexecutedDO;
import cn.rongcapital.chorus.das.entity.UnexecutedJob;
import cn.rongcapital.chorus.das.service.PlatformMaintenanceService;
import cn.rongcapital.chorus.das.xd.dao.XDMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * 平台维护服务类
 * @author kevin.gong
 * @Time 2017年9月19日 上午11:36:45
 */
@Slf4j
@Service("PlatformMaintenanceService")
public class PlatformMaintenanceServiceImpl implements PlatformMaintenanceService {
    @Autowired
    private CommonInfoMapper commonInfoMapper;
    
    @Autowired
    private JobUnexecutedMapper jobUnexecutedMapper;
    
    @Autowired
    private XDClient xdClient;
    
    @Autowired
    private YarnClient yarnClient;
    
    @Autowired
    private XDMapper xdMapper;
    
    @Autowired
    private CuratorFramework curator;
    
    /**
     * 上次application信息
     */
    private final String ZK_PREAPPLICATION_PATH = "/chorus/xd/preappinfo";
    
    private static final String XD_APPLICATION_TYPE = "XD";
    
    private static final Gson gson = new Gson();
    
    /**
     * 需要重新执行的任务
     */
    private static final int JOB_NEED_RERUN = 0;
    
    /**
     * 未重新执行的任务
     */
    private static final int JOB_UNEXEC = 0;

    @Override
    public int getPlatformMaintenanceStatus() {
        CommonInfoDO commonInfoDO = commonInfoMapper.selectByUserIdAndAttrId(CommonAttribute.ALL_CHORUS_USER_ID, CommonAttribute.MAINTENANCE_STATUS);
        return commonInfoDO == null? 0: Integer.parseInt(commonInfoDO.getValue());
    }

    @Override
    public boolean setPlatformMaintenanceStatus(int status) {
        CommonInfoDO commonInfoDO = new CommonInfoDO();
        commonInfoDO.setUserId(CommonAttribute.ALL_CHORUS_USER_ID);
        commonInfoDO.setAttributeId(CommonAttribute.MAINTENANCE_STATUS);
        commonInfoDO.setValue(String.valueOf(status));
        if(commonInfoMapper.update(commonInfoDO) > 0) {
            if(status == 0) {
                //维护完成时，需要执行（1）维护期间的定时任务（2）由于xd重启未执行完任务
                executeWaitJobs();
                restartXDStartedJob();
            }
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 执行待执行任务
     */
    private void executeWaitJobs() {
        List<JobUnexecutedDO> list = jobUnexecutedMapper.selectJobUnexecuted(JOB_NEED_RERUN, JOB_UNEXEC);
        if(CollectionUtils.isNotEmpty(list)) {
            Set<String> jobNames = list.stream().map(JobUnexecutedDO::getJobName).collect(Collectors.toSet());
            for (String jobName : jobNames) {
                try {
                    xdClient.lanuchJob(jobName, null);
                    jobUnexecutedMapper.updateWaitExecuteJobStatus(jobName);
                } catch (Exception e) {
                    log.error("lanuch待执行任务异常,jobName:{}", jobName, e);
                }
            }
        }
    }

    @Override
    public List<UnexecutedJob> getWaitExecuteJobs(Integer pageNum, Integer pageSize) {
        return jobUnexecutedMapper.selectWaitExecuteJobs((pageNum-1)*pageSize, pageSize);
    }
    
    @Override
    public int getWaitExecuteJobsCount() {
        return jobUnexecutedMapper.selectWaitExecuteJobsCount();
    }
    
    /**
     * 获取前一次application信息
     */
    private ApplicationInfo getPreviousAppInfo() {
        try {
            if(curator.checkExists().forPath(ZK_PREAPPLICATION_PATH) == null) {
                curator.create().creatingParentContainersIfNeeded().forPath(ZK_PREAPPLICATION_PATH, null);
            }
            
            byte[] data = curator.getData().forPath(ZK_PREAPPLICATION_PATH);
            if(data != null) {
                return gson.fromJson(new String(data), ApplicationInfo.class); 
            }
        } catch (Exception e) {
            log.error("get previous application info error", e);
        }
        return null;
    }
    
    /**
     * 获取当前application信息
     */
    private ApplicationReport getCurrentAppInfo() {
        List<ApplicationReport> runningApps = getRunningApps(XD_APPLICATION_TYPE);
        if (runningApps == null || runningApps.size() != 1) {
            log.info("get current application info fail！");
            return null;
        } else {
            return runningApps.get(0);
        }
    }
    
    /**
     * 保存application信息到zk
     */
    private void setAppInfo(ApplicationReport appinfo) throws Exception {
        ApplicationInfo appli = new ApplicationInfo();
        appli.setAppId(appinfo.getApplicationId().getId());
        appli.setStartTime(appinfo.getStartTime());
        curator.setData().forPath(ZK_PREAPPLICATION_PATH, gson.toJson(appli).getBytes());
    }

    /**
     * 重启由于xd重启未执行完任务
     */
    private void restartXDStartedJob() {
        try {
            ApplicationReport currApp = getCurrentAppInfo();
            if(currApp == null) {
                log.info("未获取到本次application信息，不进行未执行完任务重启！");
                return;
            }
            
            ApplicationInfo preApp = getPreviousAppInfo();
            if(preApp == null || preApp.getAppId() == null) {
                log.info("未获取到上次application信息，不进行未执行完任务重启！");
                setAppInfo(currApp);
                return;
            }
            
            if(preApp.getAppId() == currApp.getApplicationId().getId()) {
                log.info("本次application信息与上次一致，不进行未执行完任务重启！");
                return;
            }
            
            String preStartTime = DateUtils.format(new Date(preApp.getStartTime()), "yyyy-MM-dd HH:mm:ss");
            String currStartTime = DateUtils.format(new Date(currApp.getStartTime()), "yyyy-MM-dd HH:mm:ss");
            List<Long> jobExecIds = xdMapper.getRunningJobExecIdAtStopXDTime(preStartTime, currStartTime);
            if(!CollectionUtils.isEmpty(jobExecIds)) {
                log.info("开始恢复由于xd重启，导致未执行完成的任务！");
                for (Long executionId : jobExecIds) {
                    try {
                        JobExecutionInfoResource jer = xdClient.displayJobExecution(executionId);
                        //删除random属性，否则lanuchjob无法执行
                        jer.getJobParameters().remove("random");
                        xdClient.lanuchJob(jer.getName(), jer.getJobParameters()==null?null:gson.toJson(jer.getJobParameters()));
                        log.info("执行任务name:{}，对应executionId:{}!", jer.getName(), executionId);
                    } catch (Exception e) {
                        log.error("重启未执行完任务失败,executionId:{}", executionId, e);
                    }
                }
            }
            
            setAppInfo(currApp);
            
        } catch (Exception e) {
            log.error("restartXDStartedJob error",e);
        }
    }
    
    private List<ApplicationReport> getRunningApps(String applicationType) {
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
}
