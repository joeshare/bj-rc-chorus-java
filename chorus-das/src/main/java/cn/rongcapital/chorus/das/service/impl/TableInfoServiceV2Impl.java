package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.authorization.api.UserDataAuthorization;
import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.das.entity.AuthorizationDetail;
import cn.rongcapital.chorus.das.entity.AuthorizationDetailCategory;
import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.TableInfoDOV2;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.service.AuthorizationDetailService;
import cn.rongcapital.chorus.das.service.ColumnInfoServiceV2;
import cn.rongcapital.chorus.das.service.HiveTableInfoServiceV2;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import cn.rongcapital.chorus.governance.AtlasService;
import cn.rongcapital.chorus.governance.KeyValuePairBuilder;
import cn.rongcapital.chorus.governance.StringValuePairBuilder;
import cn.rongcapital.chorus.governance.atlas.entity.HiveTableAtlasEntityBuilder;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.discovery.AtlasSearchResult;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasEntityHeader;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.rongcapital.chorus.common.constant.StatusCode.PROJECT_NOT_EXISTS;
import static cn.rongcapital.chorus.das.service.impl.TableInfoServiceV2Impl.TABLE_CACHE_NAME;
import static cn.rongcapital.chorus.governance.Operation.AND;
import static cn.rongcapital.chorus.governance.Operation.OR;
import static cn.rongcapital.chorus.governance.bridge.ChorusMetaStoreBridge.TYPES_CHOR_HIVE_TABLE;

/**
 * 数据管理-表基本信息模块SERVICE实现类
 *
 * @author fuxiangli
 */
@Slf4j
@Service(value = "TableInfoServiceV2")
@CacheConfig(cacheNames = {TABLE_CACHE_NAME})
public class TableInfoServiceV2Impl implements TableInfoServiceV2 {
    public static final String TABLE_CACHE_NAME = "cache_atlas_type_" + TYPES_CHOR_HIVE_TABLE;
    private static final String CACHE_KEY_PREFIX = "c.r.c.d.s.i.TableInfoServiceV2Impl";
    public static final long EXPIRE_TIME = TimeUnit.DAYS.toSeconds(1);
    private static final Joiner OR_OP_JOINER = Joiner.on(" or ");
    
    private final HiveTableAtlasEntityBuilder builder = HiveTableAtlasEntityBuilder.INSTANCE;
    
    @Autowired
    private HiveTableInfoServiceV2 hiveTableInfoServiceV2;
    @Autowired
    private ColumnInfoServiceV2 columnInfoServiceV2;
    @Autowired
    private AtlasService atlasService;
    @Autowired
    private ProjectInfoService projectInfoService;

    @Autowired
    @Qualifier("userDataAuthorizationByRanger")
    private UserDataAuthorization hiveRangerAuth;

    @Autowired
    private AuthorizationDetailService authDetailService;
    
    /**
     * 1.根据表ID，查询表信息
     *
     * @param tableEntityGuid
     * @return
     */
    @Override
    @Cacheable(unless = "#result==null")
    public TableInfoV2 selectByID(String tableEntityGuid) {
        try {
            AtlasEntity tableEntity = atlasService.getByGuid(tableEntityGuid);
            return tableEntityToTableInfo(tableEntity);
        } catch (AtlasServiceException e) {
            log.error("Cannot found entity by guid " + tableEntityGuid, e);
        }
        return null;
    }
    
    @Override
    public Map<String, TableInfoV2> selectByIds(List<String> tableEntitiesGuid) {
        try {
            List<AtlasEntity> tableEntities = atlasService.getByGuids(tableEntitiesGuid);
            Map<String, TableInfoV2> map = new HashMap<>();
            if (CollectionUtils.isNotEmpty(tableEntities)) {
                tableEntities.forEach(tableEntity -> {
                    TableInfoV2 tableInfoV2 = tableEntityToTableInfo(tableEntity);
                    map.put(tableEntity.getGuid(), tableInfoV2);
                });
                return map;
            }
        } catch (AtlasServiceException e) {
            log.error("Cannot found entity by guids " + JSON.toJSONString(tableEntitiesGuid), e);
        }
        return null;
    }
    
