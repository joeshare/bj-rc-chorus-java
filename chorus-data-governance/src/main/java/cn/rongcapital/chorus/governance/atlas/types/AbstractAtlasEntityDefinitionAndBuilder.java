package cn.rongcapital.chorus.governance.atlas.types;

import cn.rongcapital.chorus.governance.atlas.entity.AbstractAtlasAtlasEntityBuilder;
import lombok.Getter;
import lombok.Setter;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.typedef.AtlasEntityDef;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * @author yimin
 */
public abstract class AbstractAtlasEntityDefinitionAndBuilder extends AbstractAtlasAtlasEntityBuilder implements EntityTypeDefinition {
    @Setter
    @Getter
    protected AtlasEntityDef definition;

    protected void preBuild(AtlasEntity entity) {
        //DEFAULT PRE BUILD NOTHING NEED TO DO
    }

    protected void postBuild(AtlasEntity atlasEntity) {
        //DEFAULT PRE BUILD NOTHING NEED TO DO
    }

    @Override
    public AtlasEntity getEntity() {
        return new AtlasEntity(getTypeName());
    }

    protected abstract String getTypeName();

    public void createTime(@Nonnull AtlasEntity entity, Date createTime) {
        entity.setCreateTime(createTime);
    }

    public Date createTime(@Nonnull AtlasEntity entity) {
        return entity.getCreateTime();
    }

    public void updateTime(@Nonnull AtlasEntity entity, Date updateTime) {
        entity.setUpdateTime(updateTime);
    }

    public Date updateTime(@Nonnull AtlasEntity entity) {
        return entity.getUpdateTime();
    }
}
