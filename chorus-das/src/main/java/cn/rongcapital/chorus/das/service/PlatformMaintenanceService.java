package cn.rongcapital.chorus.das.service;

import java.util.List;

import cn.rongcapital.chorus.das.entity.UnexecutedJob;

/**
 * 维护服务类接口
 * @author kevin.gong
 * @Time 2017年9月19日 上午11:28:16
 */
public interface PlatformMaintenanceService {

    /**
     * 获取平台维护状态
     * @return 0：未维护 ， 1：维护中
     */
    int getPlatformMaintenanceStatus();
    
    /**
     * 设置平台维护状态
     * @param status 平台维护状态（0：未维护 ， 1：维护中）
     * @return true：成功； false：失败
     */
    boolean setPlatformMaintenanceStatus(int status);
    
    /**
     * 获取待执行任务列表
     * @param pageNum 页码
     * @param pageSize 页数量
     * @return 待执行任务列表
     */
    List<UnexecutedJob> getWaitExecuteJobs(Integer pageNum, Integer pageSize);
    
    /**
     * 获取待执行任务数量
     * @return 待执行任务数量
     */
    int getWaitExecuteJobsCount();
}
