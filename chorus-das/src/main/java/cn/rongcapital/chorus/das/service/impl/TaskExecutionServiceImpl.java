package cn.rongcapital.chorus.das.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.rongcapital.chorus.das.dao.TaskExecutionTimeoutMapper;
import cn.rongcapital.chorus.das.entity.TaskExecutionTimeout;
import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.das.service.TaskExecutionService;

@Component
public class TaskExecutionServiceImpl implements TaskExecutionService {

    @Autowired
    private TaskExecutionTimeoutMapper executionMapper;
    
    @Override
    public int insert(TaskExecutionTimeout timeout) {
       return executionMapper.insert(timeout);
    }

    @Override
    public Set<Long> filterNotified(List<XDExecution> xdExecutions) {
        return executionMapper.filterNotified(xdExecutions);
    }
}
