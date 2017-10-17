package cn.rongcapital.chorus.modules.sink;

import cn.rongcapital.chorus.modules.utils.retry.constants.LineageVerTexType;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.AtlasClientV2;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasEntityHeader;
import org.apache.atlas.model.instance.EntityMutationResponse;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.messaging.Message;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by abiton on 08/08/2017.
 */
@Slf4j
public class AtlasSink extends AbstractMessageHandler {
    @Autowired
    AtlasClientV2 atlasClientV2;

    public static final String MYSQL_TABLE = "chor_mysql_table";
    public static final String CHORUS_TABLE = "chor_hive_table";
    public static final String MYSQL_DB = "chor_mysql_db";
    public static final String unique_template_db="%s,%s,%s,%s";//<projectId>,<connect_user>,<url>

    @Override
    protected void handleMessageInternal(Message<?> message) throws Exception {
        log.debug("atlas sink receive message {}", message);
        JSONObject jsonObject = JSON.parseObject(message.getPayload().toString());
        AtlasEntity entity = new AtlasEntity("chor_hive_process");
        jsonObject.entrySet().stream()
                .forEach(e -> entity.setAttribute(e.getKey(), e.getValue()));
        JSONObject inputs = jsonObject.getJSONObject("inputs");
        JSONObject outputs = jsonObject.getJSONObject("outputs");
        List<AtlasEntity> inputsWithGuid;
        List<AtlasEntity> outputsWithGuid;
        inputsWithGuid = getInputs(inputs);
        outputsWithGuid = getOutputs(outputs);
        entity.setAttribute("operationType", "");
        entity.setAttribute("queryText", "");
        entity.setAttribute("queryPlan", "");
        entity.setAttribute("queryId", "");
        entity.setAttribute("name", jsonObject.getString("moduleViewName"));
        entity.setAttribute("startTime", jsonObject.getDate("startTime"));
        entity.setAttribute("endTime", jsonObject.getDate("endTime"));
        entity.setAttribute("inputs", inputsWithGuid);
        if (inputsWithGuid.isEmpty() || outputsWithGuid.isEmpty()) {
            return;
        }
        outputsWithGuid.forEach(
                e -> {
                    entity.setAttribute("qualifiedName",
                            combineModuleAndTableName(jsonObject.getString("moduleName"), e.getGuid()));
                    entity.setAttribute("outputs", Arrays.asList(e));
                    AtlasEntity.AtlasEntityWithExtInfo entityWithExtInfo = new AtlasEntity.AtlasEntityWithExtInfo(entity);
                    try {
                        atlasClientV2.createEntity(entityWithExtInfo);
                    } catch (AtlasServiceException e1) {
                        log.error("atlas create entity error", e1);
                    }
                }
        );

    }

    private List<AtlasEntity> getOutputs(JSONObject outputs) {
        String type = outputs.getString("type");
        LineageVerTexType lineageVerTexType = LineageVerTexType.valueOf(type);
        switch (lineageVerTexType){
            case EXTERNAL_TABLE:
                return outputs.getJSONArray("tables").stream()
                        .map(e ->{
                            AtlasEntity entity = queryTableGuid(e.toString(), MYSQL_TABLE);
                            if (entity == null) {
                                entity = createEntity(e.toString());
                            }
                            return entity;
                        })
                        .filter(e -> e!=null)
                        .collect(Collectors.toList());
            case INTERNAL_TABLE:
                return outputs.getJSONArray("tables").stream()
                        .map(e -> queryTableGuid(e.toString(), CHORUS_TABLE))
                        .filter(e -> e != null)
                        .collect(Collectors.toList())
                        ;
            default: break;
        }
        return Collections.emptyList();
    }

    private List<AtlasEntity> getInputs(JSONObject inputs) {
        String type = inputs.getString("type");
        LineageVerTexType lineageVerTexType = LineageVerTexType.valueOf(type);
        switch (lineageVerTexType){
            case EXTERNAL_TABLE:
                return inputs.getJSONArray("tables").stream()
                        .map(e ->{
                            AtlasEntity entity = queryTableGuid(e.toString(),MYSQL_TABLE);
                            if (entity == null) {
                                entity = createEntity(e.toString());
                            }
                            return entity;
                        })
                        .filter(e -> e!=null)
                        .collect(Collectors.toList());
            case INTERNAL_TABLE:
                return inputs.getJSONArray("tables").stream()
                        .map(e -> queryTableGuid(e.toString() ,CHORUS_TABLE))
                        .filter(e -> e != null)
                        .collect(Collectors.toList())
                        ;
            default: break;
        }
        return Collections.emptyList();
    }

