package cn.rongcapital.chorus.common.constant;

public class ChorusConstants {
	/**
	 * COLON：冒号
	 */
	public static final String COLON = ":";
	/**
	 * SLASH：斜线
	 */
	public static final String SLASH = "/";
	/**
	 * DOT：点
	 */
	public static final String DOT = ".";

	/**
	 * COMMA：逗号
	 */
	public static final String COMMA = ",";

	/**
	 * EQUAL：等号
	 */
	public static final String EQUAL = "=";

	/**
	 * AND：&运算符
	 */
	public static final String AND = "&";

	/**
	 * EMPTY：空字符
	 */
	public static final String EMPTY = "";

	/**
	 * HIVE_QUERY_LIMIT_COUNT：Hive数据最大查询数量
	 */
	public static final int HIVE_QUERY_LIMIT_COUNT = 100;

	/**
	 * HIVE_CREATE_TYPE_RDB：RDB导入创建
	 */
	public static final String HIVE_CREATE_TYPE_RDB = "1";

	/**
	 * HIVE_CREATE_TYPE_MANUAL：用户手动创建
	 */
	public static final String HIVE_CREATE_TYPE_MANUAL = "2";

	/**
	 * HIVE_CREATE_TYPE_IMPORT：引用其他用户
	 */
	public static final String HIVE_CREATE_TYPE_IMPORT = "3";

	/**
	 * HIVE_LOG_IMPORT_TYPE：日志导入
	 */
	public static final String HIVE_LOG_IMPORT_TYPE = "4";

	/**
	 * DEFAULT_FILED_SEPARATOR：默认字段分隔符
	 */
	public static final String DEFAULT_FILED_SEPARATOR = "\001";

	/**
	 * HIVE_TABLE_TYPE_INTERNAL：Hive表类型内部表
	 */
	public static final String HIVE_TABLE_TYPE_INTERNAL = "internal";

	/**
	 * HIVE_TABLE_TYPE_EXTERNAL：Hive表类型外部表
	 */
	public static final String HIVE_TABLE_TYPE_EXTERNAL = "external";

	/**
	 * HIVE_TABLE_TYPE_ES：Hive表类型ElasticSearch映射表
	 */
	public static final String HIVE_TABLE_TYPE_ES = "es";

	/**
	 * SYS_USER_Y：系统管理员
	 */
	public static final String SYS_USER_Y = "Y";

	/**
	 * SYS_USER_Y：系统管理员以外
	 */
	public static final String SYS_USER_N = "N";

	/**
	 * CHART_TITLE_TASK：图形Title(任务耗时排名)
	 */
	public static final String CHART_TITLE_TASK = "任务耗时排名";

	/**
	 * CHART_TITLE_STATISTICS：图形Title(账号任务统计)
	 */
	public static final String CHART_TITLE_STATISTICS = "子账号任务统计(周期/实时)";

	/**
	 * CHART_LEGEND_RUNTIME：图例的数据数组
	 */
	public static final String CHART_LEGEND_RUNTIME = "耗时";

	/**
	 * CHART_YAXIS_NAME_RUNTIME：坐标轴名称(耗时(秒))
	 */
	public static final String CHART_YAXIS_NAME_RUNTIME = "耗时(秒)";

	/**
	 * CHART_YAXIS_TYPE_VALUE：坐标轴类型(value)
	 */
	public static final String CHART_YAXIS_TYPE_VALUE = "value";

	/**
	 * CHART_COLOR_DEFAULT：图例颜色(石青色)
	 */
	public static final String CHART_COLOR_DEFAULT = "#27727B";

	/**
	 * CHART_SERIES_NAME：系列名称
	 */
	public static final String CHART_SERIES_NAME = "耗时";

	/**
	 * CHART_SERIES_TYPE_BAR：系列类型(柱状)
	 */
	public static final String CHART_SERIES_TYPE_BAR = "bar";

	/**
	 * CHART_ORIENT_VERTICAL：布局方式
	 */
	public static final String CHART_ORIENT_VERTICAL = "vertical";

	/**
	 * CHART_LEFT：水平安放位置
	 */
	public static final String CHART_LEFT = "left";

	/**
	 * FUNC_DEFAULT_SELECTED：菜单默认赋予用户
	 */
	public static final String FUNC_DEFAULT_SELECTED = "1";

	/**
	 * FUNC_SETTING：菜单权限管理员设定
	 */
	public static final String FUNC_SETTING = "2";

	/**
	 * GROUP_TYPE_TASK：业务类型 1:HADOOP任务计算
	 */
	public static final int GROUP_TYPE_TASK = 1;

	/**
	 * GROUP_TYPE_HIVE_ONCE：业务类型2:hdfs数据同步一次性
	 */
	public static final int GROUP_TYPE_HIVE_ONCE = 2;

	/**
	 * GROUP_TYPE_MAPPING：业务类型 3:KAFKA数据导入
	 */
	public static final int GROUP_TYPE_MAPPING = 3;

	/**
	 * GROUP_TYPE_REAL_CAL：业务类型 4:KAFKA数据实时计算
	 */
	public static final int GROUP_TYPE_REAL_CAL = 4;

	/**
	 * GROUP_TYPE_HIVE_SYNC：业务类型5.hdfs数据同步周期性
	 */
	public static final int GROUP_TYPE_HIVE_SYNC = 5;

	/**
	 * DEF_TREE_NODE_ICON：菜单树默认图标
	 */
	public static final String DEF_TREE_NODE_ICON = "fa fa-folder";

	/**
	 * TREE_NODE_TYPE_TABLE: 血缘关系Table节点
	 */
	public static final int TREE_NODE_TYPE_TABLE = 0;

	/**
	 * TREE_NODE_TYPE_TABLE: 血缘关系Job节点
	 */
	public static final int TREE_NODE_TYPE_JOB = 1;

}
