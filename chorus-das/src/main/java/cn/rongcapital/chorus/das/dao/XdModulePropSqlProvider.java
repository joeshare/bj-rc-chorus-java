package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.web.CommonCause;
import cn.rongcapital.chorus.das.entity.web.XdModulePropCause;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

public class XdModulePropSqlProvider {

	public String getModulePropList(final XdModulePropCause cause) {
		SQL sql = new SQL() {
			{
				SELECT("id as id,xd_module_id as xdModuleId,xd_module_type as xdModuleType,name as name,type as type,label as label,placeholder as placeholder,validate_config as validateConfig,remark, sort_num as sortNum, create_user as createUser, create_time as createTime, update_user as updateUser, update_time as updateTime ");
				FROM("xd_prop_element");
				appendWhere(this, cause);
				ORDER_BY("create_time");
			}
		};
		return getLimitSql(sql.toString(), cause);
	}

	public String getModulePropCount(final XdModulePropCause cause) {
		SQL sql = new SQL() {
			{
				SELECT("COUNT(1)");
				FROM("xd_prop_element");
				appendWhere(this, cause);
			}
		};
		return sql.toString();
	}


	private void appendWhere(SQL sql, final XdModulePropCause cause) {
		StringBuffer where = new StringBuffer();
		where.append(" use_yn = 'Y' ");
		if (!StringUtils.isBlank(cause.getSearchKey())) {
			where.append(" AND (name LIKE #{searchKey})");
		}

		//if (cause.getModuleId() != 0) {
			where.append(" AND (xd_module_id = #{moduleId})");
		//}

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
