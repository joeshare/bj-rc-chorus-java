package cn.rongcapital.chorus.governance.atlas.entity;

import org.apache.atlas.model.instance.AtlasEntity;

/**
 * @author yimin
 */
public interface AtlasEntityBuilder {
    AtlasEntity getEntity();

    AtlasEntity build(AtlasEntity entity);
}
