package cn.rongcapital.chorus.das.service;

import java.util.List;
import java.util.Set;

import cn.rongcapital.chorus.das.entity.TaskExecutionTimeout;
import cn.rongcapital.chorus.das.entity.XDExecution;

public interface TaskExecutionService {

    /**
     * @param timeout
     * @author yunzhong
     * @time 2017年6月26日下午4:36:08
     */
    int insert(TaskExecutionTimeout timeout);
    
    /**
     * @param xdExecutions
     * @return
     * @author yunzhong
     * @time 2017年6月27日下午5:16:39
     */
    Set<Long> filterNotified(List<XDExecution> xdExecutions);
}
