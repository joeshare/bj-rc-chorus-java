package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.ProjectUtil;
import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.service.*;
import cn.rongcapital.chorus.governance.AtlasService;
import cn.rongcapital.chorus.governance.atlas.entity.HiveColumnAtlasEntityBuilder;
import cn.rongcapital.chorus.governance.atlas.entity.HiveTableAtlasEntityBuilder;
import cn.rongcapital.chorus.hive.jdbc.HiveClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.IntRange;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static cn.rongcapital.chorus.governance.atlas.entity.HiveTableAtlasEntityBuilder.INSTANCE;
import static cn.rongcapital.chorus.governance.bridge.ChorusMetaStoreBridge.TYPES_CHOR_HIVE_TABLE;

/**
 * Created by alan on 11/23/16.
 */
@Slf4j
@Service(value = "HiveTableInfoServiceV2")
public class HiveTableInfoServiceV2Impl implements HiveTableInfoServiceV2 {

    private HiveColumnAtlasEntityBuilder      COL_BUILDER = HiveColumnAtlasEntityBuilder.INSTANCE;
    private final HiveTableAtlasEntityBuilder TAB_BUILDER = INSTANCE;
    @Autowired
    private TableInfoServiceV2     tableInfoServiceV2;
    @Autowired
    private ProjectInfoService     projectInfoService;
    @Autowired
    private ColumnInfoServiceV2    columnInfoServiceV2;
    @Autowired
    private AtlasService           atlasService;
    @Autowired
    private AtlasEntityFactory     atlasEntityFactory;
    @Autowired
    private LoggerFactory          loggerFactory;

    public static final String CHORUS_PROJECT_DEFAULT_HIVE_WAREHOUSE_LOCATION_URL_TEMPLATE = "/apps/hive/warehouse/%s.db";
    public static final String DEFAULT_FILE_FORMAT = "TEXTFILE";
    public static final String DEFAULT_HIVE_UESR = "hive";
    public static final String ENTITY_TABLES = "tables";
    public static final String ENTITY_COLUMNS = "columns";

    private static final Set<String> COLUMN_TYPE_NEED_LENGTH = new TreeSet<>(Arrays.asList("VARCHAR", "CHAR", "DECIMAL"));

    @Override
    public List<Map<String, Object>> getSampleDataFromHive(String tableEntityGuid, Integer size) {
        TableInfoV2 tableInfo = tableInfoServiceV2.selectByID(tableEntityGuid);
        if (tableInfo == null) {
            throw new ServiceException(StatusCode.TABLE_NOT_EXISTS);
        }
        ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(tableInfo.getProjectId());
        if (projectInfo == null) {
            throw new ServiceException(StatusCode.PROJECT_NOT_EXISTS);
        }

        return HiveClient.getInstance().execute(projectInfo.getUserName(), projectInfo.getProjectCode(),
                connection -> {
                    PreparedStatement ps = null;
                    ResultSet rs = null;
                    try {
                        String sql = String.format("select * from %s limit %d",
                                assembleDbAndTablePath(projectInfo, tableInfo), size);
                        ps = connection.prepareStatement(sql);
                        rs = ps.executeQuery();
                        List<String> columnList = columnInfoServiceV2.selectColumnInfo(tableEntityGuid).stream()
                                .map(ColumnInfoV2::getColumnName)
                                .collect(Collectors.toList());
                        List<Map<String, Object>> innerRes = new ArrayList<>();
                        while (rs.next()) {
                            Map<String, Object> tempMap = new LinkedHashMap<>();
                            for (String s : columnList) {
                                tempMap.put(s, rs.getObject(s));
                            }
                            innerRes.add(tempMap);
                        }
                        return innerRes;
                    }finally {
                        if(rs != null){
                            try{rs.close();}catch (SQLException ex){}
                            rs = null;
                        }

                        if(ps != null){
                            try{ps.close();}catch (SQLException ex){}
                            ps = null;
                        }
                    }
                }, null);
    }
    
