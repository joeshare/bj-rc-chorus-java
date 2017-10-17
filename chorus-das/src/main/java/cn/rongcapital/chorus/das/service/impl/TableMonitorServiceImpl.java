package cn.rongcapital.chorus.das.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.rongcapital.chorus.das.dao.TableMonitorMapper;
import cn.rongcapital.chorus.das.entity.TableMonitorInfo;
import cn.rongcapital.chorus.das.service.TableMonitorService;

@Component
@Transactional
public class TableMonitorServiceImpl implements TableMonitorService {

    @Autowired
    private TableMonitorMapper monitorMapper;
    
    @Override
    public int insert(TableMonitorInfo table) {
        return monitorMapper.insert(table);
    }

    @Override
    public List<TableMonitorInfo> selectRowsTop(Long projectId, Integer top) {
        return monitorMapper.selectRowsTop(projectId, top);
    }

    @Override
    public List<TableMonitorInfo> selectStorageTop(Long projectId, Integer top) {
        return monitorMapper.selectStorageTop(projectId, top);
    }

    @Override
    public long selectStorageTotal(Long projectId) {
        return monitorMapper.selectStorageTotal(projectId);
    }

    @Override
    public long getTablesTotal(Long projectId) {
        return monitorMapper.getTablesTotal(projectId);
    }

    @Override
    public int clearToday() {
        return monitorMapper.clearToday();
    }

}