    String combineModuleAndTableName(String moduleName, String tableName) {
        return moduleName + "_" + tableName;
    }

    AtlasEntity queryTableGuid(String uniqueName, String typeName) {

        Map<String, String> query = new HashMap<String, String>(1);
        query.put("unique", uniqueName);
        try {
            AtlasEntity.AtlasEntityWithExtInfo chor_hive_table = atlasClientV2.getEntityByAttribute(typeName, query);
            return chor_hive_table.getEntity();
        } catch (AtlasServiceException e1) {
            /***
             * if this record is not in atlas,then will throw AtlasServiceException, and message contain does not exist.
             * eg:Caught exception in create:
             org.apache.atlas.AtlasServiceException: Metadata service API org.apache.atlas.AtlasBaseClient$APIInfo@3305974d failed with status 404 (Not Found) Response Body
             ({"errorCode":"ATLAS-404-00-009","errorMessage":"Instance chor_hive_table with unique attribute {unique=chorus_System03.t_hhl_test_0033} does not exist"}
             *
             */
            if (e1.getMessage().contains("does not exist"))
                return null;
            log.error("query atlas error ", e1);
        } catch (RuntimeException e1) {
            log.error("get guid error ", e1);
        }
        return null;
    }

    AtlasEntity createEntity(String unique) {

        try {
            AtlasEntity atlasEntity = getAtlasEntity(unique);
            EntityMutationResponse response = atlasClientV2.createEntity(new AtlasEntity.AtlasEntityWithExtInfo(atlasEntity));
            List<AtlasEntityHeader> createResult = response.getCreatedEntities();//.getEntitiesByOperation(EntityMutations.EntityOperation.CREATE);
            if (CollectionUtils.isEmpty(createResult))
                createResult = response.getUpdatedEntities();//getEntitiesByOperation(EntityMutations.EntityOperation.UPDATE);

            if (CollectionUtils.isNotEmpty(createResult)) {
                AtlasEntity.AtlasEntityWithExtInfo getByGuidResponse = atlasClientV2.getEntityByGuid(createResult.get(0).getGuid());
                AtlasEntity ret = getByGuidResponse.getEntity();
                log.info("Created entity of type [" + ret.getTypeName() + "], guid: " + ret.getGuid());
                return ret;
            }
        } catch (AtlasServiceException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    private AtlasEntity getAtlasEntity(String unique){
        AtlasEntity atlasEntity = new AtlasEntity(MYSQL_TABLE);

        String[] str = unique.split(",");
        String projectId = str[0];
        String rdbName = str[1];
        String connectUser = str[2];
        String url = str[3];
        String name = str[4];//tableName
        atlasEntity.setAttribute("projectId", projectId);
        atlasEntity.setAttribute("connectUser", connectUser);
        atlasEntity.setAttribute("url", url);
        atlasEntity.setAttribute("name", name);
        atlasEntity.setAttribute("unique", unique);

        final HashMap<String, String> attributes = Maps.newHashMap();
        attributes.put("unique", String.format(unique_template_db, projectId, rdbName,connectUser,url));
        AtlasEntity.AtlasEntityWithExtInfo extInfo = null;
        try {
            extInfo = atlasClientV2.getEntityByAttribute(MYSQL_DB, attributes);
        } catch (AtlasServiceException e) {
            if (e.getMessage().contains("does not exist")){
                extInfo = null;
            }
        }
        AtlasEntity dbAtlasEntity = null;
        if(extInfo != null){
            dbAtlasEntity = extInfo.getEntity();
        }
        atlasEntity.setAttribute("database",dbAtlasEntity);
        atlasEntity.setAttribute("qualifiedName",getQualifiedName());
        return atlasEntity;
    }

    private long getQualifiedName() {
        int bound = 100000;
        Random random = new Random();
        int v = random.nextInt(bound);
        return System.currentTimeMillis() * bound + v;
    }
}
