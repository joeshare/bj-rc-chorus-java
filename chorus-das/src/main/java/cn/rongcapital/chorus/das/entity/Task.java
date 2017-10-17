package cn.rongcapital.chorus.das.entity;

/**
 * t_step表映射BEAN
 *
 * @author lengyang
 */
public class Task extends CommonEntity {
	/**
	 * 业务主键
	 */
	private Integer taskId;
	/**
	 * 数据任务表ID
	 */
	private Integer jobId;

	/**
	 * 模块类型 1:默认Job 2:用户自定义Job 3:默认Stream source 4:用户自定义Stream source 5:默认Stream processor 6:用自定义Stream processor 7:默认Stream sink 8:用户自定义Stream sink 9:默认Stream other 10:用户自定义Stream other 11:job definition
	 */
	private int moduleType;

	/**
	 * Srping XD定义模块名
	 */
	private String moduleName;
	/**
	 * 步骤名（英文）
	 */
	private String taskName;
	/**
	 * 步骤别名
	 */
	private String aliasName;
	/**
	 * 任务输入
	 */
	private String dataInput;
	/**
	 * 任务输出
	 */
	private String dataOutput;
	/**
	 * Step配置参数信息
	 */
	private String config;

	/**
	 * Step taskDSL
	 */
	private String taskDSL;
	
	/**
	 * 实例ID
	 */
	private String instanceId;
	/**
	 * 描述
	 */
	private String description;

	/**
	 * 变量
	 */
	private String variable;

	/**
	 * 任务创建人名
	 */
	private String createUserName;

	/**
	 * 更新者用户名
	 */
	private String updateUserName;
	
	public String getTaskDSL() {
		return taskDSL;
	}

	public void setTaskDSL(String taskDSL) {
		this.taskDSL = taskDSL;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public int getModuleType() {
		return moduleType;
	}

	public void setModuleType(int moduleType) {
		this.moduleType = moduleType;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
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

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	/**
	 * 实例数量
	 */
	private Integer instanceSize;

	/**
	 * 实例组名
	 */
	private String groupName;

	public Integer getInstanceSize() {
		// TODO:临时修改固定返回1，下期修改。
		return 1;
		//return instanceSize;
	}

	public void setInstanceSize(Integer instanceSize) {
		this.instanceSize = instanceSize;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
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
