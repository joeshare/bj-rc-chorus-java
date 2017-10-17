package cn.rongcapital.chorus.das.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import cn.rongcapital.chorus.das.entity.TaskExecutionTimeout;
import cn.rongcapital.chorus.das.entity.XDExecution;

public interface TaskExecutionTimeoutMapper {

    /**
     * @param timeout
     * @author yunzhong
     * @time 2017年6月26日下午4:39:22
     */
    int insert(@Param("timeout")TaskExecutionTimeout timeout);

    /**
     * 返回已经有记录的结果集。
     * 
     * @param xdExecutions
     * @return
     * @author yunzhong
     * @time 2017年6月27日下午5:17:14
     */
    Set<Long> filterNotified(@Param("xdExecutions")List<XDExecution> xdExecutions);

}