    private TableInfoV2 tableEntityToTableInfo(@Nonnull AtlasEntity tableEntity) {
        return TableInfoV2.builder()
                       .tableInfoId(builder.guid(tableEntity))
                       .projectId(builder.projectId(tableEntity))
                       .projectCode(builder.project(tableEntity))
                       .tableName(builder.name(tableEntity))
                       .dataField(builder.businessField(tableEntity))
                       .tableType(builder.dataType(tableEntity))
                       .isSnapshot(builder.snapshot(tableEntity) ? "1" : "0")
                       .updateFrequence(builder.dataUpdateFrequency(tableEntity))
                       .sla(builder.sla(tableEntity))
                       .securityLevel(builder.securityLevel(tableEntity))
                       .isOpen(builder.booleanToByte(builder.open(tableEntity)))
                       .tableDes(builder.comment(tableEntity))
                       .createTime(builder.createTime(tableEntity))
                       .updateTime(builder.lastAccessTime(tableEntity))
                       .statusCode(builder.statusCode(tableEntity))
                       .columns(builder.columns(tableEntity))
                       .createUser(builder.createUser(tableEntity))
                       .createUserId(builder.createUserId(tableEntity))
                       .build();
    }
    
    private TableInfoDOV2 tableEntityToTableInfoDO(@Nonnull AtlasEntity tableEntity) {
        return TableInfoDOV2.builder()
                       .tableInfoId(builder.guid(tableEntity))
                       .projectId(builder.projectId(tableEntity))
                       .tableName(builder.name(tableEntity))
                       .projectCode(builder.project(tableEntity))
                       .projectName(builder.projectName(tableEntity))
                       .securityLevel(builder.securityLevel(tableEntity))
                       .tableDes(builder.comment(tableEntity))
                       .build();
    }
    
    /**
     * 2.根据项目ID，查询表信息
     * <p>
     * //TODO pageable not support
     *
     * @param projectId
     * @return
     */
    @Override
    @Cacheable(unless = "#result.size()==0")
    public List<TableInfoV2> listAllTableInfo(Long projectId, int pageNum, int pageSize) {
        Map<String, Object> params = generateActiveParams("projectId",projectId);
        final List<AtlasEntity> tableEntities = atlasService.dslSearch(TYPES_CHOR_HIVE_TABLE, params, pageNum, pageSize);
        return tableEntities.parallelStream().map(this::tableEntityToTableInfo).collect(Collectors.toList());
    }
    
    
    @Transactional
    @Override
    @CacheEvict(allEntries = true)
    public void createTable(TableInfoV2 tableInfo, List<ColumnInfoV2> columnInfoList) {
        hiveTableInfoServiceV2.createTable(tableInfo, columnInfoList);
    }
    
