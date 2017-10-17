package cn.rongcapital.chorus.das.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import cn.rongcapital.chorus.das.entity.StreamExecStatistic;

/**
 * 流式任务执行统计Mapper
 * @author kevin.gong
 * @Time 2017年6月20日 上午10:03:41
 */
public interface StreamExecStatisticMapper {

    /**
     * 获取流式任务执行分布
     * @param projectId 项目编号
     * @param startDate 开始日期
     * @return date:日期，noExecNum：未执行数量，failedNum：执行失败数量，runningNum：正在执行数量
     */
	@Select({
        "select `date`,sum(no_exec_num) as noExecNum,sum(failed_num) as failedNum,sum(running_num) as runningNum from stream_exec_statistic"  +
	    " where project_id=#{projectId} and `date`>=#{startDate} and `date`<CURDATE() group by `date` order by `date`" })
    List<Map<String, Object>> getStreamExecDist(@Param("projectId")long projectId, @Param("startDate")String startDate);
	
	@Insert({
        "INSERT INTO `stream_exec_statistic` (`project_id`, `no_exec_num`, `failed_num`, `running_num`, `date`) VALUES (",
        "#{projectId,jdbcType=BIGINT}, ",
        "#{noExecNum,jdbcType=INTEGER}, ",
        "#{failedNum,jdbcType=INTEGER},",
        "#{runningNum,jdbcType=INTEGER},",
        "date_add(CURDATE(), interval -1 day))"
    })
    int add(StreamExecStatistic streamExecStatistic);
}