    @Transactional
    @Override
    public Boolean createTable(TableInfoV2 tableInfo, List<ColumnInfoV2> columnInfoList) {
        Logger auditLogger = loggerFactory.getTableAuditLogger();
        ProjectInfo projectInfo = getProject(tableInfo.getProjectId());
        Predicate<ColumnInfoV2> isPartitionKey = columnInfo ->
                                                         columnInfo.getIsPartitionKey() != null && columnInfo.getIsPartitionKey() == 1;
        Stream<ColumnInfoV2> partitionColumnInfoList = columnInfoList.stream().filter(isPartitionKey);
        Stream<ColumnInfoV2> normalColumnInfoList = columnInfoList.stream().filter(isPartitionKey.negate());
        String sql = " CREATE TABLE %s ( %s ) PARTITIONED BY (%s) " +
                     " STORED AS %s LOCATION '" + ProjectUtil.hiveTableLocation(projectInfo.getProjectCode(), tableInfo.getTableName()) + "' ";
        String partitionColumnSql = partitionColumnInfoList
                                            .map(c -> "`" + c.getColumnName() + "` " + combineTypeAndLength(c.getColumnType(), c.getColumnLength()) + " COMMENT '" + c.getColumnDesc() + "'")
                                            .collect(Collectors.joining(","));
        String normalColumnSql = normalColumnInfoList
                                         .map(c -> "`" + c.getColumnName() + "` " + combineTypeAndLength(c.getColumnType(), c.getColumnLength()) + " COMMENT '" + c.getColumnDesc() + "'")
                                         .collect(Collectors.joining(","));
        final String finalSql = String.format(sql,
                assembleDbAndTablePath(projectInfo, tableInfo),
                normalColumnSql, partitionColumnSql, DEFAULT_FILE_FORMAT);
        log.info("creating table in hive,\n sql: {}", finalSql);
        final Boolean execute = execute(finalSql, DEFAULT_HIVE_UESR, projectInfo.getProjectCode());
        if (execute) {
            auditLogger.info("hive table created success.\n {}", finalSql);
            ingestTableAndColumnsEntities(tableInfo, columnInfoList, projectInfo);
        }
        return execute;
    }
    
    private String combineTypeAndLength(String columnType, String columnLength) {
        if (COLUMN_TYPE_NEED_LENGTH.contains(columnType.toUpperCase())) {
            String columnTypeAndLength = "%s(%s)";
            return String.format(columnTypeAndLength, columnType, columnLength);
        } else {
            return columnType;
        }
        
    }
    
