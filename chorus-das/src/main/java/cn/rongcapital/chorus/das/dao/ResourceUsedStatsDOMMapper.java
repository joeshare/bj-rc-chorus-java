package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ResourceUsedStats;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ResourceUsedStatsDOMMapper {
    /***
     * 删除所有的数据
     * @return
     */
    int deleteAll();
    /***
     * 批量插入数据，如果存在则替换
     * @param records
     * @return
     */
    int batchInsert(@Param("records") List<ResourceUsedStats> records);

    /***
     * 统计表使用度topN
     *
     * @param projectId
     * @param resourceTypes
     * @param topN
     * @return
     */
    List<ResourceUsedStats> statsTableUsedTopN(@Param("projectId")long projectId, @Param("rTypes") List<String> resourceTypes, @Param("topN")int topN);
}