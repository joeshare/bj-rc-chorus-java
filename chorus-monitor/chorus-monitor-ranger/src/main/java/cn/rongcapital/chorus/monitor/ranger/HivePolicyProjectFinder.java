package cn.rongcapital.chorus.monitor.ranger;

import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component(PolicyProjectFinderFactory.HIVE_FINDER_BEAN_NAME)
@Slf4j
public class HivePolicyProjectFinder extends AbstractPolicyProjectFinder {

    private static final String SUPPORT_RESROUCE_PREFIX = "chorus_";

    protected String getProjectCode(String resource) {
        String[] paths = resource.split("/");
        String firstPath = paths[0];
        return firstPath.substring(SUPPORT_RESROUCE_PREFIX.length());
    }

    protected boolean isSupportResource(String resource) {
        return resource != null && resource.startsWith(SUPPORT_RESROUCE_PREFIX);
    }

}
