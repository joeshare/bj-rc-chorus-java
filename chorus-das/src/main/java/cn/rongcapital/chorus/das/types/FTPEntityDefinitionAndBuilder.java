package cn.rongcapital.chorus.das.types;

import cn.rongcapital.chorus.das.util.RandomUtil;
import cn.rongcapital.chorus.governance.atlas.types.AbstractAtlasEntityDefinitionAndBuilder;
import cn.rongcapital.chorus.governance.atlas.types.AtlasEntityAttributeDef;
import cn.rongcapital.chorus.governance.atlas.types.AtlasEntityTypeDef;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.commons.collections.MapUtils;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

import static org.apache.atlas.model.typedef.AtlasBaseTypeDef.ATLAS_TYPE_STRING;

/**
 * Created by Athletics on 2017/9/6.
 */
@Configuration
@AtlasEntityTypeDef(name = FTPEntityDefinitionAndBuilder.FTP_TYPE, description = "ftp meta data", superTypes = {"Referenceable", "Asset"})
public class FTPEntityDefinitionAndBuilder extends AbstractAtlasEntityDefinitionAndBuilder {
    public static final String FTP_TYPE         = "chor_ftp";

    public static final String NAME             =   "name";
    public static final String PORT             =   "port";
    public static final String HOST             =   "host";
    public static final String USERNAME         =   "userName";
    public static final String PATH             =   "path";
    public static final String PASSWORD         =   "password";
    public static final String CONNECTMODE      =   "connectMode";
    public static final String URL              =   "url";
    public static final String CREATE_TIME      =   "createTime";

    public static final String unique_template  = "ftp://%s:%s@%s:%s/%s";//   ftp://username:password@host:password/path

    @Override
    protected void preBuild(AtlasEntity entity) {
        unique(entity,String.format(unique_template,userName(entity),password(entity),host(entity),port(entity),path(entity)));
        qualifiedName(entity, RandomUtil.getQualifiedName());
    }

    @Override
    protected String getTypeName() {
        return FTP_TYPE;
    }

    @AtlasEntityAttributeDef(name = NAME, type = ATLAS_TYPE_STRING, required = true)
    public void name(AtlasEntity entity, String name){
        setAttribute(entity, NAME, name);
    }
    @AtlasEntityAttributeDef(name = HOST, type = ATLAS_TYPE_STRING, required = true)
    public void host(AtlasEntity entity, String host){
        setAttribute(entity, HOST, host);
    }

    @AtlasEntityAttributeDef(name = PORT, type = ATLAS_TYPE_STRING, required = true)
    public void port(AtlasEntity entity, String port){
        setAttribute(entity, PORT, port);
    }

    @AtlasEntityAttributeDef(name = USERNAME, type = ATLAS_TYPE_STRING, required = true)
    public void userName(AtlasEntity entity, String userName){
        setAttribute(entity, USERNAME, userName);
    }

    @AtlasEntityAttributeDef(name = PATH, type = ATLAS_TYPE_STRING, required = true)
    public void path(AtlasEntity entity, String userName){
        setAttribute(entity, PATH, userName);
    }

    @AtlasEntityAttributeDef(name = PASSWORD, type = ATLAS_TYPE_STRING, required = true)
    public void password(AtlasEntity entity, String userName){
        setAttribute(entity, PASSWORD, userName);
    }

    @AtlasEntityAttributeDef(name = CONNECTMODE, type = ATLAS_TYPE_STRING, required = true)
    public void connectMode(AtlasEntity entity, String userName){
        setAttribute(entity, CONNECTMODE, userName);
    }

    @AtlasEntityAttributeDef(name = URL, type = ATLAS_TYPE_STRING, required = true)
    public void url(AtlasEntity entity, String userName){
        setAttribute(entity, URL, userName);
    }

    public String userName(@NotNull AtlasEntity entity){
        return MapUtils.getString(entity.getAttributes(), USERNAME);
    }

    public String password(@NotNull AtlasEntity entity){
        return MapUtils.getString(entity.getAttributes(), PASSWORD);
    }

    public String host(@NotNull AtlasEntity entity){
        return MapUtils.getString(entity.getAttributes(), HOST);
    }

    public String port(@NotNull AtlasEntity entity){
        return MapUtils.getString(entity.getAttributes(), PORT);
    }

    public String path(@NotNull AtlasEntity entity){
        return MapUtils.getString(entity.getAttributes(), PATH);
    }
}
