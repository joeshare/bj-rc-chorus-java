package cn.rongcapital.chorus.modules.batchjob.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * t_job表映射BEAN
 *
 * @author lengyang
 */
public class Job extends CommonEntity {

	/**
	 * JOB_STATUS_UNDEPLOY：状态(未发布)
	 */
	public static final String JOB_STATUS_UNDEPLOY = "UNDEPLOY";

	/**
	 * JOB_STATUS_DEPLOY：状态(已发布)
	 */
	public static final String JOB_STATUS_DEPLOY = "DEPLOY";

	/**
	 * JOB_STATUS_DELETE：状态(删除)
	 */
	public static final String JOB_STATUS_DELETE = "DELETE";

	/**
	 * JOB_TYPE_REAL：实时任务
	 */
	public final static int JOB_TYPE_REAL = 1;

	/**
	 * JOB_STATUS_DELETE：周期任务
	 */
	public final static int JOB_TYPE_SYNC = 2;

	/**
	 * 任务ID
	 */
	private Integer jobId;
	/**
	 * 任务类型(1:实时 2:定期)
	 */
	private int jobType;
	/**
	 * 项目ID
	 */
	private Integer projectId;
	/**
	 * 任务运行名称.
	 * 前端自动生成uuid,必须英文和下划线
	 */
	private String jobName;
	/**
	 * 任务名(显示)
	 */
	private String jobAliasName;
	/**
	 * 实例ID
	 */
	private Long instanceId;
	/**
	 * Spring XD DSL
	 */
	private String workFlowDSL;
	/**
	 * 任务输入
	 */
	private String dataInput;
	/**
	 * 任务输出
	 */
	private String dataOutput;
	/**
	 * 发布状态(UNDEPLOY:未发布 DEPLOY:已发布 DELETE:删除)
	 */
	private String status;
	/**
	 * 任务执行参数 JSON 格式的任务参数
	 */
	private String jobParameters;
	/**
	 * 描述
	 */
	private String description;

	/**
	 * 告警信息
	 */
	private String warningConfig;

	/**
	 * 任务负责人ID
	 */
	private String deployUserId;

	/**
	 * 任务负责人名
	 */
	private String deployUserName;

	/**
	 * 任务创建人名
	 */
	private String createUserName;

	/**
	 * 更新者用户名
	 */
	private String updateUserName;

	public String getJobParameters() {
		return jobParameters;
	}

	public void setJobParameters(String jobParameters) {
		this.jobParameters = jobParameters;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobAliasName() {
		return jobAliasName;
	}

	public void setJobAliasName(String jobAliasName) {
		this.jobAliasName = jobAliasName;
	}

	public String getWorkFlowDSL() {
		return workFlowDSL;
	}

	public void setWorkFlowDSL(String workFlowDSL) {
		this.workFlowDSL = workFlowDSL;
	}

	public String getDataInput() {
		return dataInput;
	}

	public void setDataInput(String dataInput) {
		this.dataInput = dataInput;
	}

	public String getDataOutput() {
		return dataOutput;
	}

	public void setDataOutput(String dataOutput) {
		this.dataOutput = dataOutput;
	}

	public int getJobType() {
		return jobType;
	}

	public void setJobType(int jobType) {
		this.jobType = jobType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Task数据
	 */
	private List<Task> taskList;

	/**
	 * Task数据
	 */
	private Schedule schedule;

	public List<Task> getTaskList() {
		if (taskList == null) {
			taskList = new ArrayList<Task>();
		}
		return taskList;
	}

	public void setTaskList(List<Task> taskList) {
		this.taskList = taskList;
	}

	public Schedule getSchedule() {
		if (schedule == null) {
			schedule = new Schedule();
		}
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	/**
	 * 实例数量
	 */
	private Integer instanceSize;

	/**
	 * 实例组名
	 */
	private String groupName;

	/**
	 * 项目CODE(HDFS文件路径用)
	 */
	private String projectCode;
	/**
	 * 项目名(画面显示用)
	 */
	private String projectName;

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	// TODO:执行单元大小设置异常，临时设置1
	public Integer getInstanceSize() {
		return 1;
		//return instanceSize;
	}

	public void setInstanceSize(Integer instanceSize) {
		this.instanceSize = instanceSize;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWarningConfig() {
		return warningConfig;
	}

	public void setWarningConfig(String warningConfig) {
		this.warningConfig = warningConfig;
	}

	public String getDeployUserId() {
		return deployUserId;
	}

	public void setDeployUserId(String deployUserId) {
		this.deployUserId = deployUserId;
	}

	public String getDeployUserName() {
		return deployUserName;
	}

	public void setDeployUserName(String deployUserName) {
		this.deployUserName = deployUserName;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getUpdateUserName() {
		return updateUserName;
	}

	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}
}
