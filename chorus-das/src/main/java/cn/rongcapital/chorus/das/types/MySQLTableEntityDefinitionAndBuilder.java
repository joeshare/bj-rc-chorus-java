package cn.rongcapital.chorus.das.types;

import cn.rongcapital.chorus.das.util.RandomUtil;
import cn.rongcapital.chorus.governance.atlas.types.AbstractAtlasEntityDefinitionAndBuilder;
import cn.rongcapital.chorus.governance.atlas.types.AtlasEntityAttributeDef;
import cn.rongcapital.chorus.governance.atlas.types.AtlasEntityTypeDef;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.typedef.AtlasBaseTypeDef;
import org.apache.atlas.type.AtlasTypeUtil;
import org.apache.commons.collections.MapUtils;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

import static org.apache.atlas.model.typedef.AtlasBaseTypeDef.ATLAS_TYPE_DATASET;
import static org.apache.atlas.model.typedef.AtlasBaseTypeDef.ATLAS_TYPE_STRING;

/**
 * @author yimin
 */
@Configuration
@AtlasEntityTypeDef(name = MySQLTableEntityDefinitionAndBuilder.MYSQL_TABLE, description = "mysql meta data", superTypes = {ATLAS_TYPE_DATASET})
public class MySQLTableEntityDefinitionAndBuilder extends AbstractAtlasEntityDefinitionAndBuilder {
    public static final String MYSQL_TABLE = "chor_mysql_table";

    public static final String URL            = "url";
    public static final String NAME           = "name";
    public static final String DATABASE       = "database";
    public static final String PROJECT_ID     = "projectId";
    public static final String CONNECT_USER   = "connectUser";
    public static final String CREATE_TIME    = "createTime";

    public static final String unique_template="%s,%s,%s,%s";//<projectId>:<connect_user>:<url>:<tableName>

    @AtlasEntityAttributeDef(name = NAME, type = ATLAS_TYPE_STRING, required = true)
    public void name(@Nonnull AtlasEntity entity, String name) {
        setAttribute(entity, NAME, name);
    }

    public String name(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), NAME);
    }

    @AtlasEntityAttributeDef(name = DATABASE, type = ATLAS_TYPE_STRING, required = true)
    public void database(@Nonnull AtlasEntity entity, @Nonnull AtlasEntity databaseEntity) {
        setAttribute(entity, DATABASE, AtlasTypeUtil.getAtlasObjectId(databaseEntity));
    }

    public @Nullable
    String database(@Nonnull AtlasEntity entity) {
        Object attributes = entity.getAttribute(DATABASE);
        return attributes == null ? null : MapUtils.getString((HashMap) attributes, "guid");
    }

    @AtlasEntityAttributeDef(name = PROJECT_ID, type = AtlasBaseTypeDef.ATLAS_TYPE_LONG, required = true)
    public void projectId(@Nonnull AtlasEntity entity, Long projectId) {
        setAttribute(entity, PROJECT_ID, projectId);
    }

    public Long projectId(@Nonnull AtlasEntity entity) {
        return MapUtils.getLong(entity.getAttributes(), PROJECT_ID);
    }

    @AtlasEntityAttributeDef(name = CONNECT_USER, type = ATLAS_TYPE_STRING, required = true)
    public void connectUser(AtlasEntity entity, String user) {
        setAttribute(entity, CONNECT_USER, user);
    }

    public String connectUser(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), CONNECT_USER);
    }

    @AtlasEntityAttributeDef(name = URL, type = ATLAS_TYPE_STRING, required = true)
    public void url(AtlasEntity entity, String url) {
        setAttribute(entity, URL, url);
    }

    public String url(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), URL);
    }


    @Override
    protected String getTypeName() {
        return MYSQL_TABLE;
    }

    @Override
    protected void preBuild(AtlasEntity entity) {
        unique(entity,String.format(unique_template,projectId(entity),connectUser(entity),url(entity),name(entity)));
        qualifiedName(entity, RandomUtil.getQualifiedName());
    }
}
