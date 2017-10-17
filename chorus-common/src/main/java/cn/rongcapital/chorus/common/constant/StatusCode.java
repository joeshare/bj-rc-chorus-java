package cn.rongcapital.chorus.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abiton on 15/11/2016.
 */
public enum StatusCode {

    /** 1-999 系统预留 **/
    SUCCESS("0", "成功"),
    SYSTEM_ERR("-1", "系统异常"),

    EXCEPTION_NOSUCHSTATUSCODE("0001", "没有该状态码"),
    EXCEPTION_RESOURCENOTENOUGH("0002", "资源不够"),
    PARAM_ERROR("0003", "参数传递错误"),

    XD_STATUS_ERROR("0010", "Yarn中xd的运行状况出现异常"),
    ZK_ALLOCATE_ERROR("0011", "zk分配资源异常"),
    ZK_CANNOT_GETLOCK_ERROR("0011", "zk分配资源异常"),
    
    QUEUE_CREATE_ERROR("0040","队列创建失败"),

    /** 1000-1999 项目管理和表管理 **/

    RECORD_NOT_EXISTS("1200", "记录不存在"),
    PROJECT_NOT_EXISTS("1201", "项目不存在"),
    PROJECT_EXISTS_DELETED("1202", "项目已经存在，但为删除状态"),
    PROJECT_EXISTS("1203", "项目已经存在"),
    PROJECT_OWNER_NOT_EXISTS("1204","项目owner不存在"),
    PROJECT_IS_OPENED("1205","项目为打开状态"),
    PROJECT_IS_DELETED("1206","项目为删除状态"),
    PROJECT_IS_DISABLED("1207","项目为不可用状态"),

    PROJECT_DTALAB_UNCOLSED_ERROR("1231","存在未关闭或停止的实验室，请处理后再删除"),
    PROJECT_JOB_ONLINE_ERROR("1232","存在未下线的任务，请处理后再删除"),
    PROJECT_DTATLAB_JOB_ONLINE_ERROR("1233","存在未关闭的实验室和任务，请处理后再删除"),
    PROJECT_RESOURCE_RELEASE_ERROR("1230","项目资源释放异常"),
    PROJECT_RESOURCE_RELEASE_CODE_ERROR("1234","提供的操作不存在，1:commit,0:rollback"),
    PROJECT_MEMBER_AUTH_ERROR("1235","用户无删除项目权限"),


    TABLE_NOT_EXISTS("1301", "表不存在"),
    TABLE_NAME_DUPLICATE("1302", "表名重复"),
    COLUMN_TYPE_NOT_SUPPORTED("1303", "列类型不支持"),

    TABLE_CREATED("1310", "已创建"),
    COLUMN_CREATED("1311", "已创建"),
    TABLE_DELETED("1312", "已删除"),

    DATABASE_NOT_EXISTS("1402", "数据库不存在"),
    DATABASE_CONN_FAIL("1403", "服务器连接失败"),
    HIVE_EXECUTION_FAIL("1404", "Hive执行错误"),

    APPLY_SUBMITTED("1501", "待审批"),
    APPLY_UNTREATED("1502", "全通过"),
    APPLY_TREATED("1503", "全拒绝"),

    /**2000-2999 项目资源与执行单元**/
    RESOURCE_OPERATE_APPLY("2000", "资源申请待审批"),
    RESOURCE_OPERATE_APPROVE("2001", "资源申请通过"),
    RESOURCE_OPERATE_DENY("2002", "资源申请拒绝"),

    RESOURCE_APPROVE("2003", "已分配"),
    RESOURCE_DESTROY("2004", "已销毁"),
    
    PROJECT_HDFS_PATH_CREATE_ERROR("2005","项目路径创建失败"),
    PROJECT_HDFS_PATH_QUOTA_ERROR("2006","项目路径容量设置失败"),
    PROJECT_HDFS_ALLOW_SNAPSHOT_ERROR("2007","项目HDFS Snapshot开启失败"),

    EXECUTION_UNIT_CANNOT_MODIFY("2020", "执行单元当前状态不可修改"),
    EXECUTION_UNIT_GROUPNAME_DUPLICATE("2021", "执行单元组名重复"),

