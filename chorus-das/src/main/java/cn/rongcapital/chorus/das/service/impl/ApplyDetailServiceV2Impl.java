package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.v2.ApplyDetailDOMapperV2;
import cn.rongcapital.chorus.das.entity.ApplyDetailDOV2;
import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.service.ApplyDetailServiceV2;
import cn.rongcapital.chorus.das.service.ColumnInfoServiceV2;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import com.google.common.collect.ImmutableList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author yimin
 */
@Component
public class ApplyDetailServiceV2Impl implements ApplyDetailServiceV2 {
    @Autowired
    private ApplyDetailDOMapperV2 applyDetailDOMapper;
    @Autowired
    private ColumnInfoServiceV2   columnInfoServiceV2;
    @Autowired
    private TableInfoServiceV2 tableInfoServiceV2;

    @Override
    public List<ApplyDetailDOV2> selectApplyFormDetail(Long applyFormId) {
        final List<ApplyDetailDOV2> applyDetailDOV2s = applyDetailDOMapper.selectByFormId(applyFormId);
        applyDetailDOV2s.forEach(detail->{
            final ColumnInfoV2 columnInfo = columnInfoServiceV2.getColumnInfo(detail.getColumnInfoId());
            detail.setColumnName(columnInfo.getColumnName());
            detail.setColumnDesc(columnInfo.getColumnDesc());
            detail.setSecurityLevel(columnInfo.getSecurityLevel());
        });
        return applyDetailDOV2s;
    }

    @Override
    public List<Map<String, Object>> getTopAttRateTableInfo(long projectId, int size) {
        final List<Map<String, Object>> applyCountForTables = applyDetailDOMapper.getAllApplyInfoOfProject(projectId, size);
        if(CollectionUtils.isEmpty(applyCountForTables)) return ImmutableList.of();
        applyCountForTables.forEach(apply -> apply.put("resourceName", tableInfoServiceV2.selectByID(MapUtils.getString(apply, "tableInfoId")).getTableName()));
        return applyCountForTables;
    }
}
