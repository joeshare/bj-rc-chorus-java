package cn.rongcapital.chorus.das.service.common.definition.creater;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import cn.rongcapital.chorus.das.entity.Schedule;

/**
 * Definition生成类
 * 
 * @author lengyang
 *
 */
public abstract class DefinitionCreater {
	public static final String DEF_TYPE_RDB2HIVE = "RDB2Hive";
	public static final String DEF_TYPE_HIVEEXJOB = "hiveExJob";
	public static final String DEF_TYPE_HIVE2RDB = "Hive2RDB";
	private static Map<String, DefinitionCreater> creaters = null;

	static {
		creaters = new HashMap<String, DefinitionCreater>();
		creaters.put(DEF_TYPE_RDB2HIVE, new RDB2HiveDefinitionCreater());
		creaters.put(DEF_TYPE_HIVEEXJOB, new DesignSqlDefinitionCreater());
		creaters.put(DEF_TYPE_HIVE2RDB, new Hive2RDBDefinitionCreater());
	}

	public static String createJSONObject(String type, String jsonObj, Schedule schedule) throws Exception {
		if (StringUtils.isEmpty(type) || jsonObj == null) {
			return null;
		}
		DefinitionCreater creater = creaters.get(type);
		return creater.toJSONObject(jsonObj, schedule);
	}

	public abstract String toJSONObject(String jsonObj, Schedule schedule) throws Exception;
}
