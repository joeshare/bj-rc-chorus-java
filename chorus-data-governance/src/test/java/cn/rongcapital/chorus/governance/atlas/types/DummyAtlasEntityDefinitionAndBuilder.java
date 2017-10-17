package cn.rongcapital.chorus.governance.atlas.types;

import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.typedef.AtlasBaseTypeDef;
import org.apache.commons.collections.MapUtils;

/**
 * @author yimin
 */

@AtlasEntityTypeDef(name = DummyAtlasEntityDefinitionAndBuilder.DUMMY, description = "This is just Dummy", superTypes = {"SuperTypeOne", "SuperTypeTow"})
public class DummyAtlasEntityDefinitionAndBuilder extends AbstractAtlasEntityDefinitionAndBuilder {

    public static final String DUMMY           = "Dummy";
    public static final String NAME            = "name";
    public static final String REQUIRED_UNIQUE = "requiredUnique";
    public static final String REQUIRED        = "required";


    @AtlasEntityAttributeDef(name = NAME, type = AtlasBaseTypeDef.ATLAS_TYPE_STRING)
    public void name(AtlasEntity atlasEntity, String name) {
        setAttribute(atlasEntity, NAME, name);
    }

    public void name(AtlasEntity atlasEntity) {
        MapUtils.getString(atlasEntity.getAttributes(), NAME);
    }

    @AtlasEntityAttributeDef(name = REQUIRED_UNIQUE, required = true, unique = true, type = AtlasBaseTypeDef.ATLAS_TYPE_STRING)
    public void requiredUnique(AtlasEntity atlasEntity, String requiredUnique) {
        setAttribute(atlasEntity, REQUIRED_UNIQUE, requiredUnique);
    }

    public void requiredUnique(AtlasEntity atlasEntity) {
        MapUtils.getString(atlasEntity.getAttributes(), REQUIRED_UNIQUE);
    }

    @AtlasEntityAttributeDef(name = REQUIRED, required = true, type = AtlasBaseTypeDef.ATLAS_TYPE_STRING)
    public void required(AtlasEntity atlasEntity, String required) {
        setAttribute(atlasEntity, REQUIRED, required);
    }

    public void required(AtlasEntity atlasEntity) {
        MapUtils.getString(atlasEntity.getAttributes(), REQUIRED);
    }

    @Override
    protected String getTypeName() {
        return DUMMY;
    }
}
