package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.web.CommonCause;
import cn.rongcapital.chorus.das.entity.web.JobCause;
import cn.rongcapital.chorus.das.entity.web.TaskCause;
import org.apache.ibatis.jdbc.SQL;


/**
 * Job模块SQL PROVIDER
 *
 * @author lengyang
 *
 */
public class TaskSqlProvider {

	public String validTaskName(final TaskCause cause) {
		SQL sql = new SQL() {
			{
				SELECT(" task_id as taskId, job_id as jobId, module_name as moduleName,task_name as taskName,alias_name as aliasName,data_input as dataInput,data_output as dataOutput,config,task_dsl as taskDSL, description, variable, create_user as createUser, create_time as createTime,update_user as updateUser,update_time as updateTime ");
				FROM("task");
				appendCheckNameWhere(this, cause);
			}
		};
		return getLimitSql(sql.toString(), cause);
	}

	private void appendCheckNameWhere(SQL sql, final TaskCause cause) {
		StringBuffer where = new StringBuffer();
		where.append(" 1 = 1 ");

//		if (cause.getJobId() != null) {
//			// 非当前Job
//			where.append(" AND job_id <> #{jobId} ");
//		}

		if (cause.getTaskId() != null) {
			// 非当前TaskId
			where.append(" AND task_id <> #{taskId} ");
		}
//		where.append(" AND project_id = #{projectId} ");
//		where.append(" AND job_type = #{jobType} ");
		where.append(" AND task_name = #{taskName} ");
		sql.WHERE(where.toString());
	}

	private String getLimitSql(String sql, final CommonCause cause) {
		if (cause.getStartIndex() != null) {
			return sql + " LIMIT " + cause.getStartIndex() + "," + cause.getRowCnt();
		} else {
			return sql;
		}
	}

}
