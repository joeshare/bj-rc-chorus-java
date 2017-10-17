package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ApplyForm;
import cn.rongcapital.chorus.das.entity.ApplyFormDO;

import java.util.List;

/**
 * Created by fuxiangli on 2016-11-24.
 */
public interface ApplyFormService {

    /**
     * 提交申请单
     * @param tableInfoId 表Id
     * @param columnInfoIdList 列Id列表
     * @param duration 时长, 目前单位是天
     * @param reason 申请原因
     * @param userId 申请人Id
     * @return 提交结果
     */
    int bulkInsert(Long tableInfoId, List<Long> columnInfoIdList, Integer duration, String reason, String userId, String userName);

    int approve(ApplyForm applyForm);

    /**
     * 3.查询申请单信息
     *
     * @param userId
     * @param approved  状态编码：1501 未审核 1502 已通过，1503 拒绝
     * @return
     */
    List<ApplyFormDO> selectForm(String userId, boolean approved, int pageNum, int pageSize);

    /**
     * 3.查询所有申请单信息
     *
     * @param userId
     * @return
     */
    List<ApplyFormDO> selectAllForm(String userId,int pageNum,int pageSize);

    ApplyForm selectByPrimaryKey(Long applyFormId);

}