    //TODO pageable not support
    @Override
    @Cacheable(unless = "#result.size()==0")
    public List<TableInfoDOV2> listAllTables(int pageNum, int pageSize) {
        final Collection<AtlasEntity> allTableEntities;
        try {
            allTableEntities = atlasService.dslBaseSearch(TYPES_CHOR_HIVE_TABLE, "statusCode", "=", StatusCode.COLUMN_CREATED.getCode(), pageNum, pageSize);
            return allTableEntities.parallelStream().map(this::tableEntityToTableInfoDO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return ImmutableList.of();
    }
    
    /**
     * 内部表清单(job输入输出接口用)
     */
    @Override
    @Cacheable(key = "'TableInfoServiceV2ImpllistAllTableInfo'.concat(#projectId)", condition = "#projectId!=null",unless = "#result.size()==0")
    public List<TableInfoDOV2> listAllTableInfo(Long projectId) {
//        final List<AtlasEntity> tableEntities = atlasService.dslBaseSearch(TYPES_CHOR_HIVE_TABLE, "projectId", "=", projectId);
        Map<String, Object> params = generateActiveParams("projectId", projectId);
        final List<AtlasEntity> tableEntities = atlasService.dslSearch(TYPES_CHOR_HIVE_TABLE, params, 0,
                Integer.MAX_VALUE);
        if (CollectionUtils.isEmpty(tableEntities))
            return ImmutableList.of();
        return tableEntities.parallelStream().map(this::tableEntityToTableInfoDO).collect(Collectors.toList());
    }
    
    @Override
    @Cacheable(cacheNames = TABLE_CACHE_NAME, key = "'TableInfoServiceV2ImpllistAllTableName'.concat(#projectId)", condition = "#projectId!=null",unless = "#result.size()==0")
    public List<String> listAllTableName(Long projectId) {
        Map<String, Object> params = generateActiveParams("projectId", projectId);
        final List<AtlasEntity> tableEntities = atlasService.dslSearch(TYPES_CHOR_HIVE_TABLE, params, 0,
                Integer.MAX_VALUE);
        if (CollectionUtils.isEmpty(tableEntities)) return ImmutableList.of();
        return tableEntities.parallelStream().map(entity -> MapUtils.getString(entity.getAttributes(), "name")).collect(Collectors.toList());
    }
    
    @Override
    @Cacheable(key = "'TableInfoServiceV2ImplcountTables'.concat(#projectId)", condition = "#projectId!=null")
    public long countTables(Long projectId) {
        Map<String, Object> params = generateActiveParams("projectId", projectId);
        final List<AtlasEntity> tables = atlasService.dslSearch(TYPES_CHOR_HIVE_TABLE, params, 0,Integer.MAX_VALUE);
        return tables.size();
    }
    
    //TODO pageable not support
    @Override
    @Cacheable(unless = "#result.size()==0")
    public List<TableInfoDOV2> searchByTableNameAndProjectNameAndProjectCode(String matchText, int pageNum, int pageSize) throws AtlasServiceException {
        //TODO 模糊查询方式暂时未支持，使用 '=' 匹配查询结果
        final KeyValuePairBuilder first = new StringValuePairBuilder("statusCode").value(StatusCode.COLUMN_CREATED.getCode()).op(
                AND, new StringValuePairBuilder("name").value(matchText)
                                                       .op(OR, new StringValuePairBuilder("project").value(matchText))
                                                       .op(OR, new StringValuePairBuilder("projectName").value(matchText))
        );

        final AtlasSearchResult searchResult = atlasService.dslSearch(first.where(TYPES_CHOR_HIVE_TABLE), pageSize, (pageNum - 1) * pageSize);
        return atlasService.fetchDataByHeader(searchResult).stream().map(this::tableEntityToTableInfoDO).collect(Collectors.toList());
    }
    
    @Nullable
    @Override
    public Map<TableInfoV2, List<ColumnInfoV2>> tableWithColumnsOfProject(@Nonnull List<Long> projectIds) {
        if (CollectionUtils.isEmpty(projectIds)) return Maps.newHashMap();
        try {
            Map<String, TableInfoV2> tables = tablesOfProject(projectIds);
            if (MapUtils.isEmpty(tables)) return Maps.newHashMap();
            
            Map<String, List<ColumnInfoV2>> columns = columnInfoServiceV2.columnsOfTables(tables.keySet());
            
            final Map<TableInfoV2, List<ColumnInfoV2>> result = new HashMap<>(tables.size());
            
            tables.forEach((tableUnique, tab) -> {
                List<ColumnInfoV2> cols = columns.get(tableUnique);
                if (cols == null) {
                    log.warn("table {} not has any column", tableUnique);
                    cols = ImmutableList.of();
                }
                result.put(tab, cols);
            });
            
            return result;
        } catch (AtlasServiceException e) {
            log.error("Atlas Server Error " + e.getMessage(), e);
        }
        return Maps.newHashMap();
    }
    
    
    private String in(String attributeName, List<Long> projectIds) {
        return OR_OP_JOINER.join(projectIds.stream().map(id -> attributeName + "=" + id).collect(Collectors.toList()));
    }
    
    @Nullable
    @Override
    @Cacheable(key = "'" + CACHE_KEY_PREFIX + ".tablesOfProject'.concat(#projectIds.toString())", condition = "#projectIds.size()>0", unless = "#result.size()==0")
    public Map<String, TableInfoV2> tablesOfProject(@Nonnull List<Long> projectIds) {
        Map<String, TableInfoV2> tables = Maps.newHashMap();
        try {
            String projectCondition = in("projectId", projectIds);
            String whereCondition = String.format("( %s ) and statusCode='%s'",projectCondition,StatusCode.COLUMN_CREATED.getCode());
            
            String tableQueryStr = TYPES_CHOR_HIVE_TABLE + " where " + whereCondition;
            if (log.isDebugEnabled()) {
                log.debug("query string {} for tables of projects {}", tableQueryStr, projectIds);
            }
            int offset = 0;
            int limit = 1000;
            List<AtlasEntityHeader> allTableHeaders = new ArrayList<>();
            while (true) {
                List<AtlasEntityHeader> tableEntityHeaders = atlasService.dslSearch(tableQueryStr, limit, offset).getEntities();
                if (CollectionUtils.isNotEmpty(tableEntityHeaders)) allTableHeaders.addAll(tableEntityHeaders);
                if (CollectionUtils.isEmpty(tableEntityHeaders) || tableEntityHeaders.size() < limit) break;
                offset += limit;
            }
            
            allTableHeaders.forEach(header -> {
                String tableUnique = MapUtils.getString(header.getAttributes(), "unique");// format : chorus_<project_code>.<table_name> , <hive_db_name>.<table_name> also match
                String tableName = MapUtils.getString(header.getAttributes(), "name");
                String tableId = header.getGuid();
                if (log.isDebugEnabled()) {
                    log.debug("tableUnique is {}, tableName is {}, tableId is {}", tableUnique, tableName, tableId);
                }
                String projectCode = tableUnique.substring(tableUnique.indexOf("chorus_") + 7, StringUtils.lastIndexOf(tableUnique, tableName) - 1);
                if (log.isDebugEnabled()) {
                    log.debug("projectCode is {}", projectCode);
                }
                tables.put(tableUnique, TableInfoV2.builder().tableName(tableName)
                                                .tableInfoId(tableId)
                                                .projectCode(projectCode).build());
            });
        } catch (AtlasServiceException e) {
            log.error("Atlas Server Error " + e.getMessage(), e);
        }
        return tables;
    }
    
    @Override
    public TableInfoV2 getAtlasByUnique(String tableName, Long projectId) throws AtlasServiceException, ServiceException {
        ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(projectId);
        if (projectInfo == null) {
            throw new ServiceException(PROJECT_NOT_EXISTS);
        }
        
        String attrValue = String.format("chorus_%s.%s", projectInfo.getProjectCode(), tableName);
        try {
            AtlasEntity foundEntity = atlasService.getEntityByUniqueAttribute(TYPES_CHOR_HIVE_TABLE, "unique", attrValue);
            if(foundEntity != null)
                return tableEntityToTableInfo(foundEntity);
        } catch (AtlasServiceException ex) {
            /***
             * if this record is not in atlas,then will throw AtlasServiceException, and message contain does not exist.
             * eg:Caught exception in create:
             org.apache.atlas.AtlasServiceException: Metadata service API org.apache.atlas.AtlasBaseClient$APIInfo@3305974d failed with status 404 (Not Found) Response Body
             ({"errorCode":"ATLAS-404-00-009","errorMessage":"Instance chor_hive_table with unique attribute {unique=chorus_System03.t_hhl_test_0033} does not exist"}
             *
             */
            if (ex.getMessage().contains("does not exist"))
                return null;
            
            throw ex;
        }

        return null;
    }
    
    @Override
    @CacheEvict(cacheNames = {TABLE_CACHE_NAME}, allEntries = true)
    public void alterTable(TableInfoV2 tableInfo, List<ColumnInfoV2> addedColumnList) {
        hiveTableInfoServiceV2.alterTable(tableInfo, addedColumnList);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {TABLE_CACHE_NAME}, allEntries = true)
    public void delete(String tableInfoId) throws Exception {
        final AtlasEntity entity = atlasService.getByGuid(tableInfoId);
        if (entity == null) {
            log.error("There is no table {}", tableInfoId);
            throw new ServiceException(StatusCode.TABLE_NOT_EXISTS);
        }
        final TableInfoV2 table = tableEntityToTableInfo(entity);
        final String unique = builder.unique(entity);
        //hive 删除
        if (StringUtils.isEmpty(unique)) {
            log.warn("There is no unique info of {} table", tableInfoId);
        } else {
            hiveTableInfoServiceV2.delete(unique);
        }
        //表删除,delete操作会删除column
        builder.statusCode(entity, StatusCode.TABLE_DELETED.getCode());
        atlasService.deleteWithUpdate(entity);
        //权限失效
        cleanRanger(table);
    }

    /**
     * @param table
     * @throws Exception
     * @author yunzhong
     * @time 2017年9月7日下午1:57:28
     */
    private void cleanRanger(TableInfoV2 table) throws Exception {
        final List<AuthorizationDetail> authDetails = authDetailService.selectByProjectId(table.getProjectId());
        if (CollectionUtils.isEmpty(authDetails)) {
            log.info("There are no auth details of project {}", table.getProjectId());
            return;
        }
        String policyNamePre = "chorus_" + table.getProjectCode() + "_" + table.getTableName();
        authDetails.forEach(authDetail -> {
            if (AuthorizationDetailCategory.HIVE.name().equals(authDetail.getCategory())) {
                int lastIndex = authDetail.getPolicyName().lastIndexOf("_");
                String nameEncodePre = authDetail.getPolicyName().substring(0, lastIndex);
                if (policyNamePre.equals(nameEncodePre)) {
                    try {
                        authDetail.setEnabled(0);
                        authDetailService.updateByPrimaryKey(authDetail);
                        if (!hiveRangerAuth.disablePolicy(authDetail.getPolicyId())) {
                            log.warn("Failed to disalbe policy {} {}", authDetail.getPolicyId(),
                                    authDetail.getPolicyName());
                        }
                    } catch (Exception e) {
                        log.error("Failed to disable policy {} {}", authDetail.getPolicyId(),
                                authDetail.getPolicyName());
                        log.error(e.getLocalizedMessage(), e);
                    }
                }
            }
        });
    }

    private Map<String, Object> generateActiveParams(String key, Object value){
        Map<String, Object> params = new HashMap<>();
        params.put(key, value);
        params.put("statusCode", StatusCode.COLUMN_CREATED.getCode());
        return params;
    }
}