    RESOURCE_TEMPLATE_NOT_EXISTS("2201", "资源模板不存在"),
    RESOURCE_INNER_NOT_EXISTS("2202", "项目资源不存在"),
    EXECUTION_UNIT_HAS_EXECUTING_BATCHJOB("2203", "执行单元上有批量任务正在执行"),
    EXECUTION_UNIT_HAS_EXECUTING_STREAM("2204", "执行单元上有实时任务正在执行"),


    EXECUTION_UNIT_CREATED("2101", "执行单元已创建"),
    EXECUTION_UNIT_STARTED("2102", "执行单元已启动"),
    EXECUTION_UNIT_STOPPED("2103", "执行单元已停止"),
    EXECUTION_UNIT_DESTROYED("2104", "执行单元已销毁"),
    INSTANCE_INFO_NOT_EXISTS("2105", "执行单元组不存在"),
    ENV_HOST_NOT_ENOUGH("2106", "执行单元组所需环境硬件资源不足"),
    RESOURCE_ALLOCATE_ERROR("2107", "硬件资源分配出错"),
    EXECUTION_UNIT_PARTIAL_STARTED("2108", "执行单元部分启动"),
    EXECUTION_UNIT_DESTROY_ERROR("2019","执行单元销毁失败"),

    EXECUTION_UNIT_OP_CREATE("2111", "创建执行单元"),
    EXECUTION_UNIT_OP_START("2112", "启动执行单元"),
    EXECUTION_UNIT_OP_STOP("2113", "停止执行单元"),
    EXECUTION_UNIT_OP_DESTROY("2114", "销毁执行单元"),
    EXECUTION_UNIT_OP_MODIFY("2115", "修改执行单元"),

    EXECUTION_UNIT_STARTED_TIMEOUT("2116", "执行单元启动超时"),
    EXECUTION_UNIT_STARTING("2117", "执行单元启动中"),

    /**4000-4999 数据开发相关**/
    DATA_DEV_PROJECT_ID_NULL_ERROR("4001","项目ID不能为空"),
    DATA_DEV_JOB_TYPE_NULL_ERROR("4002","任务类型不能为空"),
    DATA_DEV_JOB_ID_NULL_ERROR("4003","任务ID不能为空"),
    DATA_DEV_CREATE_USER_ID_NULL_ERROR("4004","创建人用户ID不能为空"),
    DATA_DEV_NOT_EXISTS_ERROR("4005","任务不存在"),
    DATA_DEV_JOB_TYPE_ERROR("4006","任务类型错误"),
    DATA_DEV_CREATE_USER_NAME_NULL_ERROR("4007","创建人用户名不能为空"),
    DATA_DEV_DEPLOY_USER_ID_NULL_ERROR("4008","负责人用户ID不能为空"),
    DATA_DEV_DEPLOY_USER_NAME_NULL_ERROR("4009","负责人用户名不能为空"),
    DATA_DEV_UPDATE_USER_ID_NULL_ERROR("4010","更新人用户ID不能为空"),
    DATA_DEV_UPDATE_USER_NAME_NULL_ERROR("4011","更新人用户名不能为空"),
    DATA_DEV_JOB_NAME_NULL_ERROR("4012","XD任务名不能为空"),
    DATA_DEV_JOB_AS_NAME_NULL_ERROR("4020","任务名不能为空"),
    DATA_DEV_JOB_AS_NAME_EXISTS_ERROR("4021","任务名已经存在"),
    DATA_DEV_TASK_NAME_NULL_ERROR("4022","XD运行工作流名不能为空"),
    DATA_DEV_TASK_NAME_EXISTS_ERROR("4023","XD运行工作流名已经存在"),
    DATA_DEV_JOB_NAME_EXISTS_ERROR("4024","XD运行任务名已经存在"),
    DATA_DEV_CRON_ERROR("4030","CRON表达式错误"),
    DATA_DEV_DEPLOY_SUCCESS("4031","发布成功"),
    DATA_DEV_DEPLOY_ERROR("4032","发布失败"),
    DATA_DEV_UNDEPLOY_SUCCESS("4033","取消发布成功"),
    DATA_DEV_UNDEPLOY_ERROR("4034","取消发布失败"),
    DATA_DEV_RESTART_SUCCESS("4035","重启任务成功"),
    DATA_DEV_RESTART_ERROR("4036","重启任务失败"),
    DATA_DEV_DEPLOY_STREAM_COUNT_ERROR("4037","发布Stream Count失败"),
    DATA_DEV_UNDEPLOY_STREAM_COUNT_ERROR("4038","取消发布Stream Count失败"),
    DATA_DEV_JOB_DSL_NULL_ERROR("4040","工作流DSL不能为空"),
    DATA_DEV_TASK_LIST_NULL_ERROR("4041","工作流中节点不能为空"),
    DATA_DEV_DEPLOY_INSTANCE_ID_NULL_ERROR("4042","执行容器ID不能为空"),
    DATA_DEV_DEPLOY_INSTANCE_NOT_EXISTS_ERROR("4043","执行容器不存在"),
    DATA_DEV_LAUNCH_PARAM_ERROR("4044","启动参数错误"),
    DATA_DEV_VARIABLE_CONVERT_ERROR("4045","变量转换错误"),
    DATA_DEV_EXECUTING_ERROR("4046","任务正在执行中，禁止此操作"),
    DATA_DEV_RUNNING_JOB_UPDATE_FORBID("4047","任务正在执行中，禁止修改"),

