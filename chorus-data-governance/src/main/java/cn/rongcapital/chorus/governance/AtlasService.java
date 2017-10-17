package cn.rongcapital.chorus.governance;

import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.discovery.AtlasSearchResult;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.apache.atlas.model.typedef.AtlasStructDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author yimin
 */
public interface AtlasService {
    /**
     * Get the type def specified by {@code typeName}
     *
     * @param typeName unique type name
     *
     * @return
     *
     * @throws AtlasServiceException
     */
    AtlasStructDef getAtlasStructDef(String typeName) throws AtlasServiceException;

    /**
     * 保存新的 atlas type
     *
     * @param typesDef
     *
     * @return
     *
     * @throws AtlasServiceException
     */
    AtlasTypesDef newTypes(AtlasTypesDef typesDef) throws AtlasServiceException;

    /**
     * 清理 atlas types 定义
     *
     * @deprecated  cannot real del types def
     * @param typesDef
     * @return
     */
    @Deprecated
    boolean cleanTypes(AtlasTypesDef typesDef);


    /**
     * 检查 {@code typeName} 指定的 atlas type 是否已经定义
     *
     * @param typeName
     *
     * @return
     *
     * @throws AtlasServiceException
     */
    boolean existAtlasTypeDef(String typeName) throws AtlasServiceException;

    int clean(@Nonnull String... guids) throws AtlasServiceException;

    /**
     * 收集 entities
     *
     * @param entities 收集原则：尽最大可能收集
     *
     * @return int[]  收集结果;返回结果与参数按位置对应； 采集成功：not null 并且有正确的guid； 失败：null
     *
     * @throws AtlasServiceException
     */
    @Nonnull
    AtlasEntity[] ingest(AtlasEntity... entities) throws AtlasServiceException;


    /**
     * 收集 entities
     *
     * @param entities 收集原则：尽最大可能收集
     *
     * @return int[]  收集结果;返回结果与参数按位置对应； 采集成功：not null 并且有正确的guid； 失败：null
     *
     * @throws AtlasServiceException
     */
    @Nonnull
    AtlasEntity[] ingestBatch(AtlasEntity... entities) throws AtlasServiceException;


    /**
     * get the {@link AtlasEntity} by {@code uniqueName}
     *
     * @param typeName
     * @param attributeName
     * @param attributeValue
     *
     * @return
     */
    @Nullable
    AtlasEntity getEntityByUniqueAttribute(String typeName, String attributeName, String attributeValue) throws AtlasServiceException;

    /**
     * @param entityGuid
     *
     * @return
     */
    AtlasEntity getByGuid(String entityGuid) throws AtlasServiceException;

    /**
     * DSL 查询
     *
     * @param typeName
     * @param attributeName
     * @param op
     * @param attributeValue
     *
     * @return
     */
    @Nonnull List<AtlasEntity> dslBaseSearch(String typeName, String attributeName, String op, Object attributeValue);

    @Nonnull
    AtlasSearchResult dslSearch(String query) throws AtlasServiceException;

    @Nonnull
    AtlasSearchResult dslSearch(String query, int limit, int offset) throws AtlasServiceException;

    /**
     * {@link AtlasService#dslBaseSearch}
     *
     * @param typeName
     * @param attributeName
     * @param op
     * @param attributeValue
     * @param pageNum
     * @param pageSize
     *
     * @return
     */
    @Nonnull List<AtlasEntity> dslBaseSearch(String typeName, String attributeName, String op, Object attributeValue, int pageNum, int pageSize);

    @Nonnull List<AtlasEntity> fetchDataByHeader(AtlasSearchResult atlasSearchResult) throws AtlasServiceException;

    /**
     *
     * @param typeName
     * @param attributeName
     * @param op
     * @param attributeValue
     * @param referenceAttributeName
     * @return
     */
    @Nullable List<AtlasEntity> selectingReferences(String typeName, String attributeName, String op, Object attributeValue, @Nonnull String referenceAttributeName);

    /**
     * 查询 {@code typeName} 类型的所有 entities
     * @param typeName
     * @return
     */
    @Nonnull Collection<AtlasEntity> selectingByType(String typeName);

    /**
     * {@link AtlasService#selectingByType}
     *
     * @param typeName
     * @param limit
     * @param offset
     *
     * @return
     */
    @Nonnull
    Collection<AtlasEntity> selectingByType(final String typeName, final int limit, final int offset);
    /**
     * 查询 {@code typeName} 根据table/column name 查询类型的所有 entities
     * @param typeName
     * @return
     */
    @Nonnull
    Collection<AtlasEntity> getAllEntitiesByFuzzyName(String typeName, String tableColumnName) throws AtlasServiceException;

    /**
     * atlas entity 更新
     *
     * @param employeeEntities
     *
     * @throws AtlasServiceException
     */
    void update(@Nonnull AtlasEntity... employeeEntities) throws AtlasServiceException;

    /**
     * 根据guid批量查询
     * @param entityGuids
     * @return
     */
    List<AtlasEntity> getByGuids(List<String> entityGuids) throws AtlasServiceException;

    /**
     * 根据guid查询血缘关系
     * @return
     * @throws AtlasServiceException
     */
    AtlasLineageInfo getLineageByGuid(String guid ,AtlasLineageInfo.LineageDirection direction, int depth) throws AtlasServiceException;

    /**
     * @param entity
     * @author yunzhong
     * @time 2017年9月11日下午3:13:23
     */
    boolean deleteWithUpdate(AtlasEntity entity)  throws AtlasServiceException;
    
    /**
     * @param typeName
     * @param params
     * @param pageNum
     * @param pageSize
     * @return
     * @author yunzhong
     * @time 2017年9月11日下午4:46:06
     */
    @Nonnull
    List<AtlasEntity> dslSearch(String typeName, Map<String,Object> params, int pageNum, int pageSize);
    
    /**
     * @param typeName
     * @param params
     * @param referenceAttributeName
     * @return
     * @author yunzhong
     * @time 2017年9月14日下午2:46:37
     */
    @Nullable List<AtlasEntity> selectingReferences(String typeName, Map<String,Object> params, @Nonnull String referenceAttributeName);

}
