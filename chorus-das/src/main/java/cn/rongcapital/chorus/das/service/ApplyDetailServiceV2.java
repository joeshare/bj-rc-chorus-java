package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ApplyDetailDOV2;

import java.util.List;
import java.util.Map;

/**
 * just version 2 for api {@link ApplyDetailService}
 */
public interface ApplyDetailServiceV2 {

    /**
     * 1.根据申请单ID，查询申请单明细信息
     *
     * @param applyFormId
     *
     * @return
     */
    List<ApplyDetailDOV2> selectApplyFormDetail(Long applyFormId);

    /**
     * 查询最高关注度表
     * @param projectId 项目编号
     * @param size 表数量
     * @return resourceName：表名 ；attCount：关注度（用户申请次数）
     * @author gonglin
     * @time 2017年6月23日
     */
    List<Map<String, Object>> getTopAttRateTableInfo(long projectId, int size);
}
