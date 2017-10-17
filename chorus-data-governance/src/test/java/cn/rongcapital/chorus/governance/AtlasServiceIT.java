package cn.rongcapital.chorus.governance;

import cn.rongcapital.chorus.governance.atlas.entity.BaseResourceIT;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import org.apache.atlas.AtlasClient;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.discovery.AtlasSearchResult;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasObjectId;
import org.apache.atlas.model.typedef.AtlasBaseTypeDef;
import org.apache.atlas.model.typedef.AtlasEntityDef;
import org.apache.atlas.model.typedef.AtlasStructDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.apache.atlas.type.AtlasTypeUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.rongcapital.chorus.governance.bridge.ChorusMetaStoreBridge.TYPES_CHOR_HIVE_TABLE;
import static com.google.common.collect.ImmutableList.of;
import static org.apache.atlas.model.typedef.AtlasStructDef.AtlasConstraintDef.CONSTRAINT_PARAM_ATTRIBUTE;
import static org.apache.atlas.model.typedef.AtlasStructDef.AtlasConstraintDef.CONSTRAINT_TYPE_INVERSE_REF;
import static org.apache.atlas.model.typedef.AtlasStructDef.AtlasConstraintDef.CONSTRAINT_TYPE_OWNED_REF;
import static org.apache.commons.collections.MapUtils.getInteger;
import static org.apache.commons.collections.MapUtils.getLong;
import static org.apache.commons.collections.MapUtils.getString;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yimin
 */
public class AtlasServiceIT extends BaseResourceIT {
    private static final String typeVersion = "2.1";

    private AtlasService   atlasService;
    private AtlasTypesDef  typesDef;
    private String         employerTypeName;
    private String         employeeTypeName;
    private AtlasEntityDef employerTypeDef;
    private AtlasEntityDef employeeTypeDef;
    private AtlasEntity    employer;
    private AtlasEntity[]  employeeArray;
    private final Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        super.setUp();

        employerTypeName = randomAlphabetic(5);
        employeeTypeName = randomAlphabetic(5);

        employerTypeDef = employer(employerTypeName, employeeTypeName);
        employeeTypeDef = employee(employeeTypeName, employerTypeName);

        typesDef = AtlasTypeUtil.getTypesDef(ImmutableList.of(), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(employerTypeDef, employeeTypeDef));
        atlasService = new AtlasServiceImpl(atlasClientV2);

        atlasService.newTypes(typesDef);
        System.out.println("new types : " + employerTypeName + ", " + employeeTypeName);

