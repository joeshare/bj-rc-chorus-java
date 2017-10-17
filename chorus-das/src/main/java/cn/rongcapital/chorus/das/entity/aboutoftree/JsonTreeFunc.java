package cn.rongcapital.chorus.das.entity.aboutoftree;

import java.util.*;
/**
 * json返回树结构
 * @author Administrator
 *
 */
public class JsonTreeFunc {
	
    //节点id
	private String id;
//	//对应父节点
	private String fFuncId;
	//文本名称
    private String text;
    //图标
    private String icon;
    //状态
    private State state;
    //子节点
    private List<Children> children;
 
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getfFuncId() {
		return fFuncId;
	}
	public void setfFuncId(String fFuncId) {
		this.fFuncId = fFuncId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public List<Children> getChildren() {
		return children;
	}
	public void setChildren(List<Children> children) {
		this.children = children;
	}

}
