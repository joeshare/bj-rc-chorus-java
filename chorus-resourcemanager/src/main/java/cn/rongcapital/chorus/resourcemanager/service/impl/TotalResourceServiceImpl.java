package cn.rongcapital.chorus.resourcemanager.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.das.dao.TotalResourceDOMapper;
import cn.rongcapital.chorus.das.entity.TotalResource;
import cn.rongcapital.chorus.das.service.QueueService;
import cn.rongcapital.chorus.resourcemanager.service.HdfsService;
import cn.rongcapital.chorus.resourcemanager.service.TotalResourceService;
import cn.rongcapital.chorus.resourcemanager.service.YarnService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alan on 05/01/2017.
 */
@Service
@Slf4j
public class TotalResourceServiceImpl implements TotalResourceService {

    @Autowired
    private YarnService yarnService;
    @Autowired
    private HdfsService hdfsService;
    @Autowired
    private QueueService queueService;
    @Autowired
    private TotalResourceDOMapper totalResourceMapper;

    private static TotalResource emptyResource() {
        return new TotalResource() {{
            setCpu(0);
            setMemory(0);
            setStorage(0);
        }};
    }

    @Override
    public TotalResource sumResourceByStatus(StatusCode statusCode) {
        return totalResourceMapper.sumResourceByStatus(statusCode.getCode());
    }

    @Override
    public TotalResource getTotalResource() {
        List<NodeReport> nodeReportList = yarnService.getNodeReport();
        TotalResource res = nodeReportList.stream()
                .map(nr -> {
                    TotalResource nodeResource = new TotalResource();
                    nodeResource.setCpu(nr.getCapability().getVirtualCores());
                    nodeResource.setMemory(nr.getCapability().getMemory() / 1024);
                    nodeResource.setStorage(0);
                    return nodeResource;
                })
                .reduce(emptyResource(), (pre, thiz) -> {
                    pre.setCpu(pre.getCpu() + thiz.getCpu());
                    pre.setMemory(pre.getMemory() + thiz.getMemory());
                    return pre;
                });
        int storage = hdfsService.getTotalCapacity();
        res.setStorage(storage);
        log.info("Got total resource from yarn, cpu: {}, memory: {}, storage {} .",
                res.getCpu(), res.getMemory(),res.getStorage());
        return res;
    }

    @Override
    public TotalResource getTotalResourceWithQueueCapacity(){
        TotalResource total = getTotalResource();
        QueueInfo chorusQueue = null;
        try {
            chorusQueue = queueService.getChorusQueue();
        } catch (Exception e) {
            log.error(StatusCode.SYSTEM_ERR.getDesc(), e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        }
        float capacity = chorusQueue.getCapacity();
        int storage = hdfsService.getTotalCapacity();

        TotalResource totalWithQueue = new TotalResource();
        totalWithQueue.setCpu((int) Math.floor(total.getCpu() * capacity));
        totalWithQueue.setMemory((int) Math.floor(total.getMemory() * capacity));
        totalWithQueue.setStorage(storage);

        return totalWithQueue;
    }

    @Override
    public TotalResource getLeftResource() {
        TotalResource total = getTotalResourceWithQueueCapacity();
        TotalResource used = sumResourceByStatus(StatusCode.RESOURCE_APPROVE);
        TotalResource left = total;
        if (used != null) {
            left.setCpu(total.getCpu() - used.getCpu());
            left.setMemory(total.getMemory() - used.getMemory());
            left.setStorage(total.getStorage() - used.getStorage());
        }
        log.info("Calculated left resource, cpu: {}, memory: {}, storage {} .",
                left.getCpu(), left.getMemory(),left.getStorage());
        return left;
    }

    @Override
    public TotalResource getLeftResourceWithQueueCapacity(){
        TotalResource totalWithQueue = getTotalResourceWithQueueCapacity();
        TotalResource used = sumResourceByStatus(StatusCode.RESOURCE_APPROVE);
        TotalResource left = totalWithQueue;
        if (used != null) {
            left.setCpu(totalWithQueue.getCpu() - used.getCpu());
            left.setMemory(totalWithQueue.getMemory() - used.getMemory());
            left.setStorage(totalWithQueue.getStorage() - used.getStorage());
        }
        log.info("Calculated left resource, cpu: {}, memory: {}, storage {} .",
                left.getCpu(), left.getMemory(),left.getStorage());
        return left;
    }
}
