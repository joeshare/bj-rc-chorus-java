package cn.rongcapital.chorus.das.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import cn.rongcapital.chorus.das.entity.ExecuteHistory;
import cn.rongcapital.chorus.das.entity.web.ExecuteHistoryCause;

public interface SqlQueryMapper {
	
	/**
	 * 查询 执行历史
	 * @param cause
	 * @return
	 */
	@SelectProvider(type=SqlQuerySqlProvider.class,method="list")
    List<ExecuteHistory> list(final ExecuteHistoryCause cause);
	
	/**
	 * 查询 执行历史数量
	 * @param cause
	 * @return
	 */
	@SelectProvider(type=SqlQuerySqlProvider.class,method="count")
    int count(final ExecuteHistoryCause cause);

	/**
	 * 添加数据任务
	 *
	 * @param executeHistory
	 * @return
	 */
	@Insert({
		"insert into execute_history (execute_history_id, execute_status, execute_sql, execute_time, create_user, create_time, update_user, update_time ) values (" +
		"#{executeHistoryId,jdbcType=INTEGER}, ",
		"#{executeStatus,jdbcType=INTEGER}, ",
		"#{executeSql,jdbcType=VARCHAR},",
		"#{executeTime,jdbcType=INTEGER}, ",
		"#{createUser,jdbcType=VARCHAR}, ",
		"#{createTime,jdbcType=TIMESTAMP}, ",
		"#{updateUser,jdbcType=VARCHAR}, ",
		"#{updateTime,jdbcType=TIMESTAMP}) "
	})
	@Options(useGeneratedKeys=true, keyProperty="executeHistoryId", keyColumn="execute_history_id")
	int insert(ExecuteHistory executeHistory);

	/**
	 * 列出所有数据任务
	 *
	 * @return
	 */
	@Select({
		"select execute_history_id as executeHistoryId, execute_status as executeStatus, execute_sql as executeSql, execute_time as executeTime, create_user as createUser, create_time as createTime,update_user as updateUser,update_time as updateTime ",
		" from execute_history ",
		" where create_user= #{userId}"
	})
	List<ExecuteHistory> selectByUserId(String userId);
	
	/**
	 * 更新
	 * @param job
	 * @return
	 */
	@Update({
		"update execute_history set execute_status=#{executeStatus},",
		"execute_time=#{executeTime}",
		"where execute_history_id=#{executeHistoryId} "
	})
	int updateStatusAndTime(ExecuteHistory executeHistory);
}
