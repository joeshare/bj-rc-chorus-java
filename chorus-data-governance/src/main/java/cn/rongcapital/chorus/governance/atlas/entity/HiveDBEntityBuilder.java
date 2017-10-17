package cn.rongcapital.chorus.governance.atlas.entity;


import cn.rongcapital.chorus.governance.bridge.ChorusMetaStoreBridge;
import org.apache.atlas.model.instance.AtlasEntity;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * @author yimin
 */

public class HiveDBEntityBuilder extends AbstractAtlasAtlasEntityBuilder{
    public static final HiveDBEntityBuilder INSTANCE = new HiveDBEntityBuilder();

    private HiveDBEntityBuilder() {}

    @Override
    public AtlasEntity getEntity() {
        return new AtlasEntity(ChorusMetaStoreBridge.TYPES_CHOR_HIVE_DB);
    }

    @Override
    protected void preBuild(@Nonnull AtlasEntity entity) {
        createTime(entity, new Date(System.currentTimeMillis()));
    }

    @Override
    protected void postBuild(@Nonnull AtlasEntity entity) {
        Stream.of("name", "owner", "createTime", "createUser", "createUserId", "project", "projectId","projectName")
              .forEach(requiredAttr -> notNull(entity.getAttribute(requiredAttr), "require attr '" + requiredAttr + "' cannot be null"));
    }

    public HiveDBEntityBuilder name(@Nonnull AtlasEntity entity, @Nonnull String name) {
        setAttribute(entity, "name", name);
        return this;
    }

    public HiveDBEntityBuilder descritpion(@Nonnull AtlasEntity entity, String description) {
        setAttribute(entity, "description", description);
        return this;
    }

    public HiveDBEntityBuilder locationUrl(@Nonnull AtlasEntity entity, String locationUrl) {
        setAttribute(entity, "location", locationUrl);
        return this;
    }

    public HiveDBEntityBuilder owner(@Nonnull AtlasEntity entity, @Nonnull String owner) {
        setAttribute(entity, "owner", owner);
        return this;
    }

    public HiveDBEntityBuilder ownerType(@Nonnull AtlasEntity entity, int ownerType) {
        setAttribute(entity, "ownerType", ownerType);
        return this;
    }

    public HiveDBEntityBuilder clusterName(@Nonnull AtlasEntity entity, String clusterName) {
        setAttribute(entity, "clusterName", clusterName);
        return this;
    }

    public HiveDBEntityBuilder parameters(@Nonnull AtlasEntity entity, Map<String, String> parameters) {
        setAttribute(entity, "parameters", parameters);
        return this;
    }

    private HiveDBEntityBuilder createTime(@Nonnull AtlasEntity entity, @Nonnull Date createTime) {
        setAttribute(entity, "createTime", createTime);
        return this;
    }

    public HiveDBEntityBuilder createUser(@Nonnull AtlasEntity entity, @Nonnull String createUser) {
        setAttribute(entity, "createUser", createUser);
        return this;
    }

    public HiveDBEntityBuilder createUserId(@Nonnull AtlasEntity entity, @Nonnull String createUserId) {
        setAttribute(entity, "createUserId", createUserId);
        return this;
    }

    public HiveDBEntityBuilder project(@Nonnull AtlasEntity entity, @Nonnull String project) {
        setAttribute(entity, "project", project);
        return this;
    }

    public HiveDBEntityBuilder projectId(@Nonnull AtlasEntity entity, @Nonnull Long projectId) {
        setAttribute(entity, "projectId", projectId);
        return this;
    }

    public HiveDBEntityBuilder projectName(@Nonnull AtlasEntity entity, @Nonnull String projectName) {
        setAttribute(entity, "projectName", projectName);
        return this;
    }


}
