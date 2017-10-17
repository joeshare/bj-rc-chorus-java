package cn.rongcapital.chorus.das.service;

import java.util.List;
import java.util.Set;

import cn.rongcapital.chorus.das.entity.XDExecution;

public interface JobFailHistoryService {

    /**
     * @param executions
     * @return
     * @author yunzhong
     * @version
     * @since 2017年5月17日
     */
    Set<String> selectExecutionIds(List<XDExecution> executions);

    /**
     * 
     * @param xdExecution
     * @return
     * @author yunzhong
     * @version
     * @since 2017年5月18日
     */
    int insert(XDExecution xdExecution);
}
