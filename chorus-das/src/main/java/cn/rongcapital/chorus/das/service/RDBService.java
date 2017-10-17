package cn.rongcapital.chorus.das.service;

import java.util.List;

import cn.rongcapital.chorus.das.entity.FieldBean;
import cn.rongcapital.chorus.das.entity.web.R;

/**
 * RDB相关
 * 
 * @author maboxiao
 *
 */
public interface RDBService {
	
	/**
	 * 校验连接
	 *
	 */
	public R checkSourceConnect(String dbType, String url, String user, String password);
	
	/**
	 * 取得Table列表
	 *
	 */
	public List<String> getTableList(String dbType, String url, String user, String password);
		
	/**
	 * 取得Table字段列表
	 *
	 */
	public List<FieldBean> getFieldList(String dbType, String url, String user, String password, String tableName);
}
