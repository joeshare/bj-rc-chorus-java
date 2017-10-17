package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.common.util.ProjectUtil;
import cn.rongcapital.chorus.governance.AtlasService;
import cn.rongcapital.chorus.governance.atlas.entity.HiveColumnAtlasEntityBuilder;
import cn.rongcapital.chorus.governance.atlas.entity.HiveDBEntityBuilder;
import cn.rongcapital.chorus.governance.atlas.entity.HiveTableAtlasEntityBuilder;
import cn.rongcapital.chorus.governance.bridge.ChorusMetaStoreBridge;
import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static cn.rongcapital.chorus.das.service.impl.HiveTableInfoServiceV2Impl.CHORUS_PROJECT_DEFAULT_HIVE_WAREHOUSE_LOCATION_URL_TEMPLATE;
import static cn.rongcapital.chorus.das.service.impl.HiveTableInfoServiceV2Impl.DEFAULT_HIVE_UESR;

/**
 * @author yimin
 */
@Slf4j
@Component
public class AtlasEntityFactory {
    
    private AtlasService atlasService;
    private static final String DEFAULT_CLUSTE_NAME = "default";
    private static final Random random = new Random();
    
    @Autowired
    public AtlasEntityFactory(AtlasService atlasService) {
        this.atlasService = atlasService;
    }
    
    public AtlasEntity getHiveDBEntity(ProjectInfo projectInfo, String userName, String dbName) {
        final AtlasEntity entity = HiveDBEntityBuilder.INSTANCE.getEntity();
        
        HiveDBEntityBuilder.INSTANCE.name(entity, dbName)
                .descritpion(entity, projectInfo.getProjectDesc())
                .locationUrl(entity, String.format(CHORUS_PROJECT_DEFAULT_HIVE_WAREHOUSE_LOCATION_URL_TEMPLATE, dbName))
                .owner(entity, DEFAULT_HIVE_UESR)
                .qualifiedName(entity, getQualifiedName())
                .unique(entity, dbName);

//        if (hiveDB.getOwnerType() != null) HiveDBEntityBuilder.INSTANCE.ownerType(entity, hiveDB.getOwnerType().getValue());
        
        HiveDBEntityBuilder.INSTANCE.createUser(entity, userName == null ? "" : userName)
                .createUserId(entity, projectInfo.getCreateUserId())
                .projectId(entity, projectInfo.getProjectId())
                .project(entity, projectInfo.getProjectCode())
                .projectName(entity, projectInfo.getProjectName())
                .clusterName(entity, DEFAULT_CLUSTE_NAME);
        
        return HiveDBEntityBuilder.INSTANCE.build(entity);
    }
    
    public AtlasEntity tableEntity(TableInfoV2 tableInfo, List<AtlasEntity> columnEntities, ProjectInfo projectInfo, String hiveDBName)
            throws AtlasServiceException {
        final HiveTableAtlasEntityBuilder builder = HiveTableAtlasEntityBuilder.INSTANCE;
//        final Table hiveTable = getHiveTableByName(tableInfo.getTableName());
        AtlasEntity dbEntity = atlasService.getEntityByUniqueAttribute(ChorusMetaStoreBridge.TYPES_CHOR_HIVE_DB, "unique", hiveDBName);
        AtlasEntity tableEntity = builder.getEntity();
        builder.name(tableEntity, tableInfo.getTableName());
        builder.owner(tableEntity, DEFAULT_HIVE_UESR);
//        if (hiveTable.getLastAccessTime() > 0) builder.lastAccessTime(tableEntity, new Date(TimeUnit.SECONDS.toMillis(hiveTable.getLastAccessTime())));
//        builder.retention(tableEntity, hiveTable.getRetention());
//        final Map<String, String> parameters = hiveTable.getParameters();
        
        String comment = "";// parameters.get("comment");
        if (StringUtils.isBlank(comment)) comment = tableInfo.getTableDes();
        builder.comment(tableEntity, comment);
        
        builder.db(tableEntity, dbEntity);
        //builder.sd(tableEntity, )
        builder.locationUrl(tableEntity, ProjectUtil.hiveTableLocation(projectInfo.getProjectCode(), tableInfo.getTableName()));
//        builder.parameters(tableEntity, parameters);

//        if (hiveTable.getViewOriginalText() != null) builder.viewOriginalText(tableEntity, hiveTable.getViewOriginalText());
//        if (hiveTable.getViewExpandedText() != null) builder.viewExpandedText(tableEntity, hiveTable.getViewExpandedText());

//        builder.tableType(tableEntity, hiveTable.getTableType().name());
        builder.tableType(tableEntity, tableInfo.getTableType());
        builder.statusCode(tableEntity, StatusCode.COLUMN_CREATED.getCode());
//        builder.temporary(tableEntity, hiveTable.isTemporary());
        
        final List<AtlasEntity> partKeys = columnEntities.parallelStream().filter(col -> col.getAttribute("isPartitionKey") == null ? false : (boolean) col.getAttribute("isPartitionKey")).collect(Collectors.toList());
        builder.partitionKeys(tableEntity, partKeys)
                .columns(tableEntity, columnEntities)
                .createUser(tableEntity, projectInfo.getUserName() == null ? "" : projectInfo.getUserName())
                .createUserId(tableEntity, projectInfo.getCreateUserId())
                .project(tableEntity, projectInfo.getProjectCode())
                .projectId(tableEntity, projectInfo.getProjectId())
                .projectName(tableEntity, projectInfo.getProjectName())
                .businessField(tableEntity, tableInfo.getDataField())
                .dataType(tableEntity, tableInfo.getTableType())
                .dataUpdateFrequency(tableEntity, tableInfo.getUpdateFrequence())
                .securityLevel(tableEntity, tableInfo.getSecurityLevel())
                .qualifiedName(tableEntity, getQualifiedName())
                .unique(tableEntity, hiveDBName + "." + tableInfo.getTableName());
        
        builder.snapshot(tableEntity, Boolean.valueOf((tableInfo.getIsSnapshot().equals("0")) ? "false" : "true"))
                .sla(tableEntity, tableInfo.getSla())
                .open(tableEntity, tableInfo.getIsOpen() > 0);
        
        return builder.build(tableEntity);
    }
    
