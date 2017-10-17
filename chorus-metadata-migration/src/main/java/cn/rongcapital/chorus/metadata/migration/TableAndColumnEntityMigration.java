package cn.rongcapital.chorus.metadata.migration;

import cn.rongcapital.chorus.common.util.OrikaBeanMapper;
import cn.rongcapital.chorus.das.entity.ColumnInfo;
import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.TableInfo;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.service.ColumnInfoService;
import cn.rongcapital.chorus.das.service.MetaDataMigrateService;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.das.service.impl.AtlasEntityFactory;
import cn.rongcapital.chorus.governance.AtlasService;
import cn.rongcapital.chorus.governance.atlas.entity.HiveColumnAtlasEntityBuilder;
import cn.rongcapital.chorus.metadata.migration.bean.ColumnInfoComposite;
import cn.rongcapital.chorus.metadata.migration.bean.TableInfoComposite;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.instance.AtlasEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by hhlfl on 2017-7-24.
 */
@Slf4j
@Component
public class TableAndColumnEntityMigration extends AbstractDB2AtlasMigration<List<TableInfoComposite>> {
    @Autowired
    private ColumnInfoService columnInfoService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private MetaDataMigrateService metaDataMigrateService;

    @Autowired
    public TableAndColumnEntityMigration(AtlasService atlasService, AtlasEntityFactory atlasEntityFactory) {
        super(atlasService, atlasEntityFactory);
    }

    @Override
    protected List<TableInfoComposite> getSourceEntities() throws Exception {
        List<TableInfo> tableInfos = metaDataMigrateService.getAllTableInfos();
        List<TableInfoComposite> tableInfoComposites = new ArrayList<>();
        Map<Long, ProjectInfo> projectInfoMap = new HashMap<>();
        for(TableInfo tableInfo : tableInfos){
            long projectId = tableInfo.getProjectId();
            ProjectInfo projectInfo = projectInfoMap.get(projectId);
            if(projectInfo == null){
                projectInfo = projectInfoService.selectByPrimaryKey(projectId);
                if(projectInfo == null ) {
                    log.info("db is not exist for table:{}",tableInfo.getTableName());
                    continue;
                }
                projectInfoMap.put(projectId,projectInfo);
            }
            List<ColumnInfo> columnInfos = columnInfoService.selectColumnInfo(tableInfo.getTableInfoId());
            List<ColumnInfoComposite> columnInfoComposites = columnInfos.stream().map(col->{
                ColumnInfoComposite colComposite = new ColumnInfoComposite();
                colComposite.setColumnInfo(col);
                return colComposite;
            }).collect(Collectors.toList());

            TableInfoComposite composite = new TableInfoComposite();
            composite.setTableInfo(tableInfo);
            composite.setColumnInfoList(columnInfoComposites);
            composite.setProjectInfo(projectInfo);
            tableInfoComposites.add(composite);
        }

        return tableInfoComposites;
    }

    @Override
    public void migrate()throws Exception{
        List<TableInfoComposite> sourceEntities = getSourceEntities();
        getAtlasEntity(sourceEntities);//inner ingest

    }

