package cn.rongcapital.chorus.das.entity;

/**
 * t_xd_module表映射BEAN
 *
 * @author lengyang
 */
public class XdModule extends CommonEntity {

	/**
	 * 主键
	 */
	private Integer moduleId;

	/**
	 * 模块类型 1:默认Job 2:用户自定义Job 3:默认Stream source 4:用户自定义Stream source 5:默认Stream processor 6:用自定义Stream processor 7:默认Stream sink 8:用户自定义Stream sink 9:默认Stream other 10:用户自定义Stream other 11:job definition
	 */
	private int moduleType;

	/**
	 * Spring XD 定义Module名称
	 */
	private String moduleName;

	/**
	 * Spring XD 定义Module画面显示别名
	 */
	private String moduleAliasName;

	/**
	 * 顺序号
	 */
	private int sortNum;

	/*add by hhl for module upload.*/
	/***
	 * 组件级别，0：平台，1：项目
	 */
	private int moduleLevel;
	/***
	 * 项目ID
	 */
	private long projectId;
	/**
	 * 组件类别 0：批量，1：流式
	 */
	private int moduleCategory;
	/***
	 * 上传文件名称
	 */
	private String fileName;
	/***
	 * 前台显示名称
	 */
	private String moduleViewName;

	/***
	 * 创建用户名
	 */
	private String createUserName;
	/***
	 * 更新用户名
	 */
	private String updateUserName;

	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
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

	public String getModuleAliasName() {
		return moduleAliasName;
	}

	public void setModuleAliasName(String moduleAliasName) {
		this.moduleAliasName = moduleAliasName;
	}

	public int getSortNum() {
		return sortNum;
	}

	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}

	/**
	 * 模块类型 1:默认Job 2:用户自定义Job 3:默认Stream source 4:用户自定义Stream source 5:默认Stream processor 6:用自定义Stream processor
	 * 7:默认Stream sink 8:用户自定义Stream sink 9:默认Stream other 10:用户自定义Stream other 11:job definition
	 *
	 * @return
	 */
	public String getXdModuleTypeName() {
		String moduleName = "";
		switch (moduleType) {
			case 1:
				moduleName = "defJob";
				break;
			case 2:
				moduleName = "custJob";
				break;
			case 3:
				moduleName = "source";
				break;
			case 4:
				moduleName = "custSource";
				break;
			case 5:
				moduleName = "processor";
				break;
			case 6:
				moduleName = "custProcessor";
				break;
			case 7:
				moduleName = "sink";
				break;
			case 8:
				moduleName = "custSink";
				break;
			case 9:
				moduleName = "other";
				break;
			case 10:
				moduleName = "custOther";
				break;
			case 11:
				moduleName = "jobDefinition";
				break;

		}
		return moduleName;
	}

	public int getModuleLevel() {
		return moduleLevel;
	}

	public void setModuleLevel(int moduleLevel) {
		this.moduleLevel = moduleLevel;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public int getModuleCategory() {
		return moduleCategory;
	}

	public void setModuleCategory(int moduleCategory) {
		this.moduleCategory = moduleCategory;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getModuleViewName() {
		return moduleViewName;
	}

	public void setModuleViewName(String moduleViewName) {
		this.moduleViewName = moduleViewName;
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
