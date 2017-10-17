package cn.rongcapital.chorus.governance;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.AtlasClientV2;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.SearchFilter;
import org.apache.atlas.model.discovery.AtlasSearchResult;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasEntityHeader;
import org.apache.atlas.model.instance.EntityMutationResponse;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.apache.atlas.model.typedef.AtlasClassificationDef;
import org.apache.atlas.model.typedef.AtlasEntityDef;
import org.apache.atlas.model.typedef.AtlasStructDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.atlas.model.typedef.AtlasBaseTypeDef.ATLAS_TYPE_STRING;

@Slf4j
@Component
public class AtlasServiceImpl implements AtlasService {
    public static final int DEFAULT_LIMIT    = 100;
    public static final int BEGINNING_OFFSET = 0;

    private final AtlasClientV2 atlasClientV2;
    private static final String        SIMPLE_DSL_QUERY_TEMPLATE    = "%s where %s %s %s";
    private static final AtlasEntity   _NULL                        = null;
    private static final AtlasEntity[] EMPTY                        = new AtlasEntity[0];
    private static final String        UNIQUE_KEY                   ="unique";
    private int                        MAX_NUM_OF_GUID_IN_GET_PARAM = 300;

    @Autowired
    public AtlasServiceImpl(AtlasClientV2 atlasClientV2) {
        this.atlasClientV2 = atlasClientV2;
    }

    @Override
    public int clean(@Nonnull String... guids) throws AtlasServiceException {
        final EntityMutationResponse entityMutationResponse = atlasClientV2.deleteEntitiesByGuids(Arrays.asList(guids));
        final List<AtlasEntityHeader> deletedEntities = entityMutationResponse.getDeletedEntities();
        if (deletedEntities == null) {
            return 0;
        }
        return deletedEntities.size();
    }
    @Override
    public @Nonnull
    AtlasEntity[] ingest(@Nonnull AtlasEntity... entities) {
        if (entities.length == 0) return EMPTY;
        return Arrays.stream(entities)
                     .filter(Objects::nonNull)
                     .map(atlasEntity -> {
                              log.info("Importing objects {}-{}", atlasEntity.getTypeName(), atlasEntity.getGuid());
                              EntityMutationResponse response = null;
                              try {
                                  response = atlasClientV2.createEntity(new AtlasEntity.AtlasEntityWithExtInfo(atlasEntity));
                                  List<AtlasEntityHeader> createResult = response.getCreatedEntities();//.getEntitiesByOperation(EntityMutations.EntityOperation.CREATE);
                                  if(CollectionUtils.isEmpty(createResult)) createResult = response.getUpdatedEntities();//getEntitiesByOperation(EntityMutations.EntityOperation.UPDATE);

                                  if (CollectionUtils.isNotEmpty(createResult)) {
                                      AtlasEntity.AtlasEntityWithExtInfo getByGuidResponse = atlasClientV2.getEntityByGuid(createResult.get(0).getGuid());
                                      AtlasEntity ret = getByGuidResponse.getEntity();
                                      log.info("Created entity of type [" + ret.getTypeName() + "], guid: " + ret.getGuid());
                                      return ret;
                                  }

                              } catch (AtlasServiceException e) {
                                  log.error(e.getLocalizedMessage(), e);
                              }
                              return _NULL;
                          }
                     ).toArray(AtlasEntity[]::new);
    }

    @Override
    public @Nonnull
    AtlasEntity[] ingestBatch(@Nonnull AtlasEntity... entities) {
        if (entities.length == 0) return EMPTY;
        try{
            AtlasEntity.AtlasEntitiesWithExtInfo atlasEntitiesWithExtInfo = new AtlasEntity.AtlasEntitiesWithExtInfo(Arrays.asList(entities));
            EntityMutationResponse entityMutationResponse = atlasClientV2.createEntities(atlasEntitiesWithExtInfo);
            List<AtlasEntityHeader> createdEntities = entityMutationResponse.getCreatedEntities();
            List<AtlasEntityHeader> updatedEntities = entityMutationResponse.getUpdatedEntities();
            List<AtlasEntityHeader>  atlasEntityHeaders = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(createdEntities)) {
                atlasEntityHeaders.addAll(createdEntities);
            }
            if(CollectionUtils.isNotEmpty(updatedEntities)) {
                atlasEntityHeaders.addAll(updatedEntities);
            }

            return convertAtlasEntitiesWithAtlasEntityHeaders(atlasEntityHeaders,entities);
        }catch (AtlasServiceException e){
            log.error(e.getLocalizedMessage(), e);
        }

