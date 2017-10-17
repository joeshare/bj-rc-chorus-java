package cn.rongcapital.chorus.monitor.ranger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component(PolicyProjectFinderFactory.HDFS_FINDER_BEAN_NAME)
@Slf4j
public class HdfsPolicyProjectFinder extends AbstractPolicyProjectFinder {

    private static final String SUPPORT_RESROUCE_PREFIX = "/chorus/project/";

    @Override
    protected boolean isSupportResource(String resource) {
        return resource != null && resource.startsWith(SUPPORT_RESROUCE_PREFIX);
    }

    @Override
    protected String getProjectCode(String resource) {
        return resource.split("/")[3];
    }
}
