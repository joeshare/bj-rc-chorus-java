package cn.rongcapital.chorus.das.service;

import org.apache.hadoop.yarn.api.records.QueueInfo;

import cn.rongcapital.chorus.das.entity.ResourceOperation;
import cn.rongcapital.chorus.das.entity.TotalResource;
/**
 * @author Lovett
 */
public interface QueueService {
    // 自动化创建Queue
    void createQueue(ResourceOperation resourceOperation, TotalResource totalResource);

    QueueInfo getChorusQueue() throws Exception;

    QueueInfo getReservedQueue() throws Exception;
    /**
     * 注销队列（队列比率置为 0）
     * @param projectCode 项目编码
     */
    void canceledQueue(Long projectId);
}