    private void ingestTableAndColumnsEntities(TableInfoV2 tableInfo, List<ColumnInfoV2> columnInfoList, ProjectInfo projectInfo) {
        try {
            final String hiveDbName = assembleDbName(projectInfo.getProjectCode());
            final List<AtlasEntity> columns = atlasEntityFactory.getColumns(projectInfo, columnInfoList, hiveDbName, tableInfo);
            //change ingest to ingestBatch
            final AtlasEntity[] columnsIngestResult = atlasService.ingestBatch(columns.toArray(new AtlasEntity[columns.size()]));

            IntStream.of(new IntRange(0, columnsIngestResult.length - 1).toArray()).forEach(i -> {
                AtlasEntity col = columnsIngestResult[i];
                if (col != null) {
                    log.info("entity of atlas created success: {}-{}", col.getTypeName(), col.getGuid());
                    loggerFactory.getTableAuditLogger().info("COL "
                                                             + "{} {} {} "
                                                             + "{} {} {} "
                                                             + "{} {} {} "
                                                             + "{} {} {} "
                                                             + "{} {}",
                                                             COL_BUILDER.guid(col), COL_BUILDER.unique(col), COL_BUILDER.name(col),
                                                             COL_BUILDER.type(col), COL_BUILDER.length(col), COL_BUILDER.comment(col),
                                                             COL_BUILDER.isPartitionKey(col), COL_BUILDER.isKey(col), COL_BUILDER.isForeignKey(col),
                                                             COL_BUILDER.isNull(col), COL_BUILDER.isIndex(col), COL_BUILDER.securityLevel(col),
                                                             COL_BUILDER.project(col), COL_BUILDER.projectId(col)
                    );
                } else {
                    //TODO how to fix this consistency problem, 2017-07-18 17:40:35
                    log.warn("column entity of atlas create fail");
                }
            });
            AtlasEntity tableEntity = atlasEntityFactory.tableEntity(
                    tableInfo,
                    Arrays.stream(columnsIngestResult).collect(Collectors.toList()),
                    projectInfo,
                    hiveDbName
            );
            final AtlasEntity[] tableIngestResult = atlasService.ingest(tableEntity);
            if (tableIngestResult.length == 1 && tableIngestResult[0] != null) {
                final AtlasEntity tab = tableIngestResult[0];
                loggerFactory.getTableAuditLogger().info("TAB "
                                                         + "{} {} {} "
                                                         + "{} {} {} "
                                                         + "{} {} {} "
                                                         + "{} {} {} "
                                                         + "{} {} {}",
                                                         TAB_BUILDER.guid(tab), TAB_BUILDER.unique(tab), TAB_BUILDER.name(tab),
                                                         TAB_BUILDER.comment(tab), TAB_BUILDER.businessField(tab), TAB_BUILDER.dataType(tab),
                                                         TAB_BUILDER.dataUpdateFrequency(tab), TAB_BUILDER.securityLevel(tab), TAB_BUILDER.sla(tab),
                                                         TAB_BUILDER.open(tab), TAB_BUILDER.snapshot(tab), TAB_BUILDER.project(tab),
                                                         TAB_BUILDER.projectId(tab), TAB_BUILDER.createUser(tab), TAB_BUILDER.createUser(tab)
                );
                feedTableReferenceToColumn(tab, columnsIngestResult);
                log.warn("table entity of atlas create success:  {}-{}", tab.getTypeName(), tab.getGuid());
            } else {
                log.warn("table entity of atlas create fail");
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        
    }
    
    private void feedTableReferenceToColumn(AtlasEntity tableEntity, AtlasEntity[] columnsIngestResult) throws AtlasServiceException {
        Arrays.stream(columnsIngestResult).forEach(column -> HiveColumnAtlasEntityBuilder.INSTANCE.table(column, tableEntity));
        atlasService.update(columnsIngestResult);
    }
    
    @Transactional
    @Override
    public Boolean createDb(ProjectInfo projectInfo, String userName) {
        String sql = " CREATE DATABASE %s ";
        final String dbName = assembleDbName(projectInfo.getProjectCode());
        final String finalSql = String.format(sql, dbName);
        log.info("creating db in hive,\n sql: {}", finalSql);
        final Boolean execute = execute(finalSql, DEFAULT_HIVE_UESR, projectInfo.getProjectCode());
        if (execute) {
            ingestDBEntity(projectInfo, userName, dbName);
        }
        return execute;
    }
    
    private void ingestDBEntity(ProjectInfo projectInfo, String userName, String dbName) {
        try {
            final AtlasEntity hiveDBEntity = atlasEntityFactory.getHiveDBEntity(projectInfo, userName, dbName);
            final AtlasEntity[] success = atlasService.ingest(hiveDBEntity);
            if (success[0] == null) {
                //TODO how to fix this consistency problem, 2017-07-17 11:42:56
                log.warn("hive database entity of atlas create fail: db name {}, create user {}", dbName, userName);
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }
    
    @Transactional
    Boolean execute(String finalSql, String userName, String queueName) {
        return HiveClient.getInstance().executeHiveSqlNoRes(userName, queueName, finalSql, null);
    }
    
    private String assembleDbAndTablePath(ProjectInfo projectInfo, TableInfoV2 tableInfo) {
        return String.format("%s.%s", assembleDbName(projectInfo), tableInfo.getTableName());
    }
    
    private String assembleDbName(ProjectInfo projectInfo) {
        return assembleDbName(projectInfo.getProjectCode());
    }
    
    private String assembleDbName(String projectCode) {
        return String.format("chorus_%s", projectCode);
    }
    
    @Override
    @Transactional
    public Boolean alterTable(TableInfoV2 tableInfo, List<ColumnInfoV2> addedColumnList) {
        ProjectInfo projectInfo = getProject(tableInfo.getProjectId());
        if (addedColumnList == null || addedColumnList.isEmpty()) {
            log.info("Alter table {} basic info.", tableInfo.getTableName());
            updateTableAndColumnEntites(tableInfo, addedColumnList, projectInfo);
            return true;
        }
        String normalColumnSqlTemplate = "ALTER table %s ADD COLUMNS (%s)";
        String columnSql = addedColumnList.stream()
                                   .map(c -> "`" + c.getColumnName() + "` " + combineTypeAndLength(c.getColumnType(), c.getColumnLength()) + " COMMENT '" + c.getColumnDesc() + "'")
                                   .collect(Collectors.joining(","));
        final String normalColumnsSql = String.format(normalColumnSqlTemplate,
                assembleDbAndTablePath(projectInfo, tableInfo),
                columnSql);
        log.info("Alter table in hive,\n sql: {}", normalColumnsSql);
        final Boolean execute = execute(normalColumnsSql, DEFAULT_HIVE_UESR, projectInfo.getProjectCode());
        if (execute) {
            updateTableAndColumnEntites(tableInfo, addedColumnList, projectInfo);
        }
        return execute;
    }
    
    private void updateTableAndColumnEntites(TableInfoV2 tableInfo, List<ColumnInfoV2> addedColumnList, ProjectInfo projectInfo) {
        final AtlasEntity tableEntity;
        try {
            final List<AtlasEntity> columns = atlasEntityFactory.getColumns(projectInfo, addedColumnList, projectInfo.getProjectCode(), tableInfo);
            final AtlasEntity[] columnsIngestResult = atlasService.ingest(columns.toArray(new AtlasEntity[columns.size()]));
            tableEntity = atlasService.getByGuid(tableInfo.getTableInfoId());
            assert tableEntity != null;
            final List<AtlasEntity> columnEntities = atlasService.selectingReferences(
                    TYPES_CHOR_HIVE_TABLE, "unique", "=", MapUtils.getString(tableEntity.getAttributes(), "unique"), "columns");
            columnEntities.addAll(Arrays.asList(columnsIngestResult));
            updateTableEntity(tableEntity, tableInfo, columnEntities);
            atlasService.update(tableEntity);
            feedTableReferenceToColumn(tableEntity, columnsIngestResult);
        } catch (AtlasServiceException e) {
            log.error("update atlas entity error.", e);
        }
        
    }
    
    private void updateTableEntity(AtlasEntity tableEntity, TableInfoV2 tableInfo, List<AtlasEntity> columnEntities) {
        TAB_BUILDER.columns(tableEntity, columnEntities)
                   .businessField(tableEntity, tableInfo.getDataField())
                   .dataType(tableEntity, tableInfo.getTableType())
                   .dataUpdateFrequency(tableEntity, tableInfo.getUpdateFrequence())
                   .securityLevel(tableEntity, tableInfo.getSecurityLevel())
                   .sla(tableEntity, tableInfo.getSla())
                   .snapshot(tableEntity, Boolean.valueOf((tableInfo.getIsSnapshot().equals("0")) ? "false" : "true"))
                   .open(tableEntity, tableInfo.getIsOpen() > 0)
                   .comment(tableEntity, tableInfo.getTableDes());
    }
    
    private ProjectInfo getProject(Long projectId) {
        ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(projectId);
        if (projectInfo == null) {
            throw new ServiceException(StatusCode.PROJECT_NOT_EXISTS);
        }
        return projectInfo;
    }
    
    @Override
    @Transactional
    public Boolean delete(String tableName) {
        String sql = " DROP TABLE IF EXISTS %s";
        final String finalSql = String.format(sql, tableName);
        log.info("delete table {} in hive, sql: {}", tableName, finalSql);
        return execute(finalSql, DEFAULT_HIVE_UESR, tableName);
    }
}
