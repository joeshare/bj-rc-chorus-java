package cn.rongcapital.chorus.das.entity.xd;

import java.util.List;

/**
 * Spring xd module信息
 *
 * @author lengyang
 */
public class XdModuleView {

	private List<Link> links;
	private List<Object> content;
	private Page page;

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public List<Object> getContent() {
		return content;
	}

	public void setContent(List<Object> content) {
		this.content = content;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

}
