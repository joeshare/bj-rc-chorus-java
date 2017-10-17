package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.MetaDataMigrateMapper;
import cn.rongcapital.chorus.das.entity.ColumnInfo;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.TableInfo;
import cn.rongcapital.chorus.das.service.MetaDataMigrateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by hhlfl on 2017-7-25.
 */
@Service
public class MetaDataMigrateServiceImpl implements MetaDataMigrateService {
    @Autowired
    private MetaDataMigrateMapper metaDataMigrateMapper;

    @Override
    public List<ProjectInfo> getAllProjectInfos() {
        return metaDataMigrateMapper.getAllProjectInfos();
    }

    @Override
    public List<TableInfo> getAllTableInfos() {
        return metaDataMigrateMapper.getAllTableInfos();
    }

    @Override
    @Transactional
    public int[] updateTableInfo(Map<String, TableInfo> tableInfoMap){
        int[] indexs = new int[tableInfoMap.size()];
        int i=0;
        for(String guid : tableInfoMap.keySet()){
            int indx = metaDataMigrateMapper.updateTableInfoGuid(guid,tableInfoMap.get(guid).getTableInfoId());
            indexs[i++]=indx;
        }

        return indexs;
    }

    @Override
    @Transactional
    public int[] updateColumnInfo(Map<String, ColumnInfo> columnInfoMap){
        int[] indexs = new int[columnInfoMap.size()];
        int i=0;
        for(String guid : columnInfoMap.keySet()){
            int indx = metaDataMigrateMapper.updateColumnInfoGuid(guid,columnInfoMap.get(guid).getColumnInfoId());
            indexs[i++]=indx;
        }
        return indexs;
    }
}
