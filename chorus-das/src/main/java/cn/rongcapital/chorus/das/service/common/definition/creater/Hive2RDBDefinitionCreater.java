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
import cn.rongcapital.chorus.das.service.common.definition.bean.ConnectorConfigInfo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

public class Hive2RDBDefinitionCreater extends DefinitionCreater {
	
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
		
		ProjectInfoService projectInfoService = (ProjectInfoService)SpringBeanUtils.getBean("ProjectInfoService");
		ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(tableInfo.getProjectId());
		ConnectorConfigInfo connectorConfigInfo = SpringBeanUtils.getBean(ConnectorConfigInfo.class);
		String exportDir = connectorConfigInfo.getWarehouseHome() + projectInfo.getProjectCode() + "/hive/" + hiveTable + "/*";
		
		String fields = jsonObject.getString("fields");
		
		StringBuilder definition = new StringBuilder();
		definition.append("Hive2RDB");
		definition.append(" --args=\"");
		definition.append("--connect ").append("'" + url + "'");
		definition.append(" --username ").append("'" + user + "'");
		definition.append(" --password ").append("'" + password + "'");
		definition.append(" --table ").append("'" + rdbTable + "'");
//		definition.append(" --hcatalog-database ").append(String.format("chorus_%s", projectInfo.getProjectCode()));
//		definition.append(" --hcatalog-table ").append(hiveTable);
		
		definition.append(" --export-dir ").append("'" + exportDir + "'");
		definition.append(" --input-fields-terminated-by '\\001'");
		if (!StringUtils.isBlank(fields)) {
			definition.append(" --columns ").append("'" + fields + "'");
		}
		definition.append("\"");
		definition.append(" --command=export");
		return definition.toString();
	}

}
