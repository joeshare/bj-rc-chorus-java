package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.OperLog;
import cn.rongcapital.chorus.das.entity.web.OperLogCause;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * 登录日志模块Mapper
 * @author lipeng
 *
 * 2016年3月16日
 */
public interface OperLogMapper {

    /**
     * 查询登录日志
     *
     * @param cause
     * @return
     */
    @SelectProvider(type = OperLogSqlProvider.class, method = "list")
    List<OperLog> list(final OperLogCause cause);

    /**
     * 取得登录日志数量
     *
     * @param cause
     * @return
     */
    @SelectProvider(type = OperLogSqlProvider.class, method = "count")
    int count(final OperLogCause cause);

    /**
     * 添加登录日志
     *
     * @return
     */
    @Insert({
            "insert into t_oper_log (oper_id, from_table, oper_log, oper_user_id, create_time, update_time, record_key) values (" +
                    "#{operId,jdbcType=VARCHAR}, ",
            "#{fromTable,jdbcType=VARCHAR}, ",
            "#{operLog,jdbcType=VARCHAR},",
            "#{operUserId,jdbcType=TIMESTAMP},",
            "#{createTime,jdbcType=DECIMAL},",
            "#{updateTime,jdbcType=VARCHAR}, ",
            "#{recordKey,jdbcType=VARCHAR}) "
    })
    int insert(OperLog operLog);
}
