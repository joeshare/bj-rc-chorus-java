package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.common.constant.ProjectConstants;
import cn.rongcapital.chorus.das.entity.web.CommonCause;
import cn.rongcapital.chorus.das.entity.web.XdModuleCause;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

public class XdModuleSqlProvider {

	public String getModuleList(final XdModuleCause cause) {
		SQL sql = new SQL() {
			{
				SELECT("m.module_id as moduleId,m.module_type as moduleType,m.module_category as moduleCategory,m.module_level as moduleLevel, m.module_name as moduleName,m.module_alias_name as moduleAliasName,m.module_view_name as moduleViewName, m.remark, m.sort_num as sortNum, m.create_user as createUser, m.create_time as createTime, m.update_user as updateUser, m.update_time as updateTime,m.create_user_name as createUserName,m.update_user_name as updateUserName, m.project_id as projectId,p.project_name as projectName ");
				FROM(" xd_module as m ");
//				FROM(" project_info as p ");
				LEFT_OUTER_JOIN(" project_info as p on m.project_id=p.project_id ");
				appendWhere(this, cause);
				ORDER_BY("m.create_time");
			}
		};
		return getLimitSql(sql.toString(), cause);
	}

	public String getModuleCount(final XdModuleCause cause) {
		SQL sql = new SQL() {
			{
				SELECT("COUNT(1)");
				FROM("xd_module as m ");
				appendWhere(this, cause);
			}
		};
		return sql.toString();
	}


	private void appendWhere(SQL sql, final XdModuleCause cause) {
		StringBuffer where = new StringBuffer();
		where.append(" m.use_yn = 'Y' ");
		if (!StringUtils.isBlank(cause.getSearchKey())) {
			where.append(" AND (m.module_alias_name LIKE #{searchKey})");
		}

		if (cause.getModuleType() != 0) {
			where.append(" AND (m.module_type = #{moduleType})");
		}

		if(cause.getProjectId() != null && cause.getProjectId()>=0){
			if(cause.getProjectId().longValue() == ProjectConstants.PLATFORM_PROJECT_ID.longValue()){
				where.append(" AND (m.project_id = #{projectId})");
			}else{
				where.append(" AND (m.project_id = #{projectId} or m.project_id="+ ProjectConstants.PLATFORM_PROJECT_ID+")");
			}

		}else{
			where.append(" AND (p.status_code='1205' or m.project_id="+ ProjectConstants.PLATFORM_PROJECT_ID+")");
		}


		sql.WHERE(where.toString());
	}

	private String getLimitSql(String sql, final CommonCause cause) {
		if (cause.getStartIndex() != null) {
			return sql + " LIMIT " + cause.getStartIndex() + ","
					+ CommonCause.getRows();
		} else {
			return sql;
		}
	}
}