        preData();
    }

    private AtlasEntityDef employer(String employerTypeName, String employeeTypeName) {
        return new AtlasEntityDef(employerTypeName, "", typeVersion, Arrays.asList(
                AtlasTypeUtil.createUniqueRequiredAttrDef("identityCardId", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("name", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createOptionalAttrDef("age", AtlasBaseTypeDef.ATLAS_TYPE_INT),
                AtlasTypeUtil.createOptionalAttrDef("gender", AtlasBaseTypeDef.ATLAS_TYPE_LONG),
                AtlasTypeUtil.createRequiredListAttrDefWithConstraint(
                        "employees",
                        AtlasBaseTypeDef.getArrayTypeName(employeeTypeName),
                        CONSTRAINT_TYPE_OWNED_REF,
                        null
                )
        ), ImmutableSet.of("DataSet"));
    }

    private AtlasEntityDef employee(String employeeTypeName, String employerTypeName) {
        return new AtlasEntityDef(employeeTypeName, "", typeVersion, Arrays.asList(
                AtlasTypeUtil.createUniqueRequiredAttrDef("identityCardId", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("name", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createOptionalAttrDef("age", AtlasBaseTypeDef.ATLAS_TYPE_INT),
                AtlasTypeUtil.createOptionalAttrDef("gender", AtlasBaseTypeDef.ATLAS_TYPE_LONG),
                AtlasTypeUtil.createOptionalAttrDefWithConstraint(
                        "employer",
                        employerTypeName,
                        CONSTRAINT_TYPE_INVERSE_REF,
                        new HashMap<String, Object>() {{
                            put(CONSTRAINT_PARAM_ATTRIBUTE, "employees");
                        }}
                )
        ), ImmutableSet.of("DataSet"));
    }

    private void preData() throws Exception {

        employeeArray = createEmployees();
        assertThat(employeeArray[0]).isNotNull().withFailMessage("employee entity created fail");
        assertThat(employeeArray[1]).isNotNull().withFailMessage("employee entity created fail");
        assertThat(employeeArray[2]).isNotNull().withFailMessage("employee entity created fail");

        employer = employerEntity(employerTypeName, randomNumeric(16), randomAlphabetic(10), 22, 1L, employeeArray);
        final AtlasEntity[] employerIngestResult = atlasService.ingest(employer);
        assertThat(employerIngestResult[0]).isNotNull().withFailMessage("employer entity created fail");

        // feedback employer constraint field
        feedBackEmployerConstraint(employeeArray, employerIngestResult[0]);
    }

    private AtlasEntity[] createEmployees() throws AtlasServiceException {
        AtlasEntity employeeEntity_1 = employeeEntity(employeeTypeName, randomNumeric(16), randomAlphabetic(15), 22, 1L);
        AtlasEntity employeeEntity_2 = employeeEntity(employeeTypeName, randomNumeric(16), randomAlphabetic(15), 25, 1L);
        AtlasEntity employeeEntity_3 = employeeEntity(employeeTypeName, randomNumeric(16), randomAlphabetic(15), 30, 1L);
        return atlasService.ingest(employeeEntity_1, employeeEntity_2, employeeEntity_3);
    }

    private void feedBackEmployerConstraint(AtlasEntity[] employeesIngestResult, AtlasEntity employerEntity) throws AtlasServiceException {
        final AtlasObjectId employerAtlasObjectId = AtlasTypeUtil.getAtlasObjectId(employerEntity);

        employeesIngestResult[0].setAttribute("employer", employerAtlasObjectId);
        employeesIngestResult[1].setAttribute("employer", employerAtlasObjectId);
        employeesIngestResult[2].setAttribute("employer", employerAtlasObjectId);

        atlasService.update(employeesIngestResult);
    }

    private AtlasEntity employeeEntity(String typeName, String identityCardId, String name, int age, long gender) {
        final AtlasEntity entity = new AtlasEntity(typeName);
        entity.setAttribute(AtlasClient.REFERENCEABLE_ATTRIBUTE_NAME, identityCardId);
        entity.setAttribute("identityCardId", identityCardId);
        entity.setAttribute("name", name);
        entity.setAttribute("age", age);
        entity.setAttribute("gender", gender);
        return entity;
    }

    private AtlasEntity employerEntity(String typeName, String identityCardId, String name, int age, long gender, AtlasEntity[] ingest) {
        final AtlasEntity entity = new AtlasEntity(typeName);
        entity.setAttribute(AtlasClient.REFERENCEABLE_ATTRIBUTE_NAME, identityCardId);
        entity.setAttribute("identityCardId", identityCardId);
        entity.setAttribute("name", name);
        entity.setAttribute("age", age);
        entity.setAttribute("gender", gender);
        entity.setAttribute("employees", AtlasTypeUtil.toObjectIds(Arrays.stream(ingest).collect(Collectors.toList())));
        return entity;
    }

    @Test
    public void clean() throws Exception {
        final int cleaned = atlasService.clean(Arrays.stream(employeeArray).map(entity -> entity.getGuid()).collect(Collectors.toList()).toArray(new String[0]));
        assertThat(cleaned).isEqualTo(employeeArray.length);
    }

    @Test
    public void existAtlasTypeDef() throws Exception {
        assertThat(atlasService.existAtlasTypeDef(employerTypeName)).isTrue().withFailMessage("type " + employerTypeName + " show be exist");
        assertThat(atlasService.existAtlasTypeDef(employeeTypeName)).isTrue().withFailMessage("type " + employeeTypeName + " show be exist");
    }

  @Test
    public void getEntityByUniqueAttribute() throws Exception {
      final AtlasEntity expected = employeeArray[0];
      final AtlasEntity actual = atlasService.getEntityByUniqueAttribute(
              employeeTypeName,
              "identityCardId",
              MapUtils.getString(expected.getAttributes(), "identityCardId")
      );
      assertion(expected,actual);
  }

    @Test
    public void getByGuid() throws Exception {

        final AtlasEntity expected = employeeArray[0];
        final AtlasEntity found = atlasService.getByGuid(expected.getGuid());
        assertion(expected, found);
    }

    private void assertion(AtlasEntity expected, AtlasEntity found) {
        final Map<String, Object> expectedAttributes = expected.getAttributes();
        final Map<String, Object> actualAttributes = found.getAttributes();
        assertThat(found.getTypeName()).isEqualTo(employeeTypeName);
        assertThat(getString(actualAttributes, "identityCardId")).isEqualTo(getString(expectedAttributes, "identityCardId"));
        assertThat(getString(actualAttributes, "name")).isEqualTo(getString(expectedAttributes, "name"));
        assertThat(getInteger(actualAttributes, "age")).isEqualTo(getInteger(expectedAttributes, "age"));
        assertThat(getLong(actualAttributes, "gender")).isEqualTo(getLong(expectedAttributes, "gender"));
    }

    @Test
    public void stringTypeAttributeQueryByDSL() throws Exception {
        final AtlasEntity expected = employeeArray[0];
        List<AtlasEntity> byDSL = atlasService.dslBaseSearch(employeeTypeName, "name", "=", getString(expected.getAttributes(), "name"));
        assertThat(byDSL).hasSize(1);
        assertion(expected, byDSL.get(0));
    }

    @Test
    public void intLongTypeAttributeQueryByDSL() throws Exception {
        AtlasEntity expected = employeeArray[0];
        List<AtlasEntity> byDSL = atlasService.dslBaseSearch(employeeTypeName, "age", "=", getInteger(expected.getAttributes(), "age"));
        assertThat(byDSL).hasSize(1);
        assertion(expected, byDSL.get(0));

        expected = employeeArray[1];
        byDSL = atlasService.dslBaseSearch(employeeTypeName, "gender", "=", getLong(expected.getAttributes(), "gender"));
        assertThat(byDSL).size().isGreaterThan(0);
    }


    @Test
    public void selectingReferences() throws Exception {
        final List<AtlasEntity> byDSL = atlasService.selectingReferences(employerTypeName, "name","=", getString(employer.getAttributes(), "name"), "employees");
        assertThat(byDSL).hasSize(3);
    }

    @Test
    public void selectingReferencesByForeignKeyFilter() throws Exception {
        try {
             atlasService.selectingReferences(employerTypeName, "guid","=", employer.getGuid(), "employees");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).matches(Pattern.compile("type [a-zA-Z]{5} hasn't the attribute guid"));
        }
    }

    @Test
    public void selectingByType() throws Exception {
        final Collection<AtlasEntity> byDSL = atlasService.selectingByType(employeeTypeName);
        assertThat(byDSL.parallelStream().filter(e->StringUtils.equals(employeeTypeName,e.getTypeName())).count()).isEqualTo(3);
    }

    @Test
    public void updateTypesDefs() throws Exception {
        final AtlasEntityDef entityDef = atlasClientV2.getEntityDefByName(employerTypeName);
        sout(entityDef);
        final String newAttributeName = "update_types_def_test_" + RandomStringUtils.randomAlphabetic(5);
        entityDef.addAttribute(AtlasTypeUtil.createOptionalAttrDef(newAttributeName, AtlasBaseTypeDef.ATLAS_TYPE_STRING));
        final AtlasTypesDef atlasTypesDef = atlasClientV2.updateAtlasTypeDefs(new AtlasTypesDef(of(), of(), of(), of(entityDef)));
        sout(atlasTypesDef.getEntityDefs().get(0));
        final AtlasEntityDef updated = atlasClientV2.getEntityDefByName(employerTypeName);
        AssertionsForClassTypes.assertThat(updated.getAttributeDefs().parallelStream().filter(atlasAttributeDef -> newAttributeName.equals(atlasAttributeDef.getName())).count()).isEqualTo(1);
    }

    private void sout(AtlasStructDef atlasStructDef) {
        System.out.println(atlasStructDef.getName() + "[version:" + atlasStructDef.getVersion() + ",typeVersion:" + atlasStructDef.getTypeVersion() + "]");
        descAttributeDef(atlasStructDef.getAttributeDefs());
    }

    private void descAttributeDef(List<AtlasStructDef.AtlasAttributeDef> attributeDefs) {
        attributeDefs.forEach(atlasAttributeDef -> {
            System.out.println("\t\t" + atlasAttributeDef.toString());
        });
    }

    @Test
    public void pageableSupport() throws Exception {
        Collection<AtlasEntity> entities = atlasService.selectingByType(employeeTypeName, 10, 10);
        assertThat(entities.size()).isLessThanOrEqualTo(10);
    }

    //    @Test
    public void dslBaseSearchByInheritAttribute() throws Exception {
        atlasService.dslBaseSearch(TYPES_CHOR_HIVE_TABLE, "name", "=", "tag");
    }

//    @Test
    public void getAllEntitiesByFuzzyName() throws Exception {
    }

//    @Test
    public void dslSearchTest() throws Exception {
//        atlasClientV2.getEntitiesByGuids()
        String tablesOfProjects = "chor_hive_table where projectId=222602 or projectId=222622 or projectId=222631 or projectId=222627 or projectId=222495 or projectId=222630";
        AtlasSearchResult atlasSearchResult = null;//atlasClientV2.dslSearch(tablesOfProjects);
//        System.out.println(gson.toJson(atlasSearchResult));
//        atlasSearchResult = atlasClientV2.dslSearchWithParams(tablesOfProjects + ",columns",Integer.MAX_VALUE,0);
//        System.out.println(gson.toJson(atlasSearchResult));
        atlasSearchResult =atlasClientV2.dslSearchWithParams("chor_hive_table where unique='chorus_erp.t_brnd' or unique='chorus_erp.t_order' or unique='chorus_erp.t_rtrnexch_exch' or unique='chorus_erp.t_top_interface_promotiondetail' or unique='chorus_erp.t_top_interface_tradeaccountdetail' or unique='chorus_erp.t_stor' or unique='chorus_erp.t_goods' or unique='chorus_abitontest1.batch_job_execution' or unique='chorus_abitontest1.sample2' or unique='chorus_abitontest1.table_sample' or unique='chorus_erp.t_rtrn_exch' or unique='chorus_erp.t_aftersale_package' or unique='chorus_erp.t_goods_sku' or unique='chorus_erp.t_lgst_comp' or unique='chorus_erp.t_ruixue_cust' or unique='chorus_erp.t_refund_addfund' or unique='chorus_erp.t_rtrnexch_fact_rtrn' or unique='chorus_erp.t_refund_addfund_dtls' or unique='chorus_abitontest1.testtimestamp3' or unique='chorus_erp.t_stockout_request' or unique='chorus_erp.t_bi_crm_cust' or unique='chorus_abitontest1.batch_job_execution_param' or unique='chorus_erp.t_stockout_dtls' or unique='chorus_erp.t_stockout' or unique='chorus_erp.t_order_goods' or unique='chorus_erp.t_all_type', columns",1000,0);
        System.out.println(gson.toJson(atlasSearchResult.getEntities().size()));
    }

//    @Test
//    public void updateTypeDef() throws Exception {
//        final AtlasEntityDef columnEntityDef = atlasClientV2.getEntityDefByName(TYPES_CHOR_HIVE_COLUMN);
//        sout(columnEntityDef);
//        columnEntityDef.addAttribute(AtlasTypeUtil.createOptionalAttrDef("order",AtlasBaseTypeDef.ATLAS_TYPE_INT));
//        final AtlasTypesDef atlasTypesDef = atlasClientV2.updateAtlasTypeDefs(new AtlasTypesDef(of(), of(), of(), of(columnEntityDef)));
//        sout(atlasTypesDef.getEntityDefs().get(0));
//    }

    @After
    public void tearDown() throws Exception {
//        atlasService.cleanTypes(typesDef);
    }
    
    /**
     * @throws Exception
     * @author yunzhong
     * @time 2017年9月14日下午2:53:37
     */
    @Test
    public void testselectingReferences() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("statusCode", "1311");
        params.put("unique", "chorus_yunProject11.undelete1");
        final List<AtlasEntity> byDSL = atlasService.selectingReferences("chor_hive_table",params , "columns");
        assertThat(byDSL).hasSize(5);
    }
}
