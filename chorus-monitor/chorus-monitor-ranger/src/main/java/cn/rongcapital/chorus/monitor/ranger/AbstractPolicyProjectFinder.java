package cn.rongcapital.chorus.monitor.ranger;

import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractPolicyProjectFinder implements PolicyProjectFinder {

    protected abstract boolean isSupportResource(String resource);

    protected abstract String getProjectCode(String resource);

    @Autowired
    private ProjectInfoService projectInfoService;

    @Override
    public ProjectInfo find(RangerAuditModel auditModel) {
        String resource = auditModel.getResource();
        if (!isSupportResource(resource)) {
            log.warn("Unsupport  resource: {}. Can not find associate project.", resource);
            return null;
        }
        log.debug("Try to find project code from resource {}.", resource);
        String projectCode = getProjectCode(resource);
        return projectInfoService.selectByProjectCode(projectCode);
    }

}
