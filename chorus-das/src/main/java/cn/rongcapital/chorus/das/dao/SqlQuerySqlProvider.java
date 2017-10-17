package cn.rongcapital.chorus.das.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import cn.rongcapital.chorus.das.entity.web.CommonCause;
import cn.rongcapital.chorus.das.entity.web.ExecuteHistoryCause;

public class SqlQuerySqlProvider {

	public String list(final ExecuteHistoryCause cause) {
		SQL sql = new SQL() {
			{
				SELECT("a.execute_history_id as executeHistoryId, a.execute_status as executeStatus, a.execute_sql as executeSql, a.execute_time as executeTime, a.create_user as createUser, a.create_time as createTime, a.update_user as updateUser, a.update_time as updateTime");
				FROM("execute_history a");
				appendWhere(this, cause);
				ORDER_BY("a.create_time desc");
			}
		};
		return getLimitSql(sql.toString(), cause);
	}

	public String count(final ExecuteHistoryCause cause) {
		SQL sql = new SQL() {
			{
				SELECT("COUNT(1)");
				FROM("execute_history a");
				appendWhere(this, cause);
			}
		};
		return sql.toString();
	}

	private void appendWhere(SQL sql, final ExecuteHistoryCause cause) {
		StringBuffer where = new StringBuffer();
		if (!StringUtils.isBlank(cause.getUserId())) {
			where.append("a.create_user=#{userId}");
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
