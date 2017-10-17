package cn.rongcapital.chorus.governance.atlas.entity;

import cn.rongcapital.chorus.governance.AtlasService;
import cn.rongcapital.chorus.governance.AtlasServiceImpl;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.SearchFilter;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.EntityMutationResponse;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.apache.atlas.model.typedef.AtlasBaseTypeDef;
import org.apache.atlas.model.typedef.AtlasEntityDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.apache.atlas.type.AtlasTypeUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.core.MultivaluedMap;
import java.util.*;

import static org.apache.atlas.ApplicationProperties.ATLAS_CONFIGURATION_DIRECTORY_PROPERTY;

/**
 * Created by Athletics on 2017/7/26.
 */
public class AtlasServiceTest extends BaseResourceIT {

    public static final String TYPES_HIVE_DB      = "hive_db";
    public static final String TYPES_HIVE_TABLE   = "hive_table";
    public static final String TYPES_HIVE_COLUMN  = "hive_column";
    public static final String TYPES_HIVE_PROCESS = "hive_process";

    private final        String TYPE_VERSION    = "0.1";

    private AtlasService atlasService;

    @BeforeClass
    public void setUp() throws Exception {
        System.setProperty(ATLAS_CONFIGURATION_DIRECTORY_PROPERTY, "chorus-data-governance/src/test/resources");
        super.setUp();

        atlasService = new AtlasServiceImpl(atlasClientV2);
    }

