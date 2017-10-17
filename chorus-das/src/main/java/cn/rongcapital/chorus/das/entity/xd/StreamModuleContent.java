package cn.rongcapital.chorus.das.entity.xd;

import java.util.List;

/**
 * Spring xd映射BEAN(Stream)
 *
 * @author lengyang
 */
public class StreamModuleContent {

	private String name;
	private String type;

	private String composed;
	private List<String> links;
	// private String aliasName;
	private Property props;
	/**
	 * 主键
	 */
	private Integer moduleId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String status) {
		this.type = status;
	}

	public String getComposed() {
		return composed;
	}

	public void setComposed(String definition) {
		this.composed = definition;
	}

	public List<String> getLinks() {
		return links;
	}

	public void setLinks(List<String> links) {
		this.links = links;
	}

	public Property getProps() {
		return props;
	}

	public void setProps(Property props) {
		this.props = props;
	}

	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

}
