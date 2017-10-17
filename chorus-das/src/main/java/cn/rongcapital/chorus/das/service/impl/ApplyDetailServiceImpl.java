package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.ApplyDetailDOMapper;
import cn.rongcapital.chorus.das.entity.ApplyDetailDO;
import cn.rongcapital.chorus.das.service.ApplyDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by fuxiangli on 2016-11-28.
 */
@Service(value = "ApplyDetailService")
public class ApplyDetailServiceImpl implements ApplyDetailService {
    @Autowired
    private ApplyDetailDOMapper applyDetailDOMapper;
    /**
     * 1.根据申请单ID，查询申请单明细信息
     *
     * @param applyFormId
     * @return
     */
    @Override
    public List<ApplyDetailDO> selectApplyFormDetail(Long applyFormId) {
        return applyDetailDOMapper.selectByFormId(applyFormId);
    }
}
