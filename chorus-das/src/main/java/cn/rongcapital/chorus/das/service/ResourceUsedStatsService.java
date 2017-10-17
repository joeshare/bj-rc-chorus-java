package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ResourceUsedStats;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by hhlfl on 2017-6-19.
 */
public interface ResourceUsedStatsService {
    int deleteAll();
    /***
     * 批量插入数据，如果存在则替换
     * @param records
     * @return
     */
    int batchInsert(List<ResourceUsedStats> records);

    /***
     * 统计表使用度topN
     *
     * @param projectId
     * @param resourceTypes
     * @param topN
     * @return
     */
    List<ResourceUsedStats> statsTableUsedTopN(long projectId, List<String> resourceTypes, int topN);

    /***
     * 批量插入同时并覆盖之前的数据
     *
     * @param records
     */
    void batchInsertWitchOverride(List<ResourceUsedStats> records);
}
