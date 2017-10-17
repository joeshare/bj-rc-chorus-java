package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.ColumnManageMapper;
import cn.rongcapital.chorus.das.dao.TableInfoMapper;
import cn.rongcapital.chorus.das.dao.TableManageMapper;
import cn.rongcapital.chorus.das.entity.ColumnInfo;
import cn.rongcapital.chorus.das.entity.TableInfo;
import cn.rongcapital.chorus.das.entity.TableInfoDO;
import cn.rongcapital.chorus.das.service.MetadataService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by fuxiangli on 2016-11-16.
 */

@Service(value = "MetadataService")

public class MetadataServiceImpl implements MetadataService {
    @Autowired
    private TableManageMapper tableManageMapper;
    @Autowired
    private TableInfoMapper tableInfoMapper;
    @Autowired
    private ColumnManageMapper columnManageMapper;
    /**
     * 1.查询所有表信息
     *
     * @return
     */
    @Override
    public List<TableInfoDO> selectAllTable(int pageNum,int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TableInfoDO> tableInfoDOs;
        tableInfoDOs = tableManageMapper.selectAllTable();
        return tableInfoDOs;
    }
    /**
     * 2.根据表名称,列名称，模糊查询表信息
     *
     * @param tableColumnName
     * @param pageNum
     *@param pageSize @return
     */
    @Override
    public List<TableInfoDO> selectByFuzzyName(String tableColumnName, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TableInfoDO> tabInfo = tableManageMapper.selectByFuzzyName(tableColumnName);
        return tabInfo;
    }
    /**
     * 3.表和列权限申请
     *
     * @param record
     * @return
     */
    @Override
    public int insertTableAuthority(TableInfo record) {
        if (record != null) {
            tableInfoMapper.insert(record);
        }
        return 0;
    }
    /**
     * 4.根据表ID,查询列信息
     *
     * @param tableId
     * @return
     */
    @Override
    public List<ColumnInfo> selectColumnInfo(Long tableId) {
        List<ColumnInfo> columnInfo;
        columnInfo = columnManageMapper.selectByTableId(tableId);
        return columnInfo;
    }
}
