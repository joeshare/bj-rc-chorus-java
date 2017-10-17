package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ColumnInfo;
import cn.rongcapital.chorus.das.entity.TableInfo;
import cn.rongcapital.chorus.das.entity.TableInfoDO;

import java.util.List;

public interface MetadataService {
    /**
     * 1.查询所有表信息
     *
     * @return
     */
    List<TableInfoDO> selectAllTable(int pageNum,int pageSize);
    /**
     * 2.根据表名称,列名称，模糊查询表信息
     *
     * @param tableColumnName
     * @param pageNum
     *@param pageSize @return
     */
    List<TableInfoDO> selectByFuzzyName(String tableColumnName, int pageNum, int pageSize);
    /**
     * 3.表和列权限申请
     *
     * @param record
     * @return
     */
    int insertTableAuthority(TableInfo record);
    /**
     * 4.根据表ID,查询列信息
     *
     * @param tableId
     * @return
     */
    List<ColumnInfo> selectColumnInfo(Long tableId);


}
