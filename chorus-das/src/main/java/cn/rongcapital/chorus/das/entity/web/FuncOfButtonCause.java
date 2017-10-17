
package cn.rongcapital.chorus.das.entity.web;
/***
 * 页面权限控制类
 * @author Administrator
 *
 */
public class FuncOfButtonCause extends CommonCause {

	/**
	 * 用户ID
	 */
	private String userId;
	
	/**
	 * 页面节点url
	 */
	private String url;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}