    @Override
    protected List<AtlasEntity> getAtlasEntity(List<TableInfoComposite> sourceEntities) throws Exception {
        if(sourceEntities == null) return Collections.EMPTY_LIST;
        for(TableInfoComposite tableInfo : sourceEntities) {

            ProjectInfo projectInfo = tableInfo.getProjectInfo();
            TableInfoV2 tableInfoV2 = OrikaBeanMapper.INSTANCE.map(tableInfo.getTableInfo(), TableInfoV2.class);
            List<ColumnInfo> columnInfos = tableInfo.getColumnInfoList().stream().map(ColumnInfoComposite::getColumnInfo).collect(Collectors.toList());
            List<ColumnInfoV2> columnInfoList = OrikaBeanMapper.INSTANCE.mapAsList(columnInfos,ColumnInfoV2.class);
            for (int i = 0; i < columnInfoList.size(); i++) {
                columnInfoList.get(i).setColumnOrder(i);
            }
            String hiveDBName = assembleDbName(projectInfo.getProjectCode());
            List<AtlasEntity> columnEntities = atlasEntityFactory.getColumns(projectInfo, columnInfoList, hiveDBName, tableInfoV2);

            final Map<String, ColumnInfoComposite> columnInfoCompositeMap = combination(tableInfo.getColumnInfoList(), columnEntities);
            Map<Integer, List<AtlasEntity>> group = groupByExists(columnEntities);
            List<AtlasEntity> existsColumnEntities = group.get(EXISTS);
            columnEntities = group.get(NON_EXISTS);
            AtlasEntity[] columnIngestResult = atlasService.ingestBatch(columnEntities.toArray(new AtlasEntity[columnEntities.size()]));
            existsColumnEntities.addAll(Arrays.asList(columnIngestResult));
            final AtlasEntity[] updateColumnEntities = existsColumnEntities.toArray(new AtlasEntity[existsColumnEntities.size()]);
            updateColumnInfo(feedBackGuid(columnInfoCompositeMap,updateColumnEntities));

            AtlasEntity tableEntity = atlasEntityFactory.tableEntity(tableInfoV2, Arrays.stream(updateColumnEntities).collect(Collectors.toList()), projectInfo, hiveDBName);
            final AtlasEntity[] tableIngestResult = atlasService.ingest(tableEntity);
            if(tableEntity != null && tableIngestResult.length>0) {
                //update table attribute for column type
                updateColumnEntities(tableIngestResult[0], updateColumnEntities);

                tableInfo.setAtlasGuid(tableIngestResult[0].getGuid());
                Map<String, TableInfo> tableInfoMap = new HashMap<>();
                tableInfoMap.put(tableInfo.getAtlasGuid(), tableInfo.getTableInfo());
                updateTableInfo(tableInfoMap);
            }
        }

        return ImmutableList.of();

    }

    private void updateColumnEntities(AtlasEntity tableEntity,  AtlasEntity... columnEntities) throws AtlasServiceException{
        if(tableEntity == null || columnEntities == null)
            return;

        List<AtlasEntity> colEntities = Arrays.stream(columnEntities).map(entity -> {
            HiveColumnAtlasEntityBuilder.INSTANCE.table(entity,tableEntity);
            return entity;
        }).collect(Collectors.toList());

        atlasService.update(colEntities.toArray(new AtlasEntity[colEntities.size()]));
    }

    private Map<String, ColumnInfo> feedBackGuid(Map<String,ColumnInfoComposite> columnCompositeMap, AtlasEntity[] columnIngestResult) {
            final Map<String, ColumnInfo> columnInfoMap = new HashMap<>();
            Arrays.stream(columnIngestResult).filter(Objects::nonNull).forEach(entity ->{
                String v = entity.getAttribute(UNIQUE).toString();
                if(columnCompositeMap.containsKey(v)){
                    ColumnInfoComposite composite = columnCompositeMap.get(v);
                    composite.setAtlasGuid(entity.getGuid());
                    columnInfoMap.put(entity.getGuid(), composite.getColumnInfo());
                }
            });

            return columnInfoMap;
    }

    private Map<String, ColumnInfoComposite> combination(List<ColumnInfoComposite> colComposites, List<AtlasEntity> entities) throws Exception{
        if(colComposites.size() != entities.size())
            throw new IllegalArgumentException(" entities size must equals colComposites size.");

        Map<String, ColumnInfoComposite> columnInfoCompositeMap = new HashMap<>();
        for(int i=0; i<colComposites.size(); i++){
            columnInfoCompositeMap.put(entities.get(i).getAttribute(UNIQUE).toString(), colComposites.get(i));
        }

        return columnInfoCompositeMap;
    }

    private void updateColumnInfo(Map<String, ColumnInfo> columnInfoMap){
        try {
            metaDataMigrateService.updateColumnInfo(columnInfoMap);
            log.info("update atlas guid to table column_info success.");
        }catch(Exception ex){
            log.error("update atals guid to table column_info failed.");
            throw ex;
        }
    }

    private void updateTableInfo(Map<String,TableInfo> tableInfoMap){
        try{
            metaDataMigrateService.updateTableInfo(tableInfoMap);
            log.info("update atlas guid to table table_info success.");
        }catch(Exception ex){
            log.error("update atlas guid to table table_info failed.");
            throw ex;
        }
    }

    @Override
    protected void update(List<TableInfoComposite> sourceEntities, Map<String,AtlasEntity> successEntities) throws Exception {
        ;
    }

}
