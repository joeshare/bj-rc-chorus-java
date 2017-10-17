package cn.rongcapital.chorus.das.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import cn.rongcapital.chorus.das.entity.XDExecution;

public interface JobFailHistoryMapper {

    /**
     * @param executions
     * @return
     * @author yunzhong
     * @version
     * @since 2017年5月18日
     */
    Set<String> selectExecutionIds(@Param("executions") List<XDExecution> executions);

    /**
     * @param xdExecution
     * @return
     * @author yunzhong
     * @version
     * @since 2017年5月18日
     */
    int insert(@Param("xdExecution") XDExecution xdExecution);
}
