package cn.rongcapital.chorus.monitor.ranger;

import cn.rongcapital.chorus.authorization.plugin.ranger.RangerUtils;
import cn.rongcapital.chorus.common.util.BeanUtils;
import cn.rongcapital.chorus.common.util.SpringBeanUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PolicyProjectFinderFactory {

    public static final String HIVE_FINDER_BEAN_NAME = "hivePolicyProjectFinder";
    public static final String HDFS_FINDER_BEAN_NAME = "hdfsPolicyProjectFinder";

    /**
     * 根据审计repo类型获取对应的Finder
     *
     * @param repoType
     * @return
     */
    public static PolicyProjectFinder getFinder(String repoType) {
        log.debug("Get finder instance for type {}.", repoType);
        RangerUtils rangerUtils = SpringBeanUtils.getBean(RangerUtils.class);
        if (rangerUtils.rangerRepositoryName.equalsIgnoreCase(repoType)) {
            return SpringBeanUtils.getBean(HIVE_FINDER_BEAN_NAME, PolicyProjectFinder.class);
        } else if (rangerUtils.getRangerHdfsRepositoryName().equalsIgnoreCase(repoType)) {
            return SpringBeanUtils.getBean(HDFS_FINDER_BEAN_NAME, PolicyProjectFinder.class);
        } else {
            log.warn("Can not get finder. Repository type {} is not support yet.", repoType);
            return null;
        }
    }


}
