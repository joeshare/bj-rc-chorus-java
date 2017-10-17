package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.TableInfo;
import cn.rongcapital.chorus.das.entity.TableInfoDO;

import java.util.List;

public interface TableManageMapper {
    List<TableInfoDO> selectByFuzzyName(String tableColumnName);

    List<TableInfo> selectByTableName(String tableName);

    List<TableInfo> selectByProjectID(Long projectId);

    List<TableInfoDO> selectAllTable();

    //查询表名和库名清单
    List<TableInfoDO> selectTableList(Long projectId);
    //查询表名清单
    List<String> selectTableName(Long projectId);
}