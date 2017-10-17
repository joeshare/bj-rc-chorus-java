package cn.rongcapital.chorus.das.service.common.definition.creater;

import cn.rongcapital.chorus.common.util.JsonUtils;
import cn.rongcapital.chorus.common.util.SpringBeanUtils;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.ResourceOut;
import cn.rongcapital.chorus.das.entity.Schedule;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.das.service.ResourceOutService;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

public class RDB2HiveDefinitionCreater extends DefinitionCreater {
	
	private static final String HIVE_PARTITION_KEY = "p_date";
	
	@Override
	public String toJSONObject(String json, Schedule schedule) throws Exception {
		JSONObject jsonObject = JsonUtils.toJsonObject(json);
		String resourceOutId = jsonObject.getString("resourceOutId");
		ResourceOutService resourceOutService = (ResourceOutService)SpringBeanUtils.getBean("ResourceOutService");
		ResourceOut resourceOut = resourceOutService.selectByPrimaryKey(Long.parseLong(resourceOutId));
		String url = resourceOut.getConnUrl() + "?useUnicode=true&characterEncoding=utf-8";
		String user = resourceOut.getConnUser();
		String password = resourceOut.getConnPass();
		
		String rdbTable = jsonObject.getString("rdbTable");
		
		TableInfoServiceV2 tableInfoServiceV2 = (TableInfoServiceV2)SpringBeanUtils.getBean("TableInfoServiceV2");
		String hiveTableId = jsonObject.getString("hiveTable");
		TableInfoV2 tableInfo = tableInfoServiceV2.selectByID(hiveTableId);
		String hiveTable = tableInfo.getTableName();
		Long projectId = tableInfo.getProjectId();

		ProjectInfoService projectInfoService = (ProjectInfoService)SpringBeanUtils.getBean("ProjectInfoService");
		ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(projectId);
//		ConnectorConfigInfo connectorConfigInfo = SpringBeanUtils.getBean(ConnectorConfigInfo.class);
//		String targetDir = connectorConfigInfo.getWarehouseHome() + String.format("chorus_%s", projectInfo.getProjectCode()) + ".db/" + hiveTable;
		
		String whereSql = jsonObject.getString("whereSql");
		
//		String incrementKey = jsonObject.getString("incrementKey");
		
//		String incrementWhere = "";
//		if (!StringUtils.isBlank(incrementKey) && schedule.getScheduleType() == 2) {
//			String hour = "00";
//			String minute = "00";
//			
//			if (StringUtils.isBlank(schedule.getHour())) {
//				hour = schedule.getHour();
//			}
//			
//			if (StringUtils.isBlank(schedule.getMinute())) {
//				minute = schedule.getMinute();
//			}
//			
//			String startTime = "concat(date_format(now(),'%Y-%m-%d'), ' " + hour + ":" + minute +":00')";
//			
//			// 调度周期-日
//			if (schedule.getScheduleCycle() == 1) {
//				incrementWhere = incrementKey + " >= date_sub(" + startTime + ", interval 1 day)";
//			} else if (schedule.getScheduleCycle() == 2) {// 调度周期-周
//				incrementWhere = incrementKey + " >= date_sub(" + startTime + ", interval 1 week)";
//			}	else if (schedule.getScheduleCycle() == 3) {// 调度周期-月
//				incrementWhere = incrementKey + " >= date_sub(" + startTime + ", interval 1 month)";
//			}
//		}
		
		String fields = jsonObject.getString("fields");
		
		StringBuilder definition = new StringBuilder();
		definition.append("RDB2Hive");
		definition.append(" --args=\"");
		definition.append("--connect ").append("'" + url + "'");
		definition.append(" --username ").append("'" + user + "'");
		definition.append(" --password ").append("'" + password + "'");
		definition.append(" --table ").append("'" + rdbTable + "'");
		definition.append(" --hive-import");
		definition.append(" --hive-database ").append("'" + String.format("chorus_%s", projectInfo.getProjectCode()) + "'");
		definition.append(" --hive-table ").append("'" + hiveTable + "'");
		definition.append(" --hive-overwrite");
		definition.append(" --hive-partition-key ").append("'" + HIVE_PARTITION_KEY + "'");
		definition.append(" --hive-partition-value ").append("'$today_yyyyMMdd'");
		definition.append(" --delete-target-dir");
//		definition.append(" --target-dir ").append(targetDir);
		
		if (!StringUtils.isBlank(whereSql)) {			
			if (whereSql.trim().startsWith("where ")) {
				whereSql = whereSql.trim().replaceFirst("where ", "");
			}
//			if (!StringUtils.isBlank(incrementWhere)) {
//				whereSql = "(" + whereSql + ") AND (" + incrementWhere + ")";
//			}
			definition.append(" --where ").append("'" + whereSql + "'");
		}
//		else {
//			if (!StringUtils.isBlank(incrementWhere)) {
//				definition.append(" --where ").append(incrementWhere);
//			}
//		}
		if (!StringUtils.isBlank(fields)) {
			definition.append(" --columns ").append("'" + fields + "'");
		}
		
		definition.append(" --m 1");
//		definition.append(" --null-string '\\\\N' --null-non-string '\\\\N'");
//		definition.append(" --append\"");
//		definition.append(" --as-parquetfile\"");
		definition.append("\" --command=import");
		return definition.toString();
	}

}
