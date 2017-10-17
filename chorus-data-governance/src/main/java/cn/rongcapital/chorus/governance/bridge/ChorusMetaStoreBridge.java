package cn.rongcapital.chorus.governance.bridge;

import cn.rongcapital.chorus.governance.AtlasService;
import cn.rongcapital.chorus.governance.AtlasServiceImpl;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.atlas.ApplicationProperties;
import org.apache.atlas.AtlasClientV2;
import org.apache.atlas.model.typedef.AtlasBaseTypeDef;
import org.apache.atlas.model.typedef.AtlasEntityDef;
import org.apache.atlas.model.typedef.AtlasEnumDef;
import org.apache.atlas.type.AtlasTypeUtil;
import org.apache.atlas.utils.AuthenticationUtil;
import org.apache.commons.configuration.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.util.Arrays;
import java.util.List;

/**
 * @author yimin
 */
public class ChorusMetaStoreBridge extends AtlasMetadataStoreBridge {
    public static final String TYPES_CHOR_HIVE_DB               = "chor_hive_db";
    public static final String TYPES_CHOR_HIVE_TABLE            = "chor_hive_table";
    public static final String TYPES_CHOR_HIVE_COLUMN           = "chor_hive_column";
    public static final String TYPES_CHOR_HIVE_PROCESS           = "chor_hive_process";
    public static final String TYPES_CHOR_SECURITY_LEVEL        = "chor_security_level";
    public static final String TYPES_CHOR_DATA_UPDATE_FREQUENCY = "chor_data_update_frequency";
    public static final String TYPES_CHOR_DATA_TYPE             = "chor_data_type";
    public static final String TYPES_CHOR_BUSINESS_FIELD        = "chor_business_field";
    public static final String TYPE_VERSION                     = "0.1";

    public static final String TYPES_HIVE_DB      = "hive_db";
    public static final String TYPES_HIVE_TABLE   = "hive_table";
    public static final String TYPES_HIVE_COLUMN  = "hive_column";
    public static final String TYPES_HIVE_PROCESS = "hive_process";

    private static final String DEFAULT_DGI_URL = "http://localhost:21000/";
    private static final String ATLAS_ENDPOINT  = "atlas.rest.address";
    private final String typeVersion;
    private final String chorHiveDBTypeName;
    private final String chorHiveTableTypeName;
    private final String chorHiveColumnTypeName;
    private final String chorHiveProcessTypeName;

    public ChorusMetaStoreBridge(AtlasService atlasService, String typeVersion, String chorHiveDBTypeName, String chorHiveTableTypeName, String chorHiveColumnTypeName,String chorHiveProcessTypeName) {
        super(atlasService);
        this.chorHiveDBTypeName = chorHiveDBTypeName;
        this.typeVersion = typeVersion;
        this.chorHiveTableTypeName = chorHiveTableTypeName;
        this.chorHiveColumnTypeName = chorHiveColumnTypeName;
        this.chorHiveProcessTypeName = chorHiveProcessTypeName;
    }

//    @Override
    public List<AtlasEnumDef> enums_() {
        return ImmutableList.of(securityLevel(), dataUpdateFrequency(),
                                dataType(), businessField()
        );
    }

    @Override
    public List<AtlasEntityDef> classes() {
        return ImmutableList.of(chorusHiveDB(), chorusHiveTable(), chorusHiveColumn(),chorusHiveProcess());
    }

