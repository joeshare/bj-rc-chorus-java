package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ApplyDetail;
import cn.rongcapital.chorus.das.entity.ApplyDetailDO;

import java.util.List;

/**
 * Created by fuxiangli on 2016-11-17.
 */
public interface ApplyDetailService {

    /**
     * 1.根据申请单ID，查询申请单明细信息
     *
     * @param applyFormId
     * @return
     */
    List<ApplyDetailDO> selectApplyFormDetail(Long applyFormId);
}
