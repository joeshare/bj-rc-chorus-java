package cn.rongcapital.chorus.governance.atlas.entity;

import cn.rongcapital.chorus.governance.bridge.ChorusMetaStoreBridge;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasObjectId;
import org.apache.atlas.type.AtlasTypeUtil;
import org.apache.commons.collections.MapUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * @author yimin
 */
public class HiveTableAtlasEntityBuilder extends AbstractAtlasAtlasEntityBuilder {
    public static final HiveTableAtlasEntityBuilder INSTANCE = new HiveTableAtlasEntityBuilder();

    private HiveTableAtlasEntityBuilder() { }

    @Override
    public AtlasEntity getEntity() {
        return new AtlasEntity(ChorusMetaStoreBridge.TYPES_CHOR_HIVE_TABLE);
    }

    @Override
    protected void preBuild(@Nonnull AtlasEntity entity) {
        final Date date = new Date(System.currentTimeMillis());
        if (createTime(entity) == null) createTime(entity, date);
        if (lastAccessTime(entity) == null) lastAccessTime(entity, date);
    }

    @Override
    protected void postBuild(@Nonnull AtlasEntity entity) {
        Stream.of("name", "db", "owner", "createTime", "createUser", "createUserId", "project", "projectId","projectName")
              .forEach(requiredAttr -> notNull(entity.getAttribute(requiredAttr), "require attr '" + requiredAttr + "' cannot be null"));
    }

    public HiveTableAtlasEntityBuilder name(@Nonnull AtlasEntity entity, String name) {
        setAttribute(entity, "name", name);
        return this;
    }

    public HiveTableAtlasEntityBuilder db(@Nonnull AtlasEntity entity, AtlasEntity db) {
        setAttribute(entity, "db", AtlasTypeUtil.getAtlasObjectId(db));
        return this;
    }

    public HiveTableAtlasEntityBuilder owner(@Nonnull AtlasEntity entity, String owner) {
        setAttribute(entity, "owner", owner);
        return this;
    }

    public HiveTableAtlasEntityBuilder createTime(@Nonnull AtlasEntity entity, Date createTime) {
        setAttribute(entity, "createTime", createTime);
        return this;
    }

    public HiveTableAtlasEntityBuilder lastAccessTime(@Nonnull AtlasEntity entity, Date lastAccessTime) {
        setAttribute(entity, "lastAccessTime", lastAccessTime);
        return this;
    }

    public HiveTableAtlasEntityBuilder comment(@Nonnull AtlasEntity entity, String comment) {
        setAttribute(entity, "comment", comment);
        return this;
    }

    public HiveTableAtlasEntityBuilder retention(@Nonnull AtlasEntity entity, int retention) {
        setAttribute(entity, "retention", retention);
        return this;
    }

    public HiveTableAtlasEntityBuilder sd(@Nonnull AtlasEntity entity, AtlasEntity sd) {
        setAttribute(entity, "sd", AtlasTypeUtil.getAtlasObjectId(sd));
        return this;
    }

    public HiveTableAtlasEntityBuilder partitionKeys(@Nonnull AtlasEntity entity, List<AtlasEntity> partitionKeys) {
        setAttribute(entity, "partitionKeys", AtlasTypeUtil.toObjectIds(partitionKeys));
        return this;
    }

    public HiveTableAtlasEntityBuilder columns(@Nonnull AtlasEntity entity, List<AtlasEntity> columns) {
        setAttribute(entity, "columns", AtlasTypeUtil.toObjectIds(columns));
        return this;
    }

    public HiveTableAtlasEntityBuilder aliases(@Nonnull AtlasEntity entity, String aliases) {
        setAttribute(entity, "aliases", aliases);
        return this;
    }

    public HiveTableAtlasEntityBuilder parameters(@Nonnull AtlasEntity entity, Map<String, String> parameters) {
        setAttribute(entity, "parameters", parameters);
        return this;
    }

    public HiveTableAtlasEntityBuilder viewOriginalText(@Nonnull AtlasEntity entity, String viewOriginalText) {
        setAttribute(entity, "viewOriginalText", viewOriginalText);
        return this;
    }

    public HiveTableAtlasEntityBuilder viewExpandedText(@Nonnull AtlasEntity entity, String viewExpandedText) {
        setAttribute(entity, "viewExpandedText", viewExpandedText);
        return this;
    }

    public HiveTableAtlasEntityBuilder tableType(@Nonnull AtlasEntity entity, String tableType) {
        setAttribute(entity, "tableType", tableType);
        return this;
    }

    public HiveTableAtlasEntityBuilder temporary(@Nonnull AtlasEntity entity, boolean temporary) {
        setAttribute(entity, "temporary", temporary);
        return this;
    }

    public HiveTableAtlasEntityBuilder createUser(@Nonnull AtlasEntity entity, String createUser) {
        setAttribute(entity, "createUser", createUser);
        return this;
    }

    public HiveTableAtlasEntityBuilder createUserId(@Nonnull AtlasEntity entity, String createUserId) {
        setAttribute(entity, "createUserId", createUserId);
        return this;
    }

    public HiveTableAtlasEntityBuilder project(@Nonnull AtlasEntity entity, String project) {
        setAttribute(entity, "project", project);
        return this;
    }

    public HiveTableAtlasEntityBuilder projectId(@Nonnull AtlasEntity entity, Long projectId) {
        setAttribute(entity, "projectId", projectId);
        return this;
    }

