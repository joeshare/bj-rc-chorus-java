package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ColumnInfo;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.TableInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by hhlfl on 2017-7-25.
 */
public interface MetaDataMigrateService {
    List<ProjectInfo> getAllProjectInfos();
    List<TableInfo> getAllTableInfos();
    int[] updateTableInfo(Map<String,TableInfo> tableInfoMap);
    int[] updateColumnInfo(Map<String, ColumnInfo> columnInfoMap);
}
