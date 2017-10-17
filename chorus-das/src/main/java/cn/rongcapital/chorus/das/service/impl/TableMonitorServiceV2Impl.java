package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.v2.TableMonitorMapperV2;
import cn.rongcapital.chorus.das.entity.TableMonitorInfoV2;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import cn.rongcapital.chorus.das.service.TableMonitorServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class TableMonitorServiceV2Impl implements TableMonitorServiceV2 {

    @Autowired
    private TableMonitorMapperV2 monitorMapper;
    @Autowired
    private TableInfoServiceV2 tableInfoServiceV2;

    @Override
    public int insert(TableMonitorInfoV2 table) {
        return monitorMapper.insert(table);
    }

    @Override
    public List<TableMonitorInfoV2> selectRowsTop(Long projectId, Integer top) {
        return monitorMapper.selectRowsTop(projectId, top);
    }

    @Override
    public List<TableMonitorInfoV2> selectStorageTop(Long projectId, Integer top) {
        return monitorMapper.selectStorageTop(projectId, top);
    }

    @Override
    public long selectStorageTotal(Long projectId) {
        return monitorMapper.selectStorageTotal(projectId);
    }

    @Override
    public long getTablesTotal(Long projectId) {
        return tableInfoServiceV2.countTables(projectId);
    }
    @Override
    public int clearToday() {
        return monitorMapper.clearToday();
    }

}