        return new AtlasEntity[entities.length];
    }

    @Nullable
    @Override
    public AtlasEntity getEntityByUniqueAttribute(String typeName, String attributeName, String attributeValue) throws AtlasServiceException {
        final HashMap<String, String> attributes = Maps.newHashMap();
        attributes.put(attributeName, attributeValue);

        AtlasEntity.AtlasEntityWithExtInfo extInfo;
        try {
            extInfo = atlasClientV2.getEntityByAttribute(typeName, attributes);
            return extInfo.getEntity();
        } catch (AtlasServiceException e) {
            if(e.getMessage().contains("does not exist")){
                return null;
            }else {
                throw e;
            }
        }
    }

    @Override
    public boolean existAtlasTypeDef(String typeName) throws AtlasServiceException {
        final AtlasTypesDef searchDefs = searchTypes(typeName);
        return !searchDefs.isEmpty();
    }

    private AtlasTypesDef searchTypes(String typeName) throws AtlasServiceException {
        MultivaluedMap<String, String> searchParams = new MultivaluedMapImpl();
        searchParams.clear();
        searchParams.add(SearchFilter.PARAM_NAME, typeName);
        SearchFilter searchFilter = new SearchFilter(searchParams);
        return atlasClientV2.getAllTypeDefs(searchFilter);
    }

    @Override
    public @Nullable
    AtlasStructDef getAtlasStructDef(String typeName) throws AtlasServiceException {
        final AtlasTypesDef searchDefs = searchTypes(typeName);
        if (searchDefs.isEmpty()) return null;

        final List<AtlasStructDef> structDefs = searchDefs.getStructDefs();
        if (CollectionUtils.isNotEmpty(structDefs)) return structDefs.get(0);

        final List<AtlasClassificationDef> classificationDefs = searchDefs.getClassificationDefs();
        if (CollectionUtils.isNotEmpty(classificationDefs)) return classificationDefs.get(0);

        final List<AtlasEntityDef> entityDefs = searchDefs.getEntityDefs();
        if (CollectionUtils.isNotEmpty(entityDefs)) return entityDefs.get(0);

        return null;
    }

    @Override
    public AtlasTypesDef newTypes(AtlasTypesDef typesDef) throws AtlasServiceException {
        return atlasClientV2.createAtlasTypeDefs(typesDef);
    }

    @Deprecated
    @Override
    public boolean cleanTypes(AtlasTypesDef typesDef) {
        try {
            atlasClientV2.deleteAtlasTypeDefs(typesDef);
            return true;
        } catch (AtlasServiceException e) {
            log.error("occurs an error when do clean types def", e);
        }
        return false;
    }

    @Override
    public AtlasEntity getByGuid(String entityGuid) throws AtlasServiceException {
        final AtlasEntity.AtlasEntityWithExtInfo entityByGuid = atlasClientV2.getEntityByGuid(entityGuid);
        return entityByGuid.getEntity();
    }

    @Override
    public List<AtlasEntity> getByGuids(List<String> entityGuids) throws AtlasServiceException {
        final AtlasEntity.AtlasEntitiesWithExtInfo entitiesWithExtInfo = atlasClientV2.getEntitiesByGuids(entityGuids);
        return entitiesWithExtInfo.getEntities();
    }

    @Override
    public AtlasLineageInfo getLineageByGuid(String guid ,AtlasLineageInfo.LineageDirection direction, int depth) throws AtlasServiceException {
        return atlasClientV2.getLineageInfo(guid, direction, depth);
    }

    @Override
    public List<AtlasEntity> dslBaseSearch(String typeName, String attributeName, String op, Object attributeValue) {
        return dslBaseSearch(typeName,attributeName,op,attributeValue,0,Integer.MAX_VALUE);
    }

    @Override
    public AtlasSearchResult dslSearch(final String query) throws AtlasServiceException {
        return dslSearch(query, DEFAULT_LIMIT, BEGINNING_OFFSET);
    }

    @Override
    public AtlasSearchResult dslSearch(final String query, final int limit, final int offset) throws AtlasServiceException {
        return atlasClientV2.dslSearchWithParams(query, limit, offset);
    }

    @Override
    @Nonnull public List<AtlasEntity> dslBaseSearch(String typeName, String attributeName, String op, Object attributeValue, int pageNum, int pageSize) {
        try {
            final String queryString = String.format(SIMPLE_DSL_QUERY_TEMPLATE, typeName, attributeName, op, attributeValueTransform(typeName,attributeName,attributeValue));
//            final String queryString = String.format(SIMPLE_DSL_QUERY_TEMPLATE, typeName, attributeName, op, String.format("'%s'",attributeName));
            if (log.isDebugEnabled()) log.debug(queryString);
            final AtlasSearchResult atlasSearchResult = atlasClientV2.dslSearchWithParams(queryString,pageSize,offset(pageNum,pageSize));

            final List<AtlasEntity> guids = fetchDataByHeader(atlasSearchResult);
            if (guids != null) return guids;
        } catch (AtlasServiceException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return ImmutableList.of();
    }

    private int offset(int pageNum, int pageSize) {
        return (pageNum - 1) * pageSize;
    }

    @Override
    @Nonnull public List<AtlasEntity> fetchDataByHeader(AtlasSearchResult atlasSearchResult) throws AtlasServiceException {
        if (atlasSearchResult != null) {
            final List<AtlasEntityHeader> entities = atlasSearchResult.getEntities();
            if (CollectionUtils.isNotEmpty(entities)) {
                final List<String> guids = entities.parallelStream().map(AtlasEntityHeader::getGuid).collect(Collectors.toList());
                if (guids.size() > MAX_NUM_OF_GUID_IN_GET_PARAM) {// 防止rest get api 的请求url 参数过长
                    final int maxToIndex = guids.size();
                    final int limit = 200;
                    List<AtlasEntity> allEntities = new ArrayList<>(guids.size());
                    int fromIndex = 0;
                    int toIndex;
                    while (fromIndex < maxToIndex) {
                        toIndex = fromIndex + limit;
                        if (toIndex > maxToIndex) toIndex = maxToIndex;
                        allEntities.addAll(atlasClientV2.getEntitiesByGuids(guids.subList(fromIndex, toIndex)).getEntities());
                        fromIndex = toIndex;
                    }
                    return allEntities;
                } else {
                    return atlasClientV2.getEntitiesByGuids(guids).getEntities();
                }
            }
        }
        return ImmutableList.of();
    }

    @Override
    public List<AtlasEntity> selectingReferences(String typeName, String attributeName, String op, Object attributeValue, @Nonnull String referenceAttributeName) {
        try {
            final String queryString = String.format(SIMPLE_DSL_QUERY_TEMPLATE, typeName, attributeName, op, attributeValueTransform(typeName, attributeName, attributeValue))
                                       + (StringUtils.isBlank(referenceAttributeName) ? "" : ", " + referenceAttributeName);
            if (log.isDebugEnabled()) log.debug(queryString);
            final AtlasSearchResult atlasSearchResult = atlasClientV2.dslSearch(queryString);

            final List<AtlasEntity> guids = fetchDataByHeader(atlasSearchResult);
            if (guids != null) return guids;
        } catch (AtlasServiceException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    public Collection<AtlasEntity> selectingByType(String typeName) {
        final Collection<AtlasEntity> allEntities = new ArrayList<>();
        do {
            final Collection<AtlasEntity> atlasEntities = getAtlasEntitiesByType(typeName, BEGINNING_OFFSET, DEFAULT_LIMIT);
            if (CollectionUtils.isNotEmpty(atlasEntities)) {
                allEntities.addAll(atlasEntities);
                if (atlasEntities.size() < DEFAULT_LIMIT) break;
            } else {
                break;
            }
        } while (true);

        return allEntities;
    }

    @Nonnull
    @Override
    public Collection<AtlasEntity> selectingByType(String typeName, int pageNum, int pageSize) {
        return getAtlasEntitiesByType(typeName, offset(pageNum, pageSize), pageSize);
    }

    private Collection<AtlasEntity> getAtlasEntitiesByType(String typeName, int offset, int limit) {
        try {
            final AtlasSearchResult atlasSearchResult = atlasClientV2.dslSearchWithParams(typeName, limit, offset);
            final List<AtlasEntity> guids = fetchDataByHeader(atlasSearchResult);
            if (guids != null) return guids;
        } catch (AtlasServiceException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return ImmutableList.of();
    }

    private String attributeValueTransform(final String typeName, final String attributeName,final Object attributeValue) throws AtlasServiceException {
        final AtlasStructDef.AtlasAttributeDef attributeDef = getAttributeDefByName(ImmutableList.of(typeName), attributeName);

        if (attributeDef != null) {
            return parseToString(attributeValue,attributeDef);
        }
        // still not returned
        throw new IllegalArgumentException("type " + typeName + " hasn't the attribute " + attributeName);
    }

    private AtlasStructDef.AtlasAttributeDef getAttributeDefByName(final Collection<String>  typesName, final String attributeName) {
        if(CollectionUtils.isEmpty(typesName)) return null;

        List<String> superTypes = new ArrayList<>();

        for (String typeName : typesName) {
            try {
                final AtlasStructDef superTypeDef = getAtlasStructDef(typeName);
                final Optional<AtlasStructDef.AtlasAttributeDef> attrDef = getAttributeDef(attributeName, superTypeDef);
                if (attrDef.isPresent()) {
                    return attrDef.get();
                }
                if (superTypeDef instanceof AtlasEntityDef) {
                    superTypes.addAll(((AtlasEntityDef) superTypeDef).getSuperTypes());
                }
                if (superTypeDef instanceof AtlasClassificationDef) {
                    superTypes.addAll(((AtlasClassificationDef) superTypeDef).getSuperTypes());
                }
            } catch (AtlasServiceException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }

        return getAttributeDefByName(superTypes, attributeName);

    }

    private Optional<AtlasStructDef.AtlasAttributeDef> getAttributeDef(String attributeName, AtlasStructDef atlasStructDef) {
        return atlasStructDef.getAttributeDefs()
                             .parallelStream()
                             .filter(atlasAttributeDef -> attributeName.equals(atlasAttributeDef.getName()))
                             .findFirst();
    }

    private String parseToString(Object attributeValue, AtlasStructDef.AtlasAttributeDef attributeDef) {
        final String attributeTypeName = attributeDef.getTypeName();
        switch (attributeTypeName) {
            case ATLAS_TYPE_STRING:
                return "'" + attributeValue + "'";
            //TODO more type support
            default:
                return String.valueOf(attributeValue);
        }
    }

    //TODO 模糊查询方式需要验证
    @Nonnull
    @Override
    public Collection<AtlasEntity> getAllEntitiesByFuzzyName(String typeName, String tableColumnName) throws AtlasServiceException {
        final HashMap<String, String> attributes = Maps.newHashMap();
        attributes.put("name", "*" + tableColumnName + "*");
        final AtlasEntity.AtlasEntityWithExtInfo entityByAttribute = atlasClientV2.getEntityByAttribute(typeName, attributes);
        return entityByAttribute.getReferredEntities().values();
    }

    @Override
    public void update(@Nonnull AtlasEntity... entities) throws AtlasServiceException {
        if (entities.length == 0) return;
        final AtlasEntity.AtlasEntitiesWithExtInfo atlasEntitiesWithExtInfo = new AtlasEntity.AtlasEntitiesWithExtInfo(Arrays.stream(entities)
                                                                                                                             .collect(Collectors.toList()));
        final EntityMutationResponse entityMutationResponse = atlasClientV2.updateEntities(atlasEntitiesWithExtInfo);//TODO enhancement: log more response message
        if (log.isDebugEnabled()) log.debug("atlas entities update");
    }

    private AtlasEntity[] convertAtlasEntitiesWithAtlasEntityHeaders(List<AtlasEntityHeader> atlasEntityHeaders, AtlasEntity ... atlasEntities){
        AtlasEntity[] result = new AtlasEntity[atlasEntities.length];
        //key:unique,value:AtlasEntity
        Map<String,AtlasEntityHeader> atlasEntityMap = new HashMap<>();
        atlasEntityHeaders.forEach(entityHeader->{
            String unique = (String)entityHeader.getAttribute(UNIQUE_KEY);
            atlasEntityMap.put(unique,entityHeader);
            log.info("Created entity of type [{}],unique:{}, guid:{} ",entityHeader.getTypeName(), unique, entityHeader.getGuid());
        });

        for(int i=0; i<atlasEntities.length; i++){
            result[i]=_NULL;
            String unique = (String)atlasEntities[i].getAttribute(UNIQUE_KEY);
            if(unique != null && atlasEntityMap.containsKey(unique)){

                result[i] = createAtlasEntityWithAtlasEntityHeader(atlasEntityMap.get(unique), atlasEntities[i]);
            }
        }

        return result;
    }


    /***
     *
     * @param entityHeader
     * @return notice: return entity not set "version,createdBy,createTime,updateBy,updateTime,class,Classifications" field value.
     */
    private AtlasEntity createAtlasEntityWithAtlasEntityHeader(AtlasEntityHeader entityHeader, AtlasEntity entity){
        AtlasEntity cEntity = new AtlasEntity();
        cEntity.setGuid(entityHeader.getGuid());
        cEntity.setAttributes(entity.getAttributes());
        cEntity.setStatus(entityHeader.getStatus());
        cEntity.setTypeName(entityHeader.getTypeName());
        return cEntity;
    }

    @Override
    public boolean deleteWithUpdate(AtlasEntity entity) throws AtlasServiceException {
        this.update(entity);
        this.clean(entity.getGuid());
        return true;
    }

    @Override
    public List<AtlasEntity> dslSearch(String typeName, Map<String, Object> params, int pageNum, int pageSize) {
        AtlasSearchResult atlasSearchResult = null;
        try {
            if (params == null || params.isEmpty()) {
                atlasSearchResult = atlasClientV2.dslSearchWithParams(typeName, pageSize, offset(pageNum, pageSize));
            } else {
                StringBuilder builder = new StringBuilder();
                params.forEach((paramKey, paramValue) -> {
                    try {
                        if (builder.length() <= 0) {
                            builder.append(paramKey + "=" + attributeValueTransform(typeName, paramKey, paramValue));
                        } else {
                            builder.append(
                                    " and " + paramKey + "=" + attributeValueTransform(typeName, paramKey, paramValue));
                        }
                    } catch (AtlasServiceException e) {
                        log.error(e.getLocalizedMessage(), e);
                    }
                });
                atlasSearchResult = atlasClientV2.dslSearchWithParams(typeName + " where " + builder.toString(), pageSize, offset(pageNum, pageSize));
            }
            final List<AtlasEntity> guids = fetchDataByHeader(atlasSearchResult);
            if (guids != null)
                return guids;
        } catch (AtlasServiceException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return ImmutableList.of();
    }
    
    @Override
    public List<AtlasEntity> selectingReferences(String typeName, Map<String, Object> params,
            String referenceAttributeName) {
        try {
            StringBuilder builder = new StringBuilder();
            params.forEach((paramKey, paramValue) -> {
                try {
                    if (builder.length() <= 0) {
                        builder.append(paramKey + "=" + attributeValueTransform(typeName, paramKey, paramValue));
                    } else {
                        builder.append(
                                " and " + paramKey + "=" + attributeValueTransform(typeName, paramKey, paramValue));
                    }
                } catch (AtlasServiceException e) {
                    log.error(e.getLocalizedMessage(), e);
                }
            });
            String qTemplate = "%s where %s";
            final String queryString = String.format(qTemplate, typeName, builder.toString())
                                       + (StringUtils.isBlank(referenceAttributeName) ? "" : ", " + referenceAttributeName);
            if (log.isDebugEnabled()) log.debug(queryString);
            final AtlasSearchResult atlasSearchResult = atlasClientV2.dslSearch(queryString);
            final List<AtlasEntity> guids = fetchDataByHeader(atlasSearchResult);
            if (guids != null) return guids;
        } catch (AtlasServiceException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return ImmutableList.of();
    }
}
