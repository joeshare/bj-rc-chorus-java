package cn.rongcapital.chorus.das.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JobTreeNode {
	/**
	 * 节点类型 ： 根节点
	 */
	public static String TYPE_ROOT = "0";
	/**
	 * 节点类型 ： 项目节点
	 */
	public static String TYPE_PROJECT = "1";
	/**
	 * 节点类型 ： 任务节点
	 */
	public static String TYPE_JOB = "2";

	/** 菜单ID */
	private String id;
	/** 父菜单ID */
	private String pid;
	/** 菜单父ID */
	private String text;
//	/** 菜类型 0:根节点 1:项目 2:任务 */
//	private String nodeType;
	/** 菜单顺序 */
	private Integer sortId;
	/** 菜单样式 */
	private String icon;
	/** 子菜单 */
	private List<JobTreeNode> children;
	
	/**
	 * Job节点自定义属性
	 */
	private JobPropertyV2 data;

	private JobTreeNodeState state;

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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getSortId() {
		return sortId;
	}

	public void setSortId(Integer sortId) {
		this.sortId = sortId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void addChild(JobTreeNode child) {
		if (children == null) {
			children = new ArrayList<>();
		}
		children.add(child);
	}

	public int getChildCount() {
		if (children == null) {
			return 0;
		}
		return children.size();
	}

	public JobTreeNode getChild(int index) {
		return children.get(index);
	}

	public List<JobTreeNode> getChildren() {
		return children;
	}

	public JobTreeNodeState getState() {
		return state;
	}

	public void setState(JobTreeNodeState state) {
		this.state = state;
	}

//	public String getNodeType() {
//		return nodeType;
//	}
//
//	public void setNodeType(String nodeType) {
//		this.nodeType = nodeType;
//	}

	public void sort(Comparator<JobTreeNode> comparator) {
		if (children != null) {
			Collections.sort(children, comparator);
			for (JobTreeNode child : children) {
				child.sort(comparator);
			}
		}
	}

	public JobPropertyV2 getData() {
		return data;
	}

	public void setData(JobPropertyV2 data) {
		this.data = data;
	}
}
