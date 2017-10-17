package cn.rongcapital.chorus.das.dao;

import java.util.Map;

import org.apache.ibatis.jdbc.SQL;

import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.entity.web.CommonCause;
import cn.rongcapital.chorus.das.entity.web.JobCause;


/**
 * Job模块SQL PROVIDER
 *
 * @author lengyang
 *
 */
public class JobSqlProvider {

	public String getProjectJobList(final JobCause cause) {
		SQL sql = new SQL() {
			{
				SELECT(" a.project_id AS projectId,a.project_code AS projectCode, a.project_name AS projectName,  b" +
						".job_id AS jobId, b.job_type AS jobType,  b.job_name AS jobName, b.job_alias_name AS jobAliasName, b.instance_id as instanceId, b.work_flow_dsl AS workFlowDSL, b.description,b.job_parameters AS jobParameters,b.status AS status, b.data_input AS dataInput, b.data_output AS dataOutput,b.deploy_user AS deployUserId,b.deploy_user_name AS deployUserName, b.create_user AS createUser,b.create_user_name AS createUserName, b.create_time AS createTime, b.update_user AS updateUser, b.update_time AS updateTime ");
				appendFrom(this, cause);
				appendWhere(this, cause);
				ORDER_BY("a.create_time ,b.create_time");
			}
		};
		return getLimitSql(sql.toString(), cause);
	}

	public String count(final JobCause cause) {
		SQL sql = new SQL() {
			{
				SELECT("COUNT(1)");
				appendFrom(this, cause);
				appendWhere(this, cause);
			}
		};
		return sql.toString();
	}

	private void appendFrom(SQL sql, final JobCause cause) {
//		if ("Y".equals(cause.getSysUserYn())) {
//			// 系统管理员查看全部
//			sql.FROM("project_info a LEFT JOIN job b ON a.project_id = b.project_id AND b.job_type = #{jobType} AND b.use_yn = 'Y' ");
//		} else {
			// 非系统管理员
			sql.FROM("project_info a LEFT JOIN job b ON a.project_id = b.project_id AND b.use_yn = 'Y' ");
//		}
	}

	private void appendWhere(SQL sql, final JobCause cause) {
		StringBuffer where = new StringBuffer();

		where.append(" b.project_id = #{projectId} ");
//		where.append(" 1 = 1 ");
		if (cause.getJobType() != 3) {
			// 任务类型(1:实时 2:定期 3:全部)
			where.append(" AND b.job_type = #{jobType} ");
		}
		if (StringUtils.isNotBlank(cause.getStatus())) {
			where.append(" AND b.status = #{status} ");
		}
		if (StringUtils.isNotBlank(cause.getJobAliasName())) {
			cause.setSearchKey(cause.getJobAliasName());
			where.append(" AND b.job_alias_name LIKE #{searchKey}");
		}
		sql.WHERE(where.toString());
	}

	public String checkProjectJobByAsName(final JobCause cause) {
		SQL sql = new SQL() {
			{
				SELECT(" job_id as jobId, job_type as jobType, project_id as projectId, job_name as jobName, job_alias_name as jobAliasName, instance_id as instanceId, work_flow_dsl AS workFlowDSL, description,job_parameters as jobParameters,status as status, data_input as dataInput, data_output as dataOutput, use_yn as useYn, create_user as createUser, create_time as createTime,update_user as updateUser,update_time as updateTime ");
				FROM("job");
				appendCheckAsNameWhere(this, cause);
			}
		};
		return getLimitSql(sql.toString(), cause);
	}

	private void appendCheckAsNameWhere(SQL sql, final JobCause cause) {
		StringBuffer where = new StringBuffer();
		where.append(" 1 = 1 ");

		if (cause.getJobId() != null) {
			// 非当前Job
			where.append(" AND job_id <> #{jobId} ");
		}
		where.append(" AND project_id = #{projectId} ");
		where.append(" AND job_type = #{jobType} ");
		where.append(" AND job_alias_name = #{jobAliasName} ");
		where.append(" AND use_yn = 'Y' ");
		sql.WHERE(where.toString());
	}

	public String validJobName(final JobCause cause) {
		SQL sql = new SQL() {
			{
				SELECT(" job_id as jobId, job_type as jobType, project_id as projectId, job_name as jobName, job_alias_name as jobAliasName, instance_id as instanceId, work_flow_dsl AS workFlowDSL, description,job_parameters as jobParameters,status as status, data_input as dataInput, data_output as dataOutput, use_yn as useYn, create_user as createUser, create_time as createTime,update_user as updateUser,update_time as updateTime  ");
				FROM("job");
				appendCheckNameWhere(this, cause);
			}
		};
		return getLimitSql(sql.toString(), cause);
	}

	private void appendCheckNameWhere(SQL sql, final JobCause cause) {
		StringBuffer where = new StringBuffer();
		where.append(" 1 = 1 ");

		if (cause.getJobId() != null) {
			// 非当前JobId
			where.append(" AND job_id <> #{jobId} ");
		}
		where.append(" AND job_name = #{jobName} ");
		where.append(" AND use_yn = 'Y' ");
		sql.WHERE(where.toString());
	}

	private String getLimitSql(String sql, final CommonCause cause) {
		if (cause.getStartIndex() != null) {
			return sql + " LIMIT " + cause.getStartIndex() + "," + cause.getRowCnt();
		} else {
			return sql;
		}
	}
	
	public String getAllJobByType(final Map<String, Object> map) {
        return new SQL() {
            {
                SELECT( "job_id as jobId, job_type as jobType, project_id as projectId, job_name as jobName, "+
                        "job_alias_name as jobAliasName, instance_id as instanceId, work_flow_dsl AS workFlowDSL, description, "+
                        "job_parameters as jobParameters, status as status, data_input as dataInput, data_output as dataOutput, use_yn as useYn, "+
                        "create_user as createUser, create_time as createTime, update_user as updateUser, update_time as updateTime ")
                .FROM("job");
                if((Boolean)map.get("onlyUsing")) {
                    WHERE("job_type = #{jobType} and use_yn = 'Y'");
                } else {
                    WHERE("job_type = #{jobType}");
                }
                ORDER_BY("projectId,jobId");
            }
        }.toString();
    }

}
