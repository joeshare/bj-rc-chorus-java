package cn.rongcapital.chorus.das.dao;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import cn.rongcapital.chorus.das.entity.web.CommonCause;
import cn.rongcapital.chorus.das.entity.web.OperLogCause;

/**
 * 登录日志模块SQL PROVIDER
 * @author MABOXIAO
 *
 */
public class OperLogSqlProvider {
	
	public String count(final OperLogCause cause){
		SQL sql = new SQL() {
            {
                SELECT("COUNT(1)");
                FROM("t_oper_log a");
                appendWhere(this, cause);
            }
        };
        return sql.toString();
	}
	
	public String list(final OperLogCause cause){
		SQL sql = new SQL() {
            {
                SELECT(" a.oper_id as operId,a.from_table as fromTable,a.oper_log as operLog,a.oper_user_id as operUserId,a.create_time as createTime,a.update_time as updateTime,a.record_key as recordKey ");
                appendWhere(this, cause);
                FROM("t_oper_log a");
                ORDER_BY("a.create_time desc");
            }
        };
        return getLimitSql(sql.toString(), cause);
	}
	
	private void appendWhere(SQL sql, final OperLogCause cause) {
		StringBuffer where = new StringBuffer();
		boolean firstFlag = true;
		if (!StringUtils.isBlank(cause.getFromTable())) {
			where.append("(a.from_table LIKE #{fromTable})");
			firstFlag = false;
		}
		
		if (!StringUtils.isBlank(cause.getCreateTime())) {
			if (firstFlag) {
				where.append("(a.create_time >= #{createTime})");
				firstFlag = false;
			} else {
				where.append(" AND (a.create_time >= #{createTime})");
			}
		}
		
		if (!StringUtils.isBlank(cause.getUpdateTime())) {
			if (firstFlag) {
				where.append("(a.update_time <= #{updateTime})");
				firstFlag = false;
			} else {
				where.append(" AND (a.update_time <= #{updateTime})");
			}
		}
		
		if (!StringUtils.isBlank(cause.getOperUserId())) {
			if (firstFlag) {
				where.append("(a.oper_user_id = #{operUserId})");
				firstFlag = false;
			} else {
				where.append(" AND (a.oper_user_id = #{operUserId})");
			}
		}
		if (!StringUtils.isBlank(cause.getRecordKey())) {
			if (firstFlag) {
				where.append("(a.record_key = #{recordKey})");
				firstFlag = false;
			} else {
				where.append(" AND (a.record_key = #{recordKey})");
			}
		}
		if (!firstFlag) {
			sql.WHERE(where.toString());
		}
    }
	
	private String getLimitSql(String sql, final CommonCause cause) {
		if (cause.getStartIndex() != null) {
			return sql + " LIMIT " + cause.getStartIndex() + "," + CommonCause.getRows();
		} else {
			return sql;
		}
	}

}
