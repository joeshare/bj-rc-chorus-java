package cn.rongcapital.chorus.das.service.common.definition.creater;

import cn.rongcapital.chorus.common.util.JsonUtils;
import cn.rongcapital.chorus.common.util.SpringBeanUtils;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.entity.Schedule;
import cn.rongcapital.chorus.das.service.common.definition.bean.ConnectorConfigInfo;
import cn.rongcapital.chorus.das.service.common.definition.bean.HiveConnConfigInfo;
import com.alibaba.fastjson.JSONObject;

public class DesignSqlDefinitionCreater extends DefinitionCreater {

	@Override
	public String toJSONObject(String json, Schedule schedule) throws Exception {
		JSONObject jsonObject = JsonUtils.toJsonObject(json);
		String createSql = jsonObject.getString("createSql");

		StringBuilder definition = new StringBuilder();
		definition.append("hiveExJob");
		// TODO: DBname/URL 修改
		String dbName = jsonObject.getString("dbName");

		if (!StringUtils.isBlank(dbName)) {
			definition.append(" --dbName=");
			definition.append(dbName);
		}

		HiveConnConfigInfo hiveConnConfigInfo = SpringBeanUtils.getBean(HiveConnConfigInfo.class);
		definition.append(" --hiveServerUrl=");
		definition.append(hiveConnConfigInfo.getUrl());

		ConnectorConfigInfo connectorConfigInfo = SpringBeanUtils.getBean(ConnectorConfigInfo.class);
		definition.append(" --hiveUser=");
		definition.append(connectorConfigInfo.getHiveUser());

		definition.append(" --intoHiveSql=\"");
		createSql = createSql.replace("\r\n"," ");
		createSql = createSql.replace("\n"," ");
		definition.append(createSql);
		definition.append("\"");

		return definition.toString();
	}

}
