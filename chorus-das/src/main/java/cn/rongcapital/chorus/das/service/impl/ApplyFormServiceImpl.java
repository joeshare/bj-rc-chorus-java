package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.dao.ApplyDetailDOMapper;
import cn.rongcapital.chorus.das.dao.ApplyFormDOMapper;
import cn.rongcapital.chorus.das.dao.ApplyFormMapper;
import cn.rongcapital.chorus.das.dao.TableAuthorityMapper;
import cn.rongcapital.chorus.das.entity.*;
import cn.rongcapital.chorus.das.service.ApplyFormService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fuxiangli on 2016-11-24.
 */

@Service(value = "ApplyFormService")
public class ApplyFormServiceImpl implements ApplyFormService {
    @Autowired
    private ApplyFormMapper applyFormMapper;
    @Autowired
    private ApplyFormDOMapper applyFormDOMapper;
    @Autowired
    private ApplyDetailDOMapper applyDetailDOMapper;
    @Autowired
    private TableAuthorityMapper tableAuthorityMapper;

    @Override
    @Transactional
    public int bulkInsert(Long tableInfoId, List<Long> columnInfoIdList, Integer duration, String reason, String userId, String userName) {
        ApplyForm applyForm = new ApplyForm();
        applyForm.setTableInfoId(tableInfoId);
        applyForm.setApplyUserId(userId);
        applyForm.setApplyTime(new Date());
        applyForm.setReason(reason);
        applyForm.setApplyUserName(userName);
        applyForm.setStatusCode(StatusCode.APPLY_SUBMITTED.getCode());
        applyFormMapper.insertSelective(applyForm);
        List<ApplyDetail> applyDetailList = columnInfoIdList.stream().map(cId -> {
            ApplyDetail ad = new ApplyDetail();
            ad.setApplyFormId(applyForm.getApplyFormId());
            ad.setTableInfoId(applyForm.getTableInfoId());
            ad.setColumnInfoId(cId);
            ad.setStatusCode(StatusCode.APPLY_SUBMITTED.getCode());
            return ad;
        }).collect(Collectors.toList());
        applyDetailDOMapper.bulkInsert(applyDetailList);
        return 0;
    }

    /**
     * 2.修改申请单
     *
     * @param applyForm
     * @return
     */
    @Override
    public int approve(ApplyForm applyForm) {
        if (applyForm != null) {
            applyForm.setDealTime(new Date());
            applyFormDOMapper.approve(applyForm);

            ApplyForm formEntity = applyFormMapper.selectByPrimaryKey(applyForm.getApplyFormId());
            Long tableInfoId = formEntity.getTableInfoId();
            List<ApplyDetailDO> detailEntityList = applyDetailDOMapper.selectByFormId(applyForm.getApplyFormId());
            detailEntityList.forEach(d -> {
                TableAuthority tableAuthority = new TableAuthority();
                tableAuthority.setTableInfoId(tableInfoId);
                tableAuthority.setColumnInfoId(d.getColumnInfoId());
                tableAuthority.setUserId(formEntity.getApplyUserId());
                tableAuthorityMapper.insert(tableAuthority);
            });
            return 1;
        }
        return 0;
    }

    /**
     * 3.查询申请单信息
     *
     * @param userId
     * @param approved 状态编码：1501 未审核 1502 已通过，1503 拒绝
     * @return
     */
    @Override
    public List<ApplyFormDO> selectForm(String userId, boolean approved, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return applyFormDOMapper.selectForm(userId, approved);
    }

    @Override
    public List<ApplyFormDO> selectAllForm(String userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return applyFormDOMapper.selectAllForm(userId);
    }

    @Override
    public ApplyForm selectByPrimaryKey(Long applyFormId) {
        return applyFormMapper.selectByPrimaryKey(applyFormId);
    }

}
