package cn.rongcapital.chorus.das.service.common.definition;

import cn.rongcapital.chorus.das.entity.Schedule;
import cn.rongcapital.chorus.das.service.common.definition.creater.DefinitionCreater;

/**
 * Definition生成共通类
 *
 * @author lengyang
 *
 */
public class DefinitionUtils {
	public static String createDefinition(String type, String json, Schedule schedule) {
		String definition = null;
		try {
			definition = DefinitionCreater.createJSONObject(type, json, schedule);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return definition;
	}

	public static void main(String[] args) {
		String json = "{\"createUser\":\"0\",\"createTime\":1479260595000,\"updateUser\":\"0\"," +
				"\"updateTime\":1479986731000,\"remark\":\"第三方\",\"useYn\":null,\"createTimeLabel\":null,\"createTimeLabelSecond\":null,\"jobId\":13,\"jobType\":2,\"projectId\":1,\"jobName\":\"3333\",\"jobAliasName\":\"测试3333\",\"dslInfo\":\"step || step2\",\"dataInput\":\"1\",\"dataOutput\":\"1\",\"status\":0,\"jobParameters\":null,\"jobStepList\":[{\"createUser\":null,\"createTime\":1479881626000,\"updateUser\":\"0\",\"updateTime\":1479986731000,\"remark\":\"步骤备注1\",\"useYn\":null,\"createTimeLabel\":null,\"createTimeLabelSecond\":null,\"stepId\":5,\"jobId\":13,\"moduleName\":\"module1\",\"stepName\":\"step\",\"aliasName\":\"步骤1\",\"dataInput\":null,\"dataOutput\":null,\"config\":null,\"definition\":null},{\"createUser\":null,\"createTime\":1479881627000,\"updateUser\":\"0\",\"updateTime\":1479986731000,\"remark\":\"步骤备注2\",\"useYn\":null,\"createTimeLabel\":null,\"createTimeLabelSecond\":null,\"stepId\":6,\"jobId\":13,\"moduleName\":\"module1\",\"stepName\":\"step2\",\"aliasName\":\"步骤2\",\"dataInput\":null,\"dataOutput\":null,\"config\":null,\"definition\":null}],\"schedule\":{\"createUser\":\"0\",\"createTime\":1479881618000,\"updateUser\":\"0\",\"updateTime\":1479986731000,\"remark\":\"\",\"useYn\":null,\"createTimeLabel\":null,\"createTimeLabelSecond\":null,\"scheduleId\":3,\"jobId\":13,\"scheduleName\":null,\"scheduleStat\":null,\"scheduleType\":null,\"scheduleCycle\":null,\"startTime\":null,\"endTime\":null,\"repeatCount\":null,\"repeatInterval\":null,\"second\":null,\"minute\":null,\"hour\":null,\"day\":null,\"week\":null,\"month\":null},\"projectName\":null}";
//		String json = "{\"createUser\":\"0\",\"createTime\":1479260595000,\"updateUser\":\"0\",
// \"updateTime\":1479986731000,\"remark\":\"第三方\",\"useYn\":\"\",\"createTimeLabel\":\"\",\"createTimeLabelSecond\":\"\",\"jobId\":13,\"jobType\":2,\"projectId\":1,\"jobName\":\"3333\",\"jobAliasName\":\"测试3333\",\"dslInfo\":\"step || step2\",\"dataInput\":\"1\",\"dataOutput\":\"1\",\"status\":0,\"jobParameters\":\"\",\"projectName\":\"\"}";
		String createDefinition = createDefinition(DefinitionCreater.DEF_TYPE_RDB2HIVE, json, null);
		System.out.println(createDefinition);
	}
}