    private AtlasEntityDef dbEntityDef(){
        AtlasEntityDef atlasEntityDef = new AtlasEntityDef("chor_hive_db_test_06", "", TYPE_VERSION, Arrays.asList(
                AtlasTypeUtil.createRequiredAttrDef("createTime", AtlasBaseTypeDef.ATLAS_TYPE_DATE),
                AtlasTypeUtil.createRequiredAttrDef("createUser", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("createUserId", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("project", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("projectId", AtlasBaseTypeDef.ATLAS_TYPE_LONG),
                AtlasTypeUtil.createUniqueRequiredAttrDef("unique", AtlasBaseTypeDef.ATLAS_TYPE_STRING)
        ), ImmutableSet.of(TYPES_HIVE_DB));
        return atlasEntityDef;
    }

    private AtlasEntityDef tableEntityDef(){
        AtlasEntityDef atlasEntityDef = new AtlasEntityDef("chor_hive_table_test_07", "", TYPE_VERSION, Arrays.asList(
                AtlasTypeUtil.createRequiredAttrDef("createUser", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("createUserId", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("project", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("projectId", AtlasBaseTypeDef.ATLAS_TYPE_LONG),
                AtlasTypeUtil.createOptionalAttrDef("code", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("statusCode", AtlasBaseTypeDef.ATLAS_TYPE_STRING),

                AtlasTypeUtil.createOptionalAttrDef("securityLevel", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createOptionalAttrDef("dataUpdateFrequency", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createOptionalAttrDef("dataType", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createOptionalAttrDef("businessField", AtlasBaseTypeDef.ATLAS_TYPE_STRING),

                AtlasTypeUtil.createOptionalAttrDef("snapshot", AtlasBaseTypeDef.ATLAS_TYPE_BOOLEAN),
                AtlasTypeUtil.createOptionalAttrDef("open", AtlasBaseTypeDef.ATLAS_TYPE_BOOLEAN),
                AtlasTypeUtil.createOptionalAttrDef("sla", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createUniqueRequiredAttrDef("unique", AtlasBaseTypeDef.ATLAS_TYPE_STRING)
        ), ImmutableSet.of(TYPES_HIVE_TABLE));
        return atlasEntityDef;
    }

    private AtlasEntityDef colunmsEntityDef(){
        AtlasEntityDef atlasEntityDef = new AtlasEntityDef("chor_hive_column_test_07", "", TYPE_VERSION, Arrays.asList(
                AtlasTypeUtil.createRequiredAttrDef("project", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("projectId", AtlasBaseTypeDef.ATLAS_TYPE_LONG),
                AtlasTypeUtil.createRequiredAttrDef("statusCode", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("length", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("precision", AtlasBaseTypeDef.ATLAS_TYPE_STRING),

                AtlasTypeUtil.createRequiredAttrDef("isKey", AtlasBaseTypeDef.ATLAS_TYPE_BOOLEAN),
                AtlasTypeUtil.createRequiredAttrDef("isForeignKey", AtlasBaseTypeDef.ATLAS_TYPE_BOOLEAN),
                AtlasTypeUtil.createRequiredAttrDef("isNull", AtlasBaseTypeDef.ATLAS_TYPE_BOOLEAN),
                AtlasTypeUtil.createRequiredAttrDef("isIndex", AtlasBaseTypeDef.ATLAS_TYPE_BOOLEAN),
                AtlasTypeUtil.createRequiredAttrDef("isPartitionKey", AtlasBaseTypeDef.ATLAS_TYPE_BOOLEAN),

                AtlasTypeUtil.createRequiredAttrDef("securityLevel", AtlasBaseTypeDef.ATLAS_TYPE_STRING),

                AtlasTypeUtil.createRequiredAttrDef("createTime", AtlasBaseTypeDef.ATLAS_TYPE_DATE),
                AtlasTypeUtil.createRequiredAttrDef("updateTime", AtlasBaseTypeDef.ATLAS_TYPE_DATE),

                AtlasTypeUtil.createUniqueRequiredAttrDef("unique", AtlasBaseTypeDef.ATLAS_TYPE_STRING)
        ), ImmutableSet.of(TYPES_HIVE_COLUMN));
        return atlasEntityDef;
    }

    private AtlasEntity getDbEntity(String typeName,String name,String unique){
        AtlasEntity atlasEntity = new AtlasEntity(typeName);
        atlasEntity.setAttribute(NAME, name);
        atlasEntity.setAttribute(DESCRIPTION, "test database entity");
        atlasEntity.setAttribute("locationUrl", "");
        atlasEntity.setAttribute("owner", "user1");
        atlasEntity.setAttribute("qualifiedName",System.currentTimeMillis());
        atlasEntity.setAttribute("unique",unique);
        atlasEntity.setAttribute("createUser","ymjtest");
        atlasEntity.setAttribute("createUserId","111");
        atlasEntity.setAttribute("project","aa");
        atlasEntity.setAttribute("projectId",111L);


        atlasEntity.setAttribute("clusterName","");
        atlasEntity.setAttribute("createTime",new Date());
        return atlasEntity;
    }

    @Test
    public void createDBAtlasTypeDefsTest() throws Exception {
        AtlasEntityDef atlasEntityDef = dbEntityDef();
        final AtlasTypesDef typesDef = AtlasTypeUtil.getTypesDef(ImmutableList.of(), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(atlasEntityDef));
        AtlasTypesDef atlasTypesDef = atlasClientV2.createAtlasTypeDefs(typesDef);

        System.out.println(atlasTypesDef.toString());
    }

    @Test
    public void createTableAtlasTypeDefsTest() throws Exception {
        AtlasEntityDef atlasEntityDef = tableEntityDef();
        final AtlasTypesDef typesDef = AtlasTypeUtil.getTypesDef(ImmutableList.of(), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(atlasEntityDef));
        AtlasTypesDef atlasTypesDef = atlasClientV2.createAtlasTypeDefs(typesDef);

        System.out.println(atlasTypesDef.toString());
    }
    @Test
    public void createColumnAtlasTypeDefsTest() throws Exception {
        AtlasEntityDef atlasEntityDef = colunmsEntityDef();
        final AtlasTypesDef typesDef = AtlasTypeUtil.getTypesDef(ImmutableList.of(), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(atlasEntityDef));
        AtlasTypesDef atlasTypesDef = atlasClientV2.createAtlasTypeDefs(typesDef);

        System.out.println(atlasTypesDef.toString());
    }

    @Test
    public void createEntitiesTest() throws AtlasServiceException {
        AtlasEntity atlasEntity_01 = getDbEntity("chor_hive_db_test","name0011","name0011");
        AtlasEntity atlasEntity_02 = getDbEntity("chor_hive_db_test", "name0021", "name0021");
        AtlasEntity atlasEntity_03 = getDbEntity("chor_hive_db_test", "name0031", "name0031");
        AtlasEntity[] entities = atlasService.ingest(atlasEntity_01, atlasEntity_02, atlasEntity_03);
        System.out.println(entities[0].toString());
    }

    @Test
    public void createDbEntityTest() throws AtlasServiceException {
        AtlasEntity atlasEntity = getDbEntity("chor_hive_db_test","test_db_name","test_db_name");

        EntityMutationResponse response = atlasClientV2.createEntity(new AtlasEntity.AtlasEntityWithExtInfo(atlasEntity));
        System.out.println("response = " + response);
    }

    @Test
    public void createTableEntityTest() throws AtlasServiceException {
        AtlasEntity atlasEntity = new AtlasEntity("chor_hive_table_test");
        atlasEntity.setAttribute(NAME, "test_table_name");
        atlasEntity.setAttribute("owner", "user1");
        atlasEntity.setAttribute("comment", "table_comment");

        final HashMap<String, String> attributes = Maps.newHashMap();
        attributes.put("unique", "test_db_name");
        final AtlasEntity.AtlasEntityWithExtInfo extInfo = atlasClientV2.getEntityByAttribute("chor_hive_db_test", attributes);
        AtlasEntity dbAtlasEntity = extInfo.getEntity();

        atlasEntity.setAttribute("db", dbAtlasEntity);
        atlasEntity.setAttribute("locationUrl", "table_locationUrl");
        atlasEntity.setAttribute("tableType", "tableType");
        atlasEntity.setAttribute("partitionKeys", "tableType");
        atlasEntity.setAttribute("columns", "columns");

        atlasEntity.setAttribute("partitionKeys", "tableType");
        atlasEntity.setAttribute("createUser","ymjtest");
        atlasEntity.setAttribute("createUserId","111");
        atlasEntity.setAttribute("project","aa");
        atlasEntity.setAttribute("projectId","0a08b136-d446-4563-8836-3ecd68211a9d");
        atlasEntity.setAttribute("businessField","aa");
        atlasEntity.setAttribute("dataType","aa");
        atlasEntity.setAttribute("dataUpdateFrequency","aa");
        atlasEntity.setAttribute("securityLevel","aa");
        atlasEntity.setAttribute("qualifiedName","test_db_table_name");
        atlasEntity.setAttribute("unique","test_db_table_name");
        atlasEntity.setAttribute("snapshot",false);
        atlasEntity.setAttribute("sla","sla");
        atlasEntity.setAttribute("open",false);
        atlasEntity.setAttribute("createTime",new Date());


        EntityMutationResponse response = atlasClientV2.createEntity(new AtlasEntity.AtlasEntityWithExtInfo(atlasEntity));
        System.out.println("response = " + response);
    }

    @Test
    public void createColumnEntityTest() throws AtlasServiceException {
        AtlasEntity atlasEntity = new AtlasEntity("chor_hive_db_test");
        atlasEntity.setAttribute(NAME, "test_column_name");
        atlasEntity.setAttribute("type","string");
        atlasEntity.setAttribute("comment","columns test");
        atlasEntity.setAttribute("position",2);
        atlasEntity.setAttribute("project","aa");
        atlasEntity.setAttribute("length",10);
        atlasEntity.setAttribute("isKey",true);
        atlasEntity.setAttribute("isNull",(byte)1);
        atlasEntity.setAttribute("isPartitionKey",(byte)1);
        atlasEntity.setAttribute("securityLevel",1);
        atlasEntity.setAttribute("unique","test_db_table_columns_name");
        atlasEntity.setAttribute("qualifiedName","test_db_table_columns_name");
        atlasEntity.setAttribute("createTime",new Date());
        atlasEntity.setAttribute("updateTime",new Date());

        EntityMutationResponse response = atlasClientV2.createEntity(new AtlasEntity.AtlasEntityWithExtInfo(atlasEntity));
        System.out.println("response = " + response);
    }


    @Test
    public void getEntityByAttributeTest() throws AtlasServiceException {

        String typeName = "chor_hive_db_test";
        Map<String, String> map = new HashMap<>();
        map.put("unique","test_db_name");
        AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo = atlasClientV2.getEntityByAttribute(typeName, map);
        System.out.println("value = " + atlasEntityWithExtInfo.toString());
    }

    @Test
    public void updateEntityByAttributeTest() throws AtlasServiceException {
        String typeName = "chor_hive_db_test";

        Map<String, String> map = new HashMap<>();
        map.put("unique", "test_db_name");
        AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo = atlasClientV2.getEntityByAttribute(typeName, map);
        System.out.println("value = " + atlasEntityWithExtInfo.toString());

        atlasEntityWithExtInfo.getEntity().getAttributes().put("project", "bb");
        Map<String, String> map1 = new HashMap<>();
        map1.put("unique", "test_db_name");
        EntityMutationResponse response = atlasClientV2.updateEntityByAttribute(typeName, map1, atlasEntityWithExtInfo);
        System.out.println("response = " + response.toString());

        Map<String, String> map2 = new HashMap<>();
        map2.put("unique", "test_db_name");
        AtlasEntity.AtlasEntityWithExtInfo result = atlasClientV2.getEntityByAttribute(typeName, map2);
        System.out.println("value = " + result.toString());
    }

    @Test
    public void deleteAtlasTypeDefTest() throws Exception{
        AtlasEntityDef atlasEntityDef4 = new AtlasEntityDef("chor_hive_column_test", "", TYPE_VERSION, Arrays.asList(
                AtlasTypeUtil.createRequiredAttrDef("project", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("projectId", AtlasBaseTypeDef.ATLAS_TYPE_LONG),
                AtlasTypeUtil.createRequiredAttrDef("statusCode", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("length", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("precision", AtlasBaseTypeDef.ATLAS_TYPE_STRING),


                AtlasTypeUtil.createRequiredAttrDef("isKey", AtlasBaseTypeDef.ATLAS_TYPE_BOOLEAN),
                AtlasTypeUtil.createRequiredAttrDef("isForeignKey", AtlasBaseTypeDef.ATLAS_TYPE_BOOLEAN),
                AtlasTypeUtil.createRequiredAttrDef("isNull", AtlasBaseTypeDef.ATLAS_TYPE_BOOLEAN),
                AtlasTypeUtil.createRequiredAttrDef("isIndex", AtlasBaseTypeDef.ATLAS_TYPE_BOOLEAN),
                AtlasTypeUtil.createRequiredAttrDef("isPartitionKey", AtlasBaseTypeDef.ATLAS_TYPE_BOOLEAN),

                AtlasTypeUtil.createRequiredAttrDef("securityLevel", AtlasBaseTypeDef.ATLAS_TYPE_STRING),

                AtlasTypeUtil.createRequiredAttrDef("createTime", AtlasBaseTypeDef.ATLAS_TYPE_DATE),
                AtlasTypeUtil.createRequiredAttrDef("updateTime", AtlasBaseTypeDef.ATLAS_TYPE_DATE),
                AtlasTypeUtil.createUniqueRequiredAttrDef("unique", AtlasBaseTypeDef.ATLAS_TYPE_STRING)
        ), ImmutableSet.of(TYPES_HIVE_COLUMN));
        final AtlasTypesDef typesDef = AtlasTypeUtil.getTypesDef(ImmutableList.of(), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(atlasEntityDef4));
        atlasClientV2.deleteAtlasTypeDefs(typesDef);
    }



    @Test
    public void existAtlasTypeDefTest() throws Exception{
        String typeName = "chor_hive_column_test";
        MultivaluedMap<String, String> searchParams = new MultivaluedMapImpl();
        searchParams.clear();
        searchParams.add(SearchFilter.PARAM_NAME, typeName);
        SearchFilter searchFilter = new SearchFilter(searchParams);
        final AtlasTypesDef searchDefs = atlasClientV2.getAllTypeDefs(searchFilter);
        System.out.println("result = " + !searchDefs.isEmpty());
    }

    @Test
    public void deleteEntityTest() throws Exception{
        final AtlasTypesDef allTypeDefs = atlasClientV2.getAllTypeDefs(new SearchFilter());
        final List<AtlasEntityDef> entityDefs = allTypeDefs.getEntityDefs();
        entityDefs.forEach(entityDef -> {
            final String typeName = entityDef.getName();
            System.out.println("\t" + typeName);
            if ("chor_hive_table".equals(typeName) || "chor_hive_db".equals(typeName)) {
                try {
                    atlasClientV2.deleteEntityByGuid(entityDef.getGuid());
                } catch (AtlasServiceException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    @Test
    public void getEntityByGuid() throws AtlasServiceException {
        System.out.println("method = getEntityByGuid");
        AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo =atlasClientV2.getEntityByGuid("f75dbca5-9d3a-4cb6-8688-6ae867278efd");
        System.out.println("value = " + atlasEntityWithExtInfo);
    }

    @Test
    public void deleteEntityByGuid() throws AtlasServiceException {
        System.out.println("method = deleteEntityByGuid");
        EntityMutationResponse response = atlasClientV2.deleteEntityByGuid("f75dbca5-9d3a-4cb6-8688-6ae867278efd");
        System.out.println("response = " + response.toString());
    }

    @Test
    public void getEntityByNameTest() throws Exception{
        String uniqueDBName = "unique";
        final HashMap<String, String> attributes = Maps.newHashMap();
        attributes.put("unique", uniqueDBName);
        final AtlasEntity.AtlasEntityWithExtInfo extInfo = atlasClientV2.getEntityByAttribute("chor_hive_column_test", attributes);
        System.out.println(extInfo.toString());
    }
    @Test
    public void getEntitiesByGuidsTest() throws AtlasServiceException {
        List<String> guids = new ArrayList<>();
        guids.add("c2c0bb8b-51f0-4df2-8daa-3646b4c4639b");
        guids.add("967e8353-3533-45cf-9046-e5623f2e4a37");
        guids.add("884e5af2-c8b5-4691-9ed8-104883a3a8df");
        guids.add("3515619a-6ab5-4588-adf1-595769603ff3");
        guids.add("21180fac-c4c4-4af4-be91-f41cd217b977");
        guids.add("d822b3f8-bc07-478f-97f7-a9e9bce57d30");
        guids.add("e8687460-11eb-47a8-969e-8160dd322990");
        guids.add("873fcb03-2ee7-48f6-97d1-e6b0dccba889");
        guids.add("c2c0bb8b-51f0-4df2-8daa-3646b4c4639b");
        guids.add("967e8353-3533-45cf-9046-e5623f2e4a37");
        guids.add("884e5af2-c8b5-4691-9ed8-104883a3a8df");
        guids.add("3515619a-6ab5-4588-adf1-595769603ff3");
        guids.add("21180fac-c4c4-4af4-be91-f41cd217b977");
        guids.add("d822b3f8-bc07-478f-97f7-a9e9bce57d30");
        guids.add("e8687460-11eb-47a8-969e-8160dd322990");
        guids.add("873fcb03-2ee7-48f6-97d1-e6b0dccba889");
        guids.add("55d0f073-4f1e-4dc6-9c2f-3225f1f6ebe2");
        AtlasEntity.AtlasEntitiesWithExtInfo atlasEntitiesWithExtInfo = atlasClientV2.getEntitiesByGuids(guids);
        System.out.println(atlasEntitiesWithExtInfo.getEntities().size());
    }

    @Test
    public void lineageQueryTest() throws AtlasServiceException {
        AtlasLineageInfo atlasLineageInfo = atlasClientV2.getLineageInfo("e8687460-11eb-47a8-969e-8160dd322990", AtlasLineageInfo.LineageDirection.INPUT,100);
        System.out.println(atlasLineageInfo.toString());
    }
}
