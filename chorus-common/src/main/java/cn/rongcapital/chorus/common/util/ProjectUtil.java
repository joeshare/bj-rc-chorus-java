package cn.rongcapital.chorus.common.util;

public class ProjectUtil {

    public static final String DEFAULT_PROJECT_HDFS_LOCATION_TEMPLATE   = "/chorus/project/%s";
    public static final String DEFAULT_PROJECT_TABLES_LOCATION_TEMPLATE = DEFAULT_PROJECT_HDFS_LOCATION_TEMPLATE + "/hive";
    public static final String DEFAULT_HIVE_TABLE_LOCATION_TEMPLATE     = DEFAULT_PROJECT_TABLES_LOCATION_TEMPLATE + "/%s";
    public static final String DEFAULT_HIVE_DB_NAME_TEMPLATE            = "chorus_%s";

    /**
     * @param projectCode
     *
     * @return
     *
     * @author yunzhong
     * @time 2017年6月21日上午11:36:15
     */
    public static String generateHiveDBName(String projectCode) {
        return String.format(DEFAULT_HIVE_DB_NAME_TEMPLATE, projectCode);
    }

    public static String hiveAllTablesLocation(String project) {
        return String.format(DEFAULT_PROJECT_TABLES_LOCATION_TEMPLATE, project);
    }

    public static String hiveTableLocation(String project, String table) {
        return String.format(DEFAULT_HIVE_TABLE_LOCATION_TEMPLATE, project, table);
    }

    public static String projectLocation(String project) {
        return String.format(DEFAULT_PROJECT_HDFS_LOCATION_TEMPLATE, project);
    }

    /**
     * @param value
     *
     * @return
     *
     * @author yunzhong
     * @time 2017年6月21日下午1:34:36
     */
    public static Long convertToLong(String value) {
        if (StringUtils.isEmpty(value)) {
            return 0L;
        }
        return Long.valueOf(value);
    }
}
