package cn.rongcapital.chorus.monitor.ranger;

import cn.rongcapital.chorus.das.entity.ProjectInfo;
import org.apache.tools.ant.Project;

public interface PolicyProjectFinder {

    /**
     * 根据审计信息，分析查找对应的Project信息
     *
     * @param auditModel
     * @return
     */
    ProjectInfo find(RangerAuditModel auditModel);

}
