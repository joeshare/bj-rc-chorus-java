package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.dao.TableAuthorityDOMapper;
import cn.rongcapital.chorus.das.entity.ColumnInfo;
import cn.rongcapital.chorus.das.entity.TableAuthorityDO;
import cn.rongcapital.chorus.das.entity.TableAuthorityWithTableDO;
import cn.rongcapital.chorus.das.service.ColumnInfoService;
import cn.rongcapital.chorus.das.service.TableAuthorityService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fuxiangli on 2016-11-30.
 */
@Service(value = "TableAuthorityService")
public class TableAuthorityServiceImpl implements TableAuthorityService {
    @Autowired
    private TableAuthorityDOMapper tableAuthorityDOMapper;
    @Autowired
    private ColumnInfoService columnInfoService;

    @Override
    public List<TableAuthorityDO> selectByUserId(String userId) {
        List<TableAuthorityDO> res = Lists.newLinkedList();
        res.addAll(tableAuthorityDOMapper.selectByAppliedUserId(userId));
        res.addAll(tableAuthorityDOMapper.selectByAdminAndOwnerId(userId));
        return res;
    }

    @Override
    public List<TableAuthorityWithTableDO> selectTableByUserId(String userId) {
        List<TableAuthorityWithTableDO> res = Lists.newLinkedList();
        res.addAll(tableAuthorityDOMapper.selectTableByAppliedUserId(userId));
        res.addAll(tableAuthorityDOMapper.selectTableByAdminAndOwnerId(userId));
        return res;
    }

    @Override
    public List<Long> selectByUserIdAndTableInfoId(String userId, Long tableInfoId) {
        boolean isOwnerOrAdmin = tableAuthorityDOMapper.selectByAdminAndOwnerId(userId).stream()
                .anyMatch(ta -> ta.getTableInfoId().equals(tableInfoId));
        if (isOwnerOrAdmin) {
            return columnInfoService.selectColumnInfo(tableInfoId).stream()
                    .map(ColumnInfo::getColumnInfoId)
                    .collect(Collectors.toList());
        } else {
            return tableAuthorityDOMapper.selectByUserIdAndTableInfoId(userId, tableInfoId,
                    Arrays.asList(StatusCode.APPLY_SUBMITTED.getCode(), StatusCode.APPLY_UNTREATED.getCode())
            ).stream().map(TableAuthorityDO::getColumnInfoId).collect(Collectors.toList());
        }
    }

}
