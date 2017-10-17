package cn.rongcapital.chorus.das.entity.xd;

import java.util.List;

/**
 * Spring xd映射BEAN
 *
 * @author lengyang
 */
public class JobModuleContent {

	private String name;
	private String status;

	private String definition;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
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
