package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.dao.ColumnManageMapper;
import cn.rongcapital.chorus.das.dao.TableInfoMapper;
import cn.rongcapital.chorus.das.dao.TableManageMapper;
import cn.rongcapital.chorus.das.entity.ColumnInfo;
import cn.rongcapital.chorus.das.entity.TableInfo;
import cn.rongcapital.chorus.das.entity.TableInfoDO;
import cn.rongcapital.chorus.das.service.HiveTableInfoService;
import cn.rongcapital.chorus.das.service.TableInfoService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据管理-表基本信息模块SERVICE实现类
 *
 * @author fuxiangli
 */

@Service(value = "TableInfoService")
public class TableInfoServiceImpl implements TableInfoService {

    @Autowired
    private TableManageMapper tableManageMapper;
    @Autowired
    private TableInfoMapper tableInfoMapper;
    @Autowired
    private ColumnManageMapper columnManageMapper;
    @Autowired
    private HiveTableInfoService hiveTableInfoService;

    /**
     * 1.根据表ID，查询表信息
     *
     * @param tableId
     * @return
     */
    @Override
    public TableInfo selectByID(Long tableId) {

        TableInfo tableInfo = tableInfoMapper.selectByPrimaryKey(tableId);
        return tableInfo;
    }

    /**
     * 2.根据项目ID，查询表信息
     *
     * @param projectId
     * @return
     */
    @Override
    public List<TableInfo> listAllTableinfo(Long projectId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TableInfo> tableInfo = tableManageMapper.selectByProjectID(projectId);
        return tableInfo;
    }

    /**
     * 4.插入数据
     *
     * @param tableInfo
     * @param columnInfoList
     * @return
     */
    @Override
    @Transactional
    public int bulkInsert(TableInfo tableInfo, List<ColumnInfo> columnInfoList) {
        Date create_time = new Date();
        if (tableInfo != null || columnInfoList != null) {
            tableInfo.setCreateTime(create_time);
            tableInfo.setStatusCode(StatusCode.TABLE_CREATED.getCode());
            tableInfoMapper.insertSelective(tableInfo);
            columnInfoList.forEach(ad -> {
                ad.setTableInfoId(tableInfo.getTableInfoId());
                ad.setCreateTime(create_time);
                ad.setStatusCode(StatusCode.COLUMN_CREATED.getCode());
            });
            columnManageMapper.bulkInsert(columnInfoList);
        }
        return 0;
    }

    @Transactional
    @Override
    public void createTable(TableInfo tableInfo, List<ColumnInfo> columnInfoList) {
        bulkInsert(tableInfo, columnInfoList);
        hiveTableInfoService.createTable(tableInfo, columnInfoList);
    }
    
    @Override
    public List<TableInfoDO> listAllTables(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return tableManageMapper.selectAllTable();
    }

    @Override
    public List<Map<String, Object>> getTopAttRateTableInfo(long projectId, int size) {
        return tableInfoMapper.selectTopAttRateTable(projectId, size);
    }
}
