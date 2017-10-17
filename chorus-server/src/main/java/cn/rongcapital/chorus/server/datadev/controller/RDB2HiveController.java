package cn.rongcapital.chorus.server.datadev.controller;

import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.ResourceOut;
import cn.rongcapital.chorus.das.entity.ResourceOutEnum;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.entity.web.R;
import cn.rongcapital.chorus.das.service.ColumnInfoServiceV2;
import cn.rongcapital.chorus.das.service.RDBService;
import cn.rongcapital.chorus.das.service.ResourceOutService;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * RDB导入CONTROLLER
 * 
 * @author lengyang
 *
 */
@Controller
@RequestMapping(value = "/rdb2hive")
public class RDB2HiveController {

	/**
	 * RDB Service
	 */
	@Autowired
	private RDBService rdbService;
	
	/**
	 * 内部资源Service
	 */
	@Autowired
    private TableInfoServiceV2 tableInfoServiceV2;
	
	/**
	 * Hive表字段信息Service
	 */
	@Autowired
	private ColumnInfoServiceV2 columnInfoServiceV2;
	
	/**
	 * 外部资源Service
	 */
	@Autowired
	private ResourceOutService resourceOutService;

	/**
	 * 取得数据源表名
	 * 
	 * @param resourceOutId
	 * @return
	 */
	@RequestMapping(value = "/getTableList/{resourceOutId}", method = RequestMethod.POST)
	@ResponseBody
	public R getTableList(@PathVariable long resourceOutId) {
		
		// 取数据源表名		
		ResourceOut resourceOut = resourceOutService.selectByPrimaryKey(resourceOutId);
		String url = resourceOut.getConnUrl();
		String user = resourceOut.getConnUser();
		String password = resourceOut.getConnPass();
		String dbType = resourceOut.getStorageType();

		R r = rdbService.checkSourceConnect(dbType, url, user, password);
		if (r.isState()) {
			r.setResult(rdbService.getTableList(dbType, url, user, password));
		}
		return r;
	}
	
	/**
	 * 取得数据源表字段名
	 * 
	 * @param resourceOutId
	 * @return
	 */
	@RequestMapping(value = "/getFieldList/{resourceOutId}/{tableName}", method = RequestMethod.POST)
	@ResponseBody
	public R getFieldList(@PathVariable long resourceOutId, @PathVariable String tableName) {
		
		// 数据源表字段名	
		ResourceOut resourceOut = resourceOutService.selectByPrimaryKey(resourceOutId);
		String url = resourceOut.getConnUrl();
		String user = resourceOut.getConnUser();
		String password = resourceOut.getConnPass();
		String dbType = resourceOut.getStorageType();

		R r = R.trueState("取得成功");
		r.setResult(rdbService.getFieldList(dbType, url, user, password, tableName));
		return r;
	}
	
	/**
	 * 取得数据源表名列表
	 * 
	 * @param projectId
	 * @return
	 */
	@RequestMapping(value = "/getRDBList/{projectId}", method = RequestMethod.POST)
	@ResponseBody
	public R getRDBList(@PathVariable long projectId) {
		
		// 数据源表名列表	
		List<ResourceOut> list = resourceOutService.selectResourceOutByProjectId(projectId, Integer.valueOf(ResourceOutEnum.MYSQL.getCode()));

		R r = R.trueState("取得成功");	

		r.setResult(list);
		return r;
	}
	
	/**
	 * 取得目标Hive表列表
	 * 
	 * @param projectId
	 * @return
	 */
	@RequestMapping(value = "/getHiveTableList/{projectId}", method = RequestMethod.POST)
	@ResponseBody
	public R getHiveTableList(@PathVariable long projectId) {
		
		// 取Hive表列表	
		List<TableInfoV2> list = tableInfoServiceV2.listAllTableInfo(projectId, 1, 100000);
		
		R r = R.trueState("取得成功");	
		r.setResult(list);
		return r;
	}
	
	/**
	 * 取得目标Hive表字段列表
	 * 
	 * @param tableInfoId
	 * @return
	 */
	@RequestMapping(value = "/getColumnInfoList/{tableInfoId}", method = RequestMethod.POST)
	@ResponseBody
	public R getColumnInfoList(@PathVariable String tableInfoId) {
		
		// 取Hive表字段列表	
		List<ColumnInfoV2> list = columnInfoServiceV2.selectColumnInfo(tableInfoId);
		
		R r = R.trueState("取得成功");	
		r.setResult(list);
		return r;
	}
}