    public List<AtlasEntity> getColumns(ProjectInfo projectInfo, List<ColumnInfoV2> columns, String dbName, TableInfoV2 tableInfoV2) {
        final String tableName = tableInfoV2.getTableName();
        List<AtlasEntity> colList = new ArrayList<>();
        int columnPosition = 0;
        for (ColumnInfoV2 col : columns) {
            log.debug("Processing field {}", col);
            final HiveColumnAtlasEntityBuilder builder = HiveColumnAtlasEntityBuilder.INSTANCE;
            AtlasEntity colEntity = builder.getEntity();
            builder.name(colEntity, col.getColumnName());
            builder.type(colEntity, col.getColumnType());
            builder.comment(colEntity, col.getColumnDesc() == null ? "" : col.getColumnDesc());
//            builder.table(colEntity, tableEntity);
//            builder.owner(colEntity, String.valueOf(tableEntity.getAttribute(AtlasClient.OWNER)));
            builder.position(colEntity, columnPosition++);
            builder.columnOrder(colEntity, col.getColumnOrder());
            builder.projectName(colEntity, projectInfo.getProjectName());
            builder.project(colEntity, projectInfo.getProjectCode()).projectId(colEntity, projectInfo.getProjectId())
                    .length(colEntity, col.getColumnLength() == null ? "" : col.getColumnLength())
                    .precision(colEntity, col.getColumnPrecision() == null ? "" : col.getColumnPrecision())
                    .isKey(colEntity, col.getIsKey() > 0).isForeignKey(colEntity, col.getIsRefKey() > 0)
                    .isNull(colEntity, col.getIsNull() > 0).isIndex(colEntity, col.getIsIndex() > 0)
                    .isPartitionKey(colEntity, (col.getIsPartitionKey() != null && col.getIsPartitionKey() > 0))
                    .securityLevel(colEntity, col.getSecurityLevel() == null ? "" : col.getSecurityLevel())
                    .statusCode(colEntity, StatusCode.COLUMN_CREATED.getCode())
                    .unique(colEntity, dbName + "." + tableName + "." + col.getColumnName())
                    .qualifiedName(colEntity, getQualifiedName());
            //by li.hzh 添加列时，需要更新信息
            if (col.getCreateTime() != null) {
                builder.createTime(colEntity, col.getCreateTime().getTime());
            }
            if (col.getUpdateTime() != null) {
                builder.updateTime(colEntity, col.getUpdateTime().getTime());
            }
            builder.statusCode(colEntity, col.getStatusCode());
            if (col.getColumnInfoId() != null) {
                builder.guid(colEntity, col.getColumnInfoId());
            }
            builder.build(colEntity);
            colList.add(colEntity);
        }
        return colList;
    }
    
    private long getQualifiedName() {
        int bound = 100000;
        int v = random.nextInt(bound);
        return System.currentTimeMillis() * bound + v;
    }
}
