package cn.rongcapital.chorus.das.types;

import cn.rongcapital.chorus.das.util.RandomUtil;
import cn.rongcapital.chorus.governance.atlas.types.AbstractAtlasEntityDefinitionAndBuilder;
import cn.rongcapital.chorus.governance.atlas.types.AtlasEntityAttributeDef;
import cn.rongcapital.chorus.governance.atlas.types.AtlasEntityTypeDef;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.typedef.AtlasBaseTypeDef;
import org.apache.commons.collections.MapUtils;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;

import static org.apache.atlas.model.typedef.AtlasBaseTypeDef.ATLAS_TYPE_INT;
import static org.apache.atlas.model.typedef.AtlasBaseTypeDef.ATLAS_TYPE_STRING;

/**
 * db
 *
 * @author yimin
 */
@Configuration
@AtlasEntityTypeDef(name = MySQLDBEntityDefinitionAndBuilder.MYSQL_DB, description = "mysql database meta data", superTypes = {"Referenceable", "Asset"})
public class MySQLDBEntityDefinitionAndBuilder extends AbstractAtlasEntityDefinitionAndBuilder {
    public static final String MYSQL_DB = "chor_mysql_db";

    public static final String NAME           = "name";
    public static final String URL            = "url";
    public static final String HOST           = "host";
    public static final String PORT           = "port";
    public static final String DATA_BASE_NAME = "dataBaseName";
    public static final String CONNECT_USER   = "connectUser";
    public static final String PARAMETERS     = "parameters";
    public static final String DESCRIPTION    = "description";
    public static final String PROJECT_NAME   = "projectName";
    public static final String PROJECT_ID     = "projectId";
    public static final String PROJECT        = "project";
    public static final String CREATE_USER_ID = "createUserId";
    public static final String CREATE_USER    = "createUser";
    public static final String CREATE_TIME    = "createTime";
    public static final String LAST_UPDATE_TIME = "lastUpdateTime";

    public static final String unique_template="%s,%s,%s,%s";//<projectId>,<resource_name>,<connect_user>,<url>

    @Override
    protected void preBuild(AtlasEntity entity) {
        unique(entity,String.format(unique_template,projectId(entity),name(entity),connectUser(entity),url(entity)));
        qualifiedName(entity, RandomUtil.getQualifiedName());
    }

    @Override
    protected String getTypeName() {
        return MYSQL_DB;
    }

    @AtlasEntityAttributeDef(name = NAME, type = ATLAS_TYPE_STRING, required = true)
    public void name(AtlasEntity entity, String name) {
        setAttribute(entity, NAME, name);
    }

    public String name(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), NAME);
    }

    @AtlasEntityAttributeDef(name = URL, type = ATLAS_TYPE_STRING, required = true)
    public void url(AtlasEntity entity, String url) {
        setAttribute(entity, URL, url);
    }

    public String url(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), URL);
    }

    @AtlasEntityAttributeDef(name = HOST, type = ATLAS_TYPE_STRING, required = true)
    public void host(AtlasEntity entity, String host) {
        setAttribute(entity, HOST, host);
    }

    public String host(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), HOST);
    }

    @AtlasEntityAttributeDef(name = PORT, type = ATLAS_TYPE_INT, required = true)
    public void port(AtlasEntity entity, String port) {
        setAttribute(entity, PORT, port);
    }

    public String port(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), PORT);
    }

    @AtlasEntityAttributeDef(name = DATA_BASE_NAME, type = ATLAS_TYPE_STRING, required = true)
    public void dataBaseName(AtlasEntity entity, String dataBaseName) {
        setAttribute(entity, DATA_BASE_NAME, dataBaseName);
    }

    public String dataBaseName(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), DATA_BASE_NAME);
    }

    @AtlasEntityAttributeDef(name = CONNECT_USER, type = ATLAS_TYPE_STRING, required = true)
    public void connectUser(AtlasEntity entity, String user) {
        setAttribute(entity, CONNECT_USER, user);
    }

    public String connectUser(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), CONNECT_USER);
    }

    @AtlasEntityAttributeDef(name = PARAMETERS, type = ATLAS_TYPE_STRING)
    public void parmaters(AtlasEntity entity, String parmaters) {
        setAttribute(entity, PARAMETERS, parmaters);
    }

    public String parmaters(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), PARAMETERS);
    }

    @AtlasEntityAttributeDef(name = DESCRIPTION, type = ATLAS_TYPE_STRING)
    public void descritpion(AtlasEntity entity, String parmaters) {
        setAttribute(entity, DESCRIPTION, parmaters);
    }

    public String descritpion(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), DESCRIPTION);
    }

    @AtlasEntityAttributeDef(name = CREATE_USER, type = AtlasBaseTypeDef.ATLAS_TYPE_STRING, required = true)
    public void createUser(@Nonnull AtlasEntity entity, String createUser) {
        setAttribute(entity, CREATE_USER, createUser);
    }

    public String createUser(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), CREATE_USER);
    }

    @AtlasEntityAttributeDef(name = CREATE_USER_ID, type = AtlasBaseTypeDef.ATLAS_TYPE_STRING, required = true)
    public void createUserId(@Nonnull AtlasEntity entity, String createUserId) {
        setAttribute(entity, CREATE_USER_ID, createUserId);
    }

    public String createUserId(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), CREATE_USER_ID);
    }

    @AtlasEntityAttributeDef(name = PROJECT, type = AtlasBaseTypeDef.ATLAS_TYPE_STRING)
    public void project(@Nonnull AtlasEntity entity, String project) {
        setAttribute(entity, PROJECT, project);
    }

    public String project(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), PROJECT);
    }

    @AtlasEntityAttributeDef(name = PROJECT_ID, type = AtlasBaseTypeDef.ATLAS_TYPE_LONG, required = true)
    public void projectId(@Nonnull AtlasEntity entity, Long projectId) {
        setAttribute(entity, PROJECT_ID, projectId);
    }

    public Long projectId(@Nonnull AtlasEntity entity) {
        return MapUtils.getLong(entity.getAttributes(), PROJECT_ID);
    }

    @AtlasEntityAttributeDef(name = PROJECT_NAME, type = AtlasBaseTypeDef.ATLAS_TYPE_STRING)
    public void projectName(@Nonnull AtlasEntity entity, @Nonnull String projectName) {
        setAttribute(entity, PROJECT_NAME, projectName);
    }

    public String projectName(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), PROJECT_NAME);
    }
}
