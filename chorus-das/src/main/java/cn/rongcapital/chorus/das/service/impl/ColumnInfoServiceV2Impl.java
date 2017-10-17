package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.service.ColumnInfoServiceV2;
import cn.rongcapital.chorus.governance.AtlasService;
import cn.rongcapital.chorus.governance.atlas.entity.HiveColumnAtlasEntityBuilder;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasEntityHeader;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.rongcapital.chorus.das.service.impl.ColumnInfoServiceV2Impl.COLUMN_CACHE_NAME;
import static cn.rongcapital.chorus.governance.bridge.ChorusMetaStoreBridge.TYPES_CHOR_HIVE_COLUMN;
import static cn.rongcapital.chorus.governance.bridge.ChorusMetaStoreBridge.TYPES_CHOR_HIVE_TABLE;

/**
 * 数据管理-列基本信息模块SERVICE实现类
 *
 * @author fuxiangli
 */
@Slf4j
@CacheConfig(cacheNames = {COLUMN_CACHE_NAME})
@Service(value = "ColumnInfoServiceV2")
public class ColumnInfoServiceV2Impl implements ColumnInfoServiceV2 {
    public static final  String                       COLUMN_CACHE_NAME = "cache_atlas_type_" + TYPES_CHOR_HIVE_COLUMN;
    public static final  String                       CACHE_KEY_PREFIX  = "c.r.c.d.s.i.ColumnInfoServiceV2Impl";
    public static final  HiveColumnAtlasEntityBuilder BUILDER           = HiveColumnAtlasEntityBuilder.INSTANCE;
    private static final Joiner                       OR_OP_JOINER      = Joiner.on(" or ");

    @Autowired
    private AtlasService atlasService;

    @Override
    public ColumnInfoV2 getColumnInfo(String columnEntityGuid) {
        final AtlasEntity byGuid;
        try {
            byGuid = atlasService.getByGuid(columnEntityGuid);
            return columnEntitiesToColumnInfo(byGuid).get(0);
        } catch (AtlasServiceException e) {
            log.error("Cannot found entity by guid " + columnEntityGuid, e);
        }
        return null;
    }


    @Override
    public List<ColumnInfoV2> selectColumnInfo(String tableEntityGuid) {
        try {
            final AtlasEntity tableEntity = atlasService.getByGuid(tableEntityGuid);
            
            Map<String, Object> params = new HashMap<>();
            params.put("unique", MapUtils.getString(tableEntity.getAttributes(), "unique"));
            params.put("statusCode", StatusCode.COLUMN_CREATED.getCode());
            final List<AtlasEntity> columnEntities = atlasService.selectingReferences(
                    TYPES_CHOR_HIVE_TABLE, params, "columns");
               return columnEntitiesToColumnInfo(columnEntities.toArray(new AtlasEntity[columnEntities.size()]));
        } catch (AtlasServiceException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return ImmutableList.of();
    }

    @Override
    @Cacheable(key = "'" + CACHE_KEY_PREFIX + ".columnsOfTables'.concat(#tablesUnique.toString())")
    public Map<String,List<ColumnInfoV2>> columnsOfTables(Collection<String> tablesUnique) throws AtlasServiceException {
        String whereCondition = in("unique", tablesUnique);

        String columnQueryStr = TYPES_CHOR_HIVE_TABLE + " where " + whereCondition + ", columns";
        if (log.isDebugEnabled()) {
            log.debug("query string {} for columns of tables {}", columnQueryStr, tablesUnique);
        }
        int offset = 0;
        int limit = 1000;
        List<AtlasEntityHeader> allColumnsHeaders = new ArrayList<>();
        while (true) {
            List<AtlasEntityHeader> columnEntityHeaders = atlasService.dslSearch(columnQueryStr, limit, offset).getEntities();
            if (CollectionUtils.isNotEmpty(columnEntityHeaders)) allColumnsHeaders.addAll(columnEntityHeaders);
            if (CollectionUtils.isEmpty(columnEntityHeaders) || columnEntityHeaders.size() < limit) break;
            offset += limit;
        }
        if (log.isDebugEnabled()) {
            log.debug("there were {} columns of projects {}", allColumnsHeaders.size(), tablesUnique);
        }
        Map<String, List<ColumnInfoV2>> result = Maps.newHashMap();
        allColumnsHeaders.forEach(header -> {
            String unique = MapUtils.getString(header.getAttributes(), "unique");
            String tableUnique = unique.substring(0, unique.lastIndexOf('.'));
            if (!result.containsKey(tableUnique)) {
                result.put(tableUnique, new ArrayList<>());
            }
            String columnName = MapUtils.getString(header.getAttributes(), "name");
            String columnId = header.getGuid();
            result.get(tableUnique).add(ColumnInfoV2.builder().columnName(columnName).columnInfoId(columnId).build());
        });
        return result;
    }

    @Override
    public List<ColumnInfoV2> selectColumnInfoByTableEntity(TableInfoV2 tableInfo) throws AtlasServiceException {
        List<String> guids = tableInfo.getColumns().parallelStream().map(map -> map.get("guid").toString()).collect(Collectors.toList());
        List<AtlasEntity> entities = atlasService.getByGuids(guids);
        return columnEntitiesToColumnInfo(entities.toArray(new AtlasEntity[entities.size()]));
    }


    private String in(String attributeName, Collection<String> attributeValues) {
        return OR_OP_JOINER.join(attributeValues.stream().map(unique -> attributeName + "='" + unique + "'").collect(Collectors.toList()));
    }

    private List<ColumnInfoV2> columnEntitiesToColumnInfo(AtlasEntity... columnEntities) {
        if (columnEntities == null || columnEntities.length == 0) return ImmutableList.of();

        List<ColumnInfoV2> result = Arrays.stream(columnEntities).parallel().map(entity -> ColumnInfoV2.builder()
                                                                              .columnInfoId(BUILDER.guid(entity))
                                                                              .tableInfoId(BUILDER.table(entity))
                                                                                .columnOrder(BUILDER.columnOrder(entity))
                                                                              .columnName(BUILDER.name(entity))
                                                                              .columnDesc(BUILDER.comment(entity))
                                                                              .columnType(BUILDER.type(entity))
                                                                              .columnLength(BUILDER.length(entity))
                                                                              .columnPrecision(BUILDER.precision(entity))
                                                                              .securityLevel(BUILDER.securityLevel(entity))
                                                                              .isKey(BUILDER.booleanToByte(BUILDER.isKey(entity)))
                                                                              .isRefKey(BUILDER.booleanToByte(BUILDER.isForeignKey(entity)))
                                                                              .isIndex(BUILDER.booleanToByte(BUILDER.isIndex(entity)))
                                                                              .isNull(BUILDER.booleanToByte(BUILDER.isNull(entity)))
                                                                              .isPartitionKey(BUILDER.booleanToByte(BUILDER.isPartitionKey(entity)))
                                                                              .createTime(BUILDER.createTime(entity))
                                                                              .updateTime(BUILDER.updateTime(entity))
                                                                              .statusCode(BUILDER.statusCode(entity))
                                                                              .build()).collect(Collectors.toList());
        result.sort(Comparator.comparingInt(value -> {
            final Integer order = value.getColumnOrder();
            return order == null ? 0 : order;
        }));
        return result;
    }

}
