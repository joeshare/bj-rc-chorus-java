package cn.rongcapital.chorus.das.entity;

/**
 * JobTree节点自定义属性
 *
 * @author lengyang
 */
public class JobPropertyV2 {
	/**
	 * 节点类型 ： 根节点
	 */
	public static int TYPE_ROOT = 0;
	/**
	 * 节点类型 ： 项目节点
	 */
	public static int TYPE_PROJECT = 1;
	/**
	 * 节点类型 ： 任务节点
	 */
	public static int TYPE_JOB = 2;
	/**
	 * 业务主键
	 */
	private String id;

	/**
	 * 父节点ID
	 */
	private String pid;
	/**
	 * 节点类型()
	 */
	private int type;

	/** projectCode */
	private String projectCode;

	public JobPropertyV2() {
	}

	public JobPropertyV2(String id, String pid, int type, String projectCode) {
		this.id = id;
		this.pid = pid;
		this.type = type;
		this.projectCode = projectCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
}