    private AtlasEntityDef chorusHiveDB() {
        return new AtlasEntityDef(chorHiveDBTypeName, "", typeVersion, Arrays.asList(
                AtlasTypeUtil.createRequiredAttrDef("createTime", AtlasBaseTypeDef.ATLAS_TYPE_DATE),
                AtlasTypeUtil.createRequiredAttrDef("createUser", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("createUserId", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("project", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("projectName", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("projectId", AtlasBaseTypeDef.ATLAS_TYPE_LONG),
                AtlasTypeUtil.createUniqueRequiredAttrDef("unique", AtlasBaseTypeDef.ATLAS_TYPE_STRING)
        ), ImmutableSet.of(TYPES_HIVE_DB));
    }

    private AtlasEntityDef chorusHiveTable() {
        return new AtlasEntityDef(chorHiveTableTypeName, "", typeVersion, Arrays.asList(
                AtlasTypeUtil.createRequiredAttrDef("createUser", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("createUserId", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("project", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("projectName", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
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
                AtlasTypeUtil.createOptionalAttrDef("locationUrl",AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createUniqueRequiredAttrDef("unique", AtlasBaseTypeDef.ATLAS_TYPE_STRING)
        ), ImmutableSet.of(TYPES_HIVE_TABLE));
    }

    private AtlasEntityDef chorusHiveColumn() {
        return new AtlasEntityDef(chorHiveColumnTypeName, "", typeVersion, Arrays.asList(
                AtlasTypeUtil.createRequiredAttrDef("project", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("projectName", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("projectId", AtlasBaseTypeDef.ATLAS_TYPE_LONG),
                AtlasTypeUtil.createOptionalAttrDef("statusCode", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
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
                AtlasTypeUtil.createUniqueRequiredAttrDef("unique", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createOptionalAttrDef("order", AtlasBaseTypeDef.ATLAS_TYPE_INT)
        ), ImmutableSet.of(TYPES_HIVE_COLUMN));
    }

    private AtlasEntityDef chorusHiveProcess(){
        return new AtlasEntityDef(chorHiveProcessTypeName, "", TYPE_VERSION, Arrays.asList(
                AtlasTypeUtil.createRequiredAttrDef("project", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("projectName", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("projectId", AtlasBaseTypeDef.ATLAS_TYPE_LONG),
                AtlasTypeUtil.createRequiredAttrDef("moduleName", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("jobName", AtlasBaseTypeDef.ATLAS_TYPE_STRING),
                AtlasTypeUtil.createRequiredAttrDef("jobId", AtlasBaseTypeDef.ATLAS_TYPE_LONG),
                AtlasTypeUtil.createRequiredAttrDef("instanceId", AtlasBaseTypeDef.ATLAS_TYPE_LONG),
                AtlasTypeUtil.createRequiredAttrDef("stepName", AtlasBaseTypeDef.ATLAS_TYPE_STRING)
        ), ImmutableSet.of(TYPES_HIVE_PROCESS));

    }

    private AtlasEnumDef businessField() {
        return new AtlasEnumDef(TYPES_CHOR_SECURITY_LEVEL, "安全级别", typeVersion, Arrays.asList(
                new AtlasEnumDef.AtlasEnumElementDef("A", "A", 1),
                new AtlasEnumDef.AtlasEnumElementDef("B", "B", 2),
                new AtlasEnumDef.AtlasEnumElementDef("C", "C", 3)
        ));
    }

    private AtlasEnumDef dataType() {
        return new AtlasEnumDef(TYPES_CHOR_DATA_UPDATE_FREQUENCY, "时效性", typeVersion, Arrays.asList(
                new AtlasEnumDef.AtlasEnumElementDef("REALTIME", "实时", 1),
                new AtlasEnumDef.AtlasEnumElementDef("DAILY", "按天", 2)
        ));
    }

    private AtlasEnumDef dataUpdateFrequency() {
        return new AtlasEnumDef(TYPES_CHOR_DATA_TYPE, "表类型", typeVersion, Arrays.asList(
                new AtlasEnumDef.AtlasEnumElementDef("BASIC", "基础表", 1),
                new AtlasEnumDef.AtlasEnumElementDef("DIMENSION", "维度表", 2),
                new AtlasEnumDef.AtlasEnumElementDef("REALTIME", "实时表", 3)
        ));
    }

    private AtlasEnumDef securityLevel() {
        return new AtlasEnumDef(TYPES_CHOR_BUSINESS_FIELD, "数据域", typeVersion, Arrays.asList(
                new AtlasEnumDef.AtlasEnumElementDef("USER", "用户域", 1),
                new AtlasEnumDef.AtlasEnumElementDef("ORDER", "订单域", 2),
                new AtlasEnumDef.AtlasEnumElementDef("LOAN", "贷款域", 3)
        ));
    }

    public static void main(String[] args) throws Exception {

        Configuration atlasConf = ApplicationProperties.get();
        String[] atlasEndpoint = atlasConf.getStringArray(ATLAS_ENDPOINT);
        if (atlasEndpoint == null || atlasEndpoint.length == 0) {
            atlasEndpoint = new String[]{DEFAULT_DGI_URL};
        }
        AtlasClientV2 atlasClient;

        if (!AuthenticationUtil.isKerberosAuthenticationEnabled()) {
            String[] basicAuthUsernamePassword = AuthenticationUtil.getBasicAuthenticationInput();
            atlasClient = new AtlasClientV2(atlasEndpoint, basicAuthUsernamePassword);
        } else {
            UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
            atlasClient = new AtlasClientV2(ugi, ugi.getShortUserName(), atlasEndpoint);
        }

        ChorusMetaStoreBridge chorusMetaStoreBridge = new ChorusMetaStoreBridge(
                new AtlasServiceImpl(atlasClient),
                TYPE_VERSION,
                TYPES_CHOR_HIVE_DB,
                TYPES_CHOR_HIVE_TABLE,
                TYPES_CHOR_HIVE_COLUMN,
                TYPES_CHOR_HIVE_PROCESS);
        chorusMetaStoreBridge.importMetadata();
    }
}
