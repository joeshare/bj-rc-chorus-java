package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.OperLog;
import cn.rongcapital.chorus.das.entity.web.OperLogCause;

import java.util.List;

public interface OperLogService {
	/**
	 * 保存操作日志信息
	 * @param fromTable
	 * @param operLog
	 * @param recordKey
     * @param userId
	 */
    void save(String fromTable, String operLog, String recordKey, String userId);

    /**
     * 没有session信息的情况下保存日志(如任务调度的情况下)
     *
     * @param fromTable
     * @param operLog
     * @param recordKey
     */
    void saveWithoutSession(String fromTable, String operLog, String recordKey);
    /**
     * 根据recordKey,页索引获取分页日志信息
     * @param id
     * @return
     */
    @Deprecated
    List<OperLog> getLogList(String id, int pageindex);
    /**
     * 根据recordKey获取日志数
     * @param id
     * @return
     */
    @Deprecated
    int getLogCount(String id);
    /**
     * 分页查询日志信息
     * @param operLogCause
     * @return
     */
    List<OperLog> getLogList(OperLogCause operLogCause);
    /**
     * 根据recordKey获取日志数
     * @param operLogCause
     * @return
     */ 
    int getLogCount(OperLogCause operLogCause);
    /**
     * 处理session或无session状态的日志记录
     * @param fromTable
     * @param operLog
     * @param recordKey
     * @param userId
     */
    public void saveSessionOrNot(String fromTable, String operLog, String recordKey, String userId);

}
