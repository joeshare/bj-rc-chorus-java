package cn.rongcapital.chorus.das.entity;

public class FieldBean {

	private String name;

	private String label;

	private String type;

	private String value;

	private int size;

	/**
	 * 数据库源表主键标识
	 */
	private boolean primaryKey = false;


	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * 数据库源表主键标识
	 */
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	/**
	 * 数据库源表主键标识
	 */
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

}
