package cn.rongcapital.chorus.das.entity.web;

public class XdModulePropCause extends CommonCause {

    /**
     * 主键
     */
    private Integer id;
    
	/**
	 * 组件ID
	 */
	private Integer moduleId;

	/**
	 * 模块类型 1:默认Job 2:用户自定义Job 3:默认Stream source 4:用户自定义Stream source 5:默认Stream processor 6:用自定义Stream processor
	 * 7:默认Stream sink 8:用户自定义Stream sink 9:默认Stream other 10:用户自定义Stream other 11:job definition
	 */
	private int moduleType;

	/**
	 * Spring XD 定义Module画面显示别名
	 */
	private String name;

 

	public int getModuleType() {
		return moduleType;
	}

	public void setModuleType(int moduleType) {
		this.moduleType = moduleType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

 
}
