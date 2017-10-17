package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ApplyFormDOV2;
import cn.rongcapital.chorus.das.entity.ApplyFormV2;

import java.util.List;

/**
 * Created by fuxiangli on 2016-11-24.
 */
public interface ApplyFormServiceV2 {

    /**
     * 提交申请单
     * @param tableInfoId 表Id
     * @param columnInfoIdList 列Id列表
     * @param duration 时长, 目前单位是天
     * @param reason 申请原因
     * @param userId 申请人Id
     * @return 提交结果
     */
    int bulkInsert(String tableInfoId, List<String> columnInfoIdList, Integer duration, String reason, String userId, String userName);

    int approve(ApplyFormV2 applyForm);

    /**
     * 3.查询申请单信息
     *
     * @param userId
     * @param approved  状态编码：1501 未审核 1502 已通过，1503 拒绝
     * @return
     */
    List<ApplyFormDOV2> selectForm(String userId, boolean approved, int pageNum, int pageSize);

    /**
     * 3.查询所有申请单信息
     *
     * @param userId
     * @return
     */
    List<ApplyFormDOV2> selectAllForm(String userId, int pageNum, int pageSize);

    ApplyFormV2 selectByPrimaryKey(Long applyFormId);

}