    public HiveTableAtlasEntityBuilder projectName(@Nonnull AtlasEntity entity, @Nonnull String projectName) {
        setAttribute(entity, "projectName", projectName);
        return this;
    }

    public HiveTableAtlasEntityBuilder statusCode(@Nonnull AtlasEntity entity, String statusCode) {
        setAttribute(entity, "statusCode", statusCode);
        return this;
    }

    public HiveTableAtlasEntityBuilder code(@Nonnull AtlasEntity entity, String code) {
        setAttribute(entity, "code", code);
        return this;
    }

    public HiveTableAtlasEntityBuilder businessField(@Nonnull AtlasEntity entity, String businessField) {
        setAttribute(entity, "businessField", businessField);
        return this;
    }

    public HiveTableAtlasEntityBuilder dataType(@Nonnull AtlasEntity entity, String dataType) {
        setAttribute(entity, "dataType", dataType);
        return this;
    }

    public HiveTableAtlasEntityBuilder dataUpdateFrequency(@Nonnull AtlasEntity entity, String dataUpdateFrequency) {
        setAttribute(entity, "dataUpdateFrequency", dataUpdateFrequency);
        return this;
    }

    public HiveTableAtlasEntityBuilder snapshot(@Nonnull AtlasEntity entity, boolean snapshot) {
        setAttribute(entity, "snapshot", snapshot);
        return this;
    }

    public HiveTableAtlasEntityBuilder sla(@Nonnull AtlasEntity entity, String sla) {
        setAttribute(entity, "sla", sla);
        return this;
    }

    public HiveTableAtlasEntityBuilder open(@Nonnull AtlasEntity entity, boolean open) {
        setAttribute(entity, "open", open);
        return this;
    }

    public HiveTableAtlasEntityBuilder securityLevel(@Nonnull AtlasEntity entity, String securityLevel) {
        setAttribute(entity, "securityLevel", securityLevel);
        return this;
    }

    public HiveTableAtlasEntityBuilder locationUrl(@Nonnull AtlasEntity entity, String locationUrl) {
        setAttribute(entity, "locationUrl", locationUrl);
        return this;
    }

    public String name(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "name");

    }

    public AtlasObjectId db(@Nonnull AtlasEntity entity) {
        return (AtlasObjectId) entity.getAttribute("db");//TODO

    }

    public String owner(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "owner");

    }

    public Date createTime(@Nonnull AtlasEntity entity) {
        return entity.getAttribute("createTime")== null ? null : new Date((Long)entity.getAttribute("createTime"));
    }

    public Date lastAccessTime(@Nonnull AtlasEntity entity) {
        return entity.getAttribute("lastAccessTime")== null ? null : new Date((Long)entity.getAttribute("lastAccessTime"));//TODO

    }

    public String comment(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "comment");

    }

    public int retention(@Nonnull AtlasEntity entity) {
        return MapUtils.getIntValue(entity.getAttributes(), "retention");

    }

    public AtlasObjectId sd(@Nonnull AtlasEntity entity) {
        return (AtlasObjectId) entity.getAttribute("sd");//todo

    }

    public List<HashMap<String, Object>> partitionKeys(@Nonnull AtlasEntity entity) {
        return (List<HashMap<String, Object>>) entity.getAttribute("partitionKeys");

    }

    public List<HashMap<String, Object>> columns(@Nonnull AtlasEntity entity) {
        return (List<HashMap<String, Object>>) entity.getAttribute("columns");

    }

    public String aliases(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "aliases");

    }

    public Map<String, String> parameters(@Nonnull AtlasEntity entity) {
        return (Map<String, String>) entity.getAttribute("parameters");

    }

    public String viewOriginalText(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "viewOriginalText");

    }

    public String viewExpandedText(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "viewExpandedText");

    }

    public String tableType(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "tableType");

    }

    public boolean temporary(@Nonnull AtlasEntity entity) {
        return MapUtils.getBooleanValue(entity.getAttributes(), "temporary");

    }

    public String createUser(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "createUser");

    }

    public String createUserId(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "createUserId");

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

    public String code(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "code");

    }

    public String businessField(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "businessField");

    }

    public String dataType(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "dataType");

    }

    public String dataUpdateFrequency(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "dataUpdateFrequency");

    }

    public boolean snapshot(@Nonnull AtlasEntity entity) {
        return MapUtils.getBooleanValue(entity.getAttributes(), "snapshot");

    }

    public String sla(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "sla");

    }

    public boolean open(@Nonnull AtlasEntity entity) {
        return MapUtils.getBooleanValue(entity.getAttributes(), "open");

    }

    public String securityLevel(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "securityLevel");

    }

    public String locationUrl(@Nonnull AtlasEntity entity) {
        return MapUtils.getString(entity.getAttributes(), "locationUrl");

    }

    public List<String> columnIds(@Nonnull AtlasEntity entity) {
        List<HashMap<String, Object>> columns = columns(entity);
        final List<String> ids = new LinkedList<>();
        if(CollectionUtils.isEmpty(columns)){
            return ids;
        }
        columns.forEach(column -> {
            final String guid = (String) column.get("guid");
            final String type = (String) column.get("typeName");
            if (ChorusMetaStoreBridge.TYPES_CHOR_HIVE_COLUMN.equalsIgnoreCase(type)) {
                ids.add(guid);
            }
        });
        return ids;
    }
}
