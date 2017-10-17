package cn.rongcapital.chorus.monitor.project.constant;

/**
 * 常量类
 * @author kevin.gong
 * @Time 2017年6月22日 上午9:57:06
 */
public class Const {

    /**
     * 项目编号 project_id
     */
    public static final String KEY_PROJECT_ID = "project_id";
    /**
     * 任务别名 job_alias_name
     */
    public static final String KEY_JOB_ALIAS_NAME = "job_alias_name";
    /**
     * 流式任务状态 - 未部署(数据库记录值)
     */
    public static final String STREAM_STATUS_UNDEPLOY = "UNDEPLOY";
    /**
     * 流式任务状态 - 已部署 (zookeeper记录值)
     */
    public static final String STREAM_STATUS_DEPLOY = "deployed";
    /**
     * 每天开始时间点 - 00:00:00
     */
    public static final String DAY_BEGIN_TIME = " 00:00:00";
    /**
     * 每天结束时间点 - 23:59:59
     */
    public static final String DAY_END_TIME = " 23:59:59";
    /**
     * 统计任务开始时间 - 01:00:00
     */
    public static final String STATISTIC_START_TIME = " 01:00:00";
}
