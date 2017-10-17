package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.dao.v2.*;
import cn.rongcapital.chorus.das.entity.*;
import cn.rongcapital.chorus.das.service.ApplyFormServiceV2;
import cn.rongcapital.chorus.das.service.ColumnInfoServiceV2;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yimin
 */
@Component
public class ApplyFormServiceV2Impl implements ApplyFormServiceV2 {
    @Autowired
    private ApplyFormMapperV2      applyFormMapper;
    @Autowired
    private ApplyFormDOMapperV2    applyFormDOMapper;
    @Autowired
    private ApplyDetailDOMapperV2  applyDetailDOMapper;
    @Autowired
    private TableAuthorityMapperV2 tableAuthorityMapper;
    @Autowired
    private TableAuthorityDOMapperV2 tableAuthorityDOMapper;
    @Autowired
    private TableInfoServiceV2 tableInfoServiceV2;
    @Autowired
    private ColumnInfoServiceV2 columnInfoServiceV2;

    @Override
    public int bulkInsert(@Nonnull String tableInfoId, List<String> columnInfoIdList, Integer duration, String reason, String userId, String userName) {
        final TableInfoV2 tableInfoV2 = tableInfoServiceV2.selectByID(tableInfoId);
        assert  tableInfoV2!=null;

        ApplyFormV2 applyForm = new ApplyFormV2();
        applyForm.setTableInfoId(tableInfoId);
        applyForm.setApplyUserId(userId);
        applyForm.setApplyTime(new Date());
        applyForm.setReason(reason);
        applyForm.setTableName(tableInfoV2.getTableName());
        applyForm.setApplyUserName(userName);
        applyForm.setStatusCode(StatusCode.APPLY_SUBMITTED.getCode());
        applyForm.setProjectId(tableInfoV2.getProjectId());
        applyFormMapper.insertSelective(applyForm);
        List<ApplyDetailV2> applyDetailList = columnInfoIdList.stream().map(cId -> {
            ApplyDetailV2 ad = new ApplyDetailV2();
            ad.setApplyFormId(applyForm.getApplyFormId());
            ad.setTableInfoId(applyForm.getTableInfoId());
            ad.setColumnInfoId(cId);
            ad.setStatusCode(StatusCode.APPLY_SUBMITTED.getCode());
            return ad;
        }).collect(Collectors.toList());
        applyDetailDOMapper.bulkInsert(applyDetailList);
        return 0;
    }

    @Override
    public int approve(ApplyFormV2 applyForm) {
        if (applyForm != null) {
            applyForm.setDealTime(new Date());
            applyFormDOMapper.approve(applyForm);

            //如果被拒绝不应该往table_authority里面插入记录
            if(applyForm.getStatusCode().equals(StatusCode.APPLY_TREATED.getCode()))
                return 1;


            ApplyFormV2 formEntity = applyFormMapper.selectByPrimaryKey(applyForm.getApplyFormId());
            TableInfoV2 tableInfoV2 = tableInfoServiceV2.selectByID(formEntity.getTableInfoId());
//            String tableInfoId = formEntity.getTableInfoId();
            List<ApplyDetailDOV2> detailEntityList = applyDetailDOMapper.selectByFormId(applyForm.getApplyFormId());
            detailEntityList.forEach(d -> {
                ColumnInfoV2 columnInfo = columnInfoServiceV2.getColumnInfo(d.getColumnInfoId());
                TableAuthorityV2 tableAuthority = new TableAuthorityV2();
                tableAuthority.setTableInfoId(tableInfoV2.getTableInfoId());
                tableAuthority.setColumnInfoId(columnInfo.getColumnInfoId());
                tableAuthority.setUserId(formEntity.getApplyUserId());
                tableAuthority.setTableName(tableInfoV2.getTableName());
                tableAuthority.setColumnName(columnInfo.getColumnName());
                tableAuthority.setProjectId(formEntity.getProjectId());
                TableAuthorityV2 tableAuthorityV2 = tableAuthorityDOMapper.selectByUnique(tableAuthority.getUserId(), tableAuthority.getColumnInfoId());
                if(tableAuthorityV2 == null) {
                    tableAuthorityMapper.insert(tableAuthority);
                }else {
                    tableAuthority.setTableAuthorityId(tableAuthorityV2.getTableAuthorityId());
                    tableAuthorityMapper.updateByPrimaryKey(tableAuthority);
                }
            });
            return 1;
        }
        return 0;
    }

    @Override
    public List<ApplyFormDOV2> selectForm(String userId, boolean approved, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return applyFormDOMapper.selectForm(userId, approved);
    }

    @Override
    public List<ApplyFormDOV2> selectAllForm(String userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return applyFormDOMapper.selectAllForm(userId);
    }

    @Override
    public ApplyFormV2 selectByPrimaryKey(Long applyFormId) {
        return applyFormMapper.selectByPrimaryKey(applyFormId);
    }
}