    SCHEDULE_TYPE_ERROR("4900","调度类型错误"),
    SCHEDULE_INFO_NULL_ERROR("4902","调度信息不能为空"),
    SCHEDULE_TYPE_NULL_ERROR("4903","调度方式不能为空"),
    XD_MODULE_TYPE_ERROR("4904","XD模块类型错误"),
    XD_MODULE_UPLOAD_SUCCESS("4500","XD Module 上传成功"),
    XD_MODULE_UPLOAD_ERROR("4501","XD Module 上传失败"),
    XD_MODULE_UPLOAD_EXISTS("4502","XD Module 已经存在"),

    /**5000-5999 任务监控**/
    JOBMONITOR_USER_ERROR("5001","用户不可用"),
    JOBMONITOR_USER_NONE_PROJECT_ERROR("5002","该用户无相关项目"),
    JOBMONITOR_DATA_EMPTY("5002","数据为空"),
    JOBMONITOR_SERVICE_ERROR("5010","监控服务异常"),

    /**6000-6999 数据实验室相关**/
    DATALAB_CREATED("6030","实验室已创建"),
    DATALAB_STARTED("6031","实验室已启动"),
    DATALAB_STOPPED("6032","实验室已停止"),
    DATALAB_DESTROYED("6033","实验室已销毁"),
    DATALAB_CREATE_ERROR("6034","实验室创建异常"),
    DATALAB_START_ERROR("6035","实验室启动异常"),
    DATALAB_STOP_ERROR("6036","实验室停止异常"),
    DATALAB_DESTROY_ERROR("6037","实验室销毁异常"),
    DATALAB_ALREADY_EXIST_ERROR("6038","实验室名称已经存在"),
    
    /**8000-8999 平台维护相关**/
    PLATFORM_MAINTAINED_ERROR("8000","平台已维护"),
    PLATFORM_STARTED_ERROR("8001","平台已维护完成"),

    /**17000-17999 即席查询相关**/
    SQLQUERY_QUERY_ERROR("17001","发生未知错误，请联系管理员"),
    SQLQUERY_SQL_BLANK("17002","查询SQL为空"),
    SQLQUERY_SQL_ILLEGAL("17003","查询SQL不合法"),
    SQLQUERY_SQL_TABLE_NOT_FOUND("17004","查询的表不存在"),
    SQLQUERY_SQL_COLUMN_INVALID("17005","查询的列名或表别名错误"),
    SQLQUERY_SQL_PERMISSION_DENIED("17006","用户没有权限访问此资源"),
    AUTHORIZATION_ERROR("5011", "授权信息已经存在"),

    ELASTICSEARCH_SERVICE_NOT_FOUND("7001", "ES服务不可用")

    ;
    private final String code;
    private final String desc;

    StatusCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    private static final Map<String, StatusCode> stringToEnum = new HashMap<>();

    static {
        for (StatusCode statusCode : values()) {
            stringToEnum.put(statusCode.code, statusCode);
        }
    }

    public static StatusCode fromCode(String code) {
        return stringToEnum.get(code);
    }
}
