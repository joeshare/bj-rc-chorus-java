package cn.rongcapital.chorus.das.entity;

/**
 * TableAuthorityTree节点自定义属性
 *
 * @author lengyang
 */
public class TableAuthorityProperty {
	/**
	 * 节点类型 ： 根节点
	 */
	public static int TYPE_ROOT = 0;
	/**
	 * 节点类型 ： 项目节点
	 */
	public static int TYPE_PROJECT = 1;
	/**
	 * 节点类型 ： 表节点
	 */
	public static int TYPE_TABLE = 2;
	/**
	 * 节点类型 ： 字段节点
	 */
	public static int TYPE_COLUMN = 3;
	/**
	 * 业务主键
	 */
	private Integer id;

	/**
	 * 父节点ID
	 */
	private Integer pid;
	/**
	 * 节点类型()
	 */
	private int type;

	public TableAuthorityProperty() {
	}

	public TableAuthorityProperty(Integer id, Integer pid, int type) {
		this.id = id;
		this.pid = pid;
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
