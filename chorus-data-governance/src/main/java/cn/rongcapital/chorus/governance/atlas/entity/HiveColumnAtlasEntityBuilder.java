package cn.rongcapital.chorus.governance.atlas.entity;

import cn.rongcapital.chorus.governance.bridge.ChorusMetaStoreBridge;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.type.AtlasTypeUtil;
import org.apache.commons.collections.MapUtils;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * @author yimin
 */
public class HiveColumnAtlasEntityBuilder extends AbstractAtlasAtlasEntityBuilder {
    public static final HiveColumnAtlasEntityBuilder INSTANCE = new HiveColumnAtlasEntityBuilder();
    
    private HiveColumnAtlasEntityBuilder() {
    }
    
    @Override
    public AtlasEntity getEntity() {
        return new AtlasEntity(ChorusMetaStoreBridge.TYPES_CHOR_HIVE_COLUMN);
    }
    
    @Override
    protected void preBuild(@Nonnull AtlasEntity entity) {
//        final Date currentDateTime = new Date(System.currentTimeMillis());
        long currentTimeMills = System.currentTimeMillis();
        if (createTime(entity) == null) createTime(entity, currentTimeMills);
        if (updateTime(entity) == null) updateTime(entity, currentTimeMills);
        if (isPartitionKey(entity) == null) isPartitionKey(entity, false);
    }
    
    @Override
    protected void postBuild(@Nonnull AtlasEntity entity) {
        Stream.of("name", "type", "project", "projectId", "projectName", "createTime")
                .forEach(requiredAttr -> notNull(entity.getAttribute(requiredAttr), "require attr '" + requiredAttr + "' cannot be null"));
    }
    
    public HiveColumnAtlasEntityBuilder guid(@Nonnull AtlasEntity entity, String guid) {
        setAttribute(entity, "guid", guid);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder name(@Nonnull AtlasEntity entity, String name) {
        setAttribute(entity, "name", name);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder type(@Nonnull AtlasEntity entity, String type) {
        setAttribute(entity, "type", type);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder comment(@Nonnull AtlasEntity entity, String comment) {
        setAttribute(entity, "comment", comment);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder table(@Nonnull AtlasEntity entity, AtlasEntity table) {
        setAttribute(entity, "table", AtlasTypeUtil.getAtlasObjectId(table));
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder project(@Nonnull AtlasEntity entity, String project) {
        setAttribute(entity, "project", project);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder projectId(@Nonnull AtlasEntity entity, Long projectId) {
        setAttribute(entity, "projectId", projectId);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder projectName(@Nonnull AtlasEntity entity, @Nonnull String projectName) {
        setAttribute(entity, "projectName", projectName);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder statusCode(@Nonnull AtlasEntity entity, String statusCode) {
        setAttribute(entity, "statusCode", statusCode);
        entity.setStatus(AtlasEntity.Status.ACTIVE);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder length(@Nonnull AtlasEntity entity, String length) {
        setAttribute(entity, "length", length);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder precision(@Nonnull AtlasEntity entity, String precision) {
        setAttribute(entity, "precision", precision);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder isKey(@Nonnull AtlasEntity entity, boolean isKey) {
        setAttribute(entity, "isKey", isKey);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder isForeignKey(@Nonnull AtlasEntity entity, boolean isForeignKey) {
        setAttribute(entity, "isForeignKey", isForeignKey);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder isNull(@Nonnull AtlasEntity entity, boolean isNull) {
        setAttribute(entity, "isNull", isNull);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder isIndex(@Nonnull AtlasEntity entity, boolean isIndex) {
        setAttribute(entity, "isIndex", isIndex);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder isPartitionKey(@Nonnull AtlasEntity entity, boolean isPartitionKey) {
        setAttribute(entity, "isPartitionKey", isPartitionKey);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder securityLevel(@Nonnull AtlasEntity entity, String securityLevel) {
        setAttribute(entity, "securityLevel", securityLevel);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder createTime(@Nonnull AtlasEntity entity, @Nonnull Long createTime) {
        setAttribute(entity, "createTime", createTime);
        entity.setCreateTime(new Date(createTime));
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder updateTime(@Nonnull AtlasEntity entity, @Nonnull Long updateTime) {
        setAttribute(entity, "updateTime", updateTime);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder owner(AtlasEntity entity, String owner) {
        setAttribute(entity, "owner", owner);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder position(AtlasEntity entity, int position) {
        setAttribute(entity, "position", position);
        return this;
    }
    
    public HiveColumnAtlasEntityBuilder columnOrder(AtlasEntity entity, int columnOrder) {
        setAttribute(entity, "columnOrder", columnOrder);
        return this;
    }
    
    public String name(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "name");
    }
    
    public String type(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "type");
    }
    
    public String comment(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "comment");
    }
    
    public String table(@Nonnull AtlasEntity entity) { // return entity's guid
        return entity.getAttribute("table") == null ? "" : (String) ((HashMap) entity.getAttribute("table")).get("guid");
    }
    
    public String project(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "project");
    }
    
    public Long projectId(@Nonnull AtlasEntity entity) {
        return MapUtils.getLong(entity.getAttributes(), "projectId");
    }
    
    public String projectName(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "projectName");
    }
    
    public String statusCode(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "statusCode");
    }
    
    public String length(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "length");
    }
    
    public String precision(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "precision");
    }
    
    public boolean isKey(@Nonnull AtlasEntity entity) {
        return MapUtils.getBooleanValue(entity.getAttributes(), "isKey");
    }
    
    public boolean isForeignKey(@Nonnull AtlasEntity entity) {
        return MapUtils.getBooleanValue(entity.getAttributes(), "isForeignKey");
    }
    
    public boolean isNull(@Nonnull AtlasEntity entity) {
        return MapUtils.getBooleanValue(entity.getAttributes(), "isNull");
    }
    
    public boolean isIndex(@Nonnull AtlasEntity entity) {
        return MapUtils.getBooleanValue(entity.getAttributes(), "isIndex");
    }
    
    public Boolean isPartitionKey(@Nonnull AtlasEntity entity) {
        return MapUtils.getBooleanValue(entity.getAttributes(), "isPartitionKey");
    }
    
    public String securityLevel(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "securityLevel");
    }
    
    public String owner(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "owner");
    }
    
    public int position(@Nonnull AtlasEntity entity) {
        return MapUtils.getIntValue(entity.getAttributes(), "position");
    }
    
    public Date createTime(@Nonnull AtlasEntity entity) {
        return entity.getAttribute("createTime") == null ? null : new Date((Long) entity.getAttribute("createTime"));
    }
    
    public Date updateTime(@Nonnull AtlasEntity entity) {
        return entity.getAttribute("updateTime") == null ? null : new Date((Long) entity.getAttribute("updateTime"));
    }
    
    public Integer columnOrder(@Nonnull AtlasEntity entity) {
        return MapUtils.getInteger(entity.getAttributes(), "columnOrder");
    }
    
}
