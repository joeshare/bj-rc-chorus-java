package cn.rongcapital.chorus.governance.atlas.entity;

import cn.rongcapital.chorus.governance.atlas.types.AtlasEntityAttributeDef;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.commons.collections.MapUtils;

import javax.annotation.Nonnull;

import static org.apache.atlas.AtlasClient.REFERENCEABLE_ATTRIBUTE_NAME;
import static org.apache.atlas.model.typedef.AtlasBaseTypeDef.ATLAS_TYPE_STRING;

/**
 * @author yimin
 */
public abstract class AbstractAtlasAtlasEntityBuilder implements AtlasEntityBuilder {

    public static final String UNIQUE = "unique";

    @Override
    public AtlasEntity build(AtlasEntity entity) {
        preBuild(entity);
        postBuild(entity);
        return entity;
    }

    protected abstract void preBuild(AtlasEntity entity);

    protected abstract void postBuild(AtlasEntity atlasEntity);

    protected void setAttribute(AtlasEntity atlasEntity, String key, Object value) {
        atlasEntity.setAttribute(key, value);
    }

    public String guid(AtlasEntity entity) {
        return entity.getGuid();
    }

    public byte booleanToByte(boolean orginal) {
        return orginal ? (byte) 1 : (byte) 0;
    }

    @AtlasEntityAttributeDef(name = UNIQUE, type = ATLAS_TYPE_STRING, required = true, unique = true)
    public AbstractAtlasAtlasEntityBuilder unique(@Nonnull AtlasEntity entity, @Nonnull String unique) {
        setAttribute(entity, UNIQUE, unique);
        return this;
    }

    public AbstractAtlasAtlasEntityBuilder qualifiedName(@Nonnull AtlasEntity entity, @Nonnull Long qualifiedName) {
        setAttribute(entity, REFERENCEABLE_ATTRIBUTE_NAME, qualifiedName);
        return this;
    }

    public String unique(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), UNIQUE);
    }

    public String qualifiedName(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), REFERENCEABLE_ATTRIBUTE_NAME);
    }
}
