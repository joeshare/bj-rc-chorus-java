package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.dao.ResourceOutDOMapper;
import cn.rongcapital.chorus.das.dao.ResourceOutMapper;
import cn.rongcapital.chorus.das.entity.ResourceOut;
import cn.rongcapital.chorus.das.entity.ResourceOutDO;
import cn.rongcapital.chorus.das.entity.ResourceOutEnum;
import cn.rongcapital.chorus.das.service.ResourceOutService;
import cn.rongcapital.chorus.das.types.FTPEntityDefinitionAndBuilder;
import cn.rongcapital.chorus.das.types.MySQLDBEntityDefinitionAndBuilder;
import cn.rongcapital.chorus.governance.AtlasService;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

import static cn.rongcapital.chorus.governance.atlas.entity.AbstractAtlasAtlasEntityBuilder.UNIQUE;

/**
 * 资源操作接口实现类
 * <p>
 * <p>实现资源的各项操作</p>
 *
 * @author shicheng
 * @version 1.0
 * @since <pre>十一月 23, 2016</pre>
 */
@Slf4j
@Service(value = "ResourceOutService")
public class ResourceOutServiceImpl implements ResourceOutService {

    @Autowired
    private ResourceOutMapper resourceOutMapper;

    @Autowired
    private ResourceOutDOMapper resourceOutDOMapper;

    @Autowired
    private AtlasService atlasService;

    @Autowired
    private MySQLDBEntityDefinitionAndBuilder dbEntityBuilder;

    @Autowired
    private FTPEntityDefinitionAndBuilder ftpEntityBuilder;

    /**
     * 根据 id 删除资源
     *
     * @param resourceOutId 资源 id
     * @return 受影响的行数
     */
    @Override
    public int deleteByPrimaryKey(Long resourceOutId) {
        if (resourceOutId != null) {
            final ResourceOut resourceOut = resourceOutMapper.selectByPrimaryKey(resourceOutId);
            if (resourceOut != null) {
                cleanOutResourceMetaData(ImmutableList.of(resourceOut));
                return resourceOutMapper.deleteByPrimaryKey(resourceOutId);
            }
        }
        return 0;
    }

    private void cleanOutResourceMetaData(@Nonnull List<ResourceOut> resourceOuts) {
        if (resourceOuts.size() == 0) return;
        final List<String> guids = resourceOuts.stream().map(ResourceOut::getGuid).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (log.isDebugEnabled()) {
            log.debug(
                    "clean {} out-resources, found {} atlas entity guids to delete:\n\t{}\n\t{}",
                    resourceOuts.size(),
                    guids.size(),
                    resourceOuts.stream().map(ResourceOut::getResourceOutId).collect(Collectors.toList()),
                    guids
            );
        }
        try {
            atlasService.clean(guids.toArray(new String[0]));
        } catch (AtlasServiceException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException("meta data clean fail", e);
        }
    }

    /**
     * 创建资源
     *
     * @param record 资源信息
     * @return 受影响的行数
     */
    @Override
    public int insert(ResourceOut record) {
        if (record != null) {
            final int affectedRows = resourceOutMapper.insert(record);
            if (affectedRows > 0) {
                syncToMetaDataCenter(record);
                if (StringUtils.isBlank(record.getGuid())) {
                    log.warn("sync out resource {}-{} to metadata center fail", record.getResourceOutId(), record.getResourceName());
                }
            }
            return affectedRows;
        }
        return 0;
    }

    @Override
    public ResourceOut syncToMetaDataCenter(@Nonnull ResourceOut record) {
        try {
            AtlasEntity atlasEntity = getAtlasEntity(record);
            String type = "";
            String unique = "";
            if(ResourceOutEnum.MYSQL.getCode().equals(record.getStorageType())){
                type = MySQLDBEntityDefinitionAndBuilder.MYSQL_DB;
                unique = dbEntityBuilder.unique(atlasEntity);
            }
            if(ResourceOutEnum.FTP.getCode().equals(record.getStorageType())){
                type = FTPEntityDefinitionAndBuilder.FTP_TYPE;
                unique = ftpEntityBuilder.unique(atlasEntity);
            }
            final AtlasEntity created = atlasService.getEntityByUniqueAttribute(type, UNIQUE, unique);
            if (created == null) {
                final AtlasEntity[] ingested = atlasService.ingest(atlasEntity);
                final AtlasEntity inserted = ingested[0];
                if (inserted != null) {
                    // dependence the record.getResourceOutId() returned
                    int affectedRows = updateGuid(record.getResourceOutId(), inserted.getGuid());
                    if (affectedRows > 0) {
                        record.setGuid(inserted.getGuid());
                    }
                } else {
                    log.warn("out resource [{}]'s type created fail.", ReflectionToStringBuilder.toString(atlasService));
                }
            } else {
                log.info("out resource [{}] is already exist {}.", ReflectionToStringBuilder.toString(atlasService), created.getGuid());
            }
        } catch (AtlasServiceException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return record;
    }

    private AtlasEntity mysqlOutResourceToEntity(ResourceOut record) {
        final AtlasEntity entity = dbEntityBuilder.getEntity();
        dbEntityBuilder.name(entity, record.getResourceName());
        dbEntityBuilder.url(entity, record.getConnUrl());
        dbEntityBuilder.host(entity, record.getConnHost());
        dbEntityBuilder.port(entity, record.getConnPort());
        dbEntityBuilder.dataBaseName(entity, record.getDatabaseName());
        dbEntityBuilder.connectUser(entity, record.getConnUser());
        dbEntityBuilder.descritpion(entity, record.getResourceDesc());
        dbEntityBuilder.createTime(entity, record.getCreateTime());
        dbEntityBuilder.updateTime(entity, record.getUpdateTime());
        dbEntityBuilder.createUser(entity, record.getCreateUserName());
        dbEntityBuilder.createUserId(entity, record.getCreateUserId());
        dbEntityBuilder.projectId(entity, record.getProjectId());
        return dbEntityBuilder.build(entity);
    }

    private AtlasEntity ftpOutResourceToEntity(ResourceOut record){
        final AtlasEntity entity = ftpEntityBuilder.getEntity();
        ftpEntityBuilder.name(entity, record.getResourceName());
        ftpEntityBuilder.host(entity, record.getConnHost());
        ftpEntityBuilder.port(entity, record.getConnPort());
        ftpEntityBuilder.userName(entity, record.getCreateUserName());
        ftpEntityBuilder.password(entity, record.getConnPass());
        ftpEntityBuilder.path(entity, record.getPath());
        ftpEntityBuilder.connectMode(entity, record.getConnectMode());
        ftpEntityBuilder.url(entity, String.format(FTPEntityDefinitionAndBuilder.unique_template, record.getCreateUserName(), record.getConnPass(), record.getConnHost(), record.getConnHost(), record.getPath()));
        return ftpEntityBuilder.build(entity);
    }

    private int updateGuid(@Nonnull Long resourceOutId, @Nullable String guid) {
        final int affectedRows = resourceOutMapper.updateGuid(resourceOutId, guid);
        if (affectedRows < 1) log.warn("update fail {}-{}", resourceOutId, guid);
        return affectedRows;
    }

    /**
     * 创建资源
     *
     * @param record 资源信息
     * @return 受影响的行数
     */
    @Override
    public int insertSelective(ResourceOut record) {
        if (record != null) {
            return resourceOutMapper.insertSelective(record);
        }
        return 0;
    }

    /**
     * 根据资源 id 查询资源信息
     *
     * @param resourceOutId 资源 ID
     * @return 资源信息
     */
    @Override
    public ResourceOut selectByPrimaryKey(Long resourceOutId) {
        if (resourceOutId != null) {
            return resourceOutMapper.selectByPrimaryKey(resourceOutId);
        }
        return null;
    }

    /**
     * 更新资源
     *
     * @param record 资源信息
     * @return 受影响的行数
     */
    @Override
    @Transactional
    public int updateByPrimaryKeySelective(ResourceOut record) {
        if (record != null) {
            int affectedRows = resourceOutMapper.updateByPrimaryKeySelective(record);
            if (affectedRows > 0) {
                updateMetaDataCenter(record);
            }
            return affectedRows;
        }
        return 0;
    }

    /**
     * @param record
     * @return
     * @author yunzhong
     * @time 2017年9月4日上午10:36:14
     */
    private void updateMetaDataCenter(@Nonnull ResourceOut record) {
        try {
            final ResourceOut dbRecord = resourceOutMapper.selectByPrimaryKey(record.getResourceOutId());
            if (dbRecord == null) {
                log.warn("There is no resource out record {}", record.getResourceOutId());
                return;
            }
            AtlasEntity atlasEntity = null;
            if (StringUtils.isNotEmpty(dbRecord.getGuid())) {
                atlasEntity = atlasService.getByGuid(dbRecord.getGuid());
            }
            // 如果atlas中不存在记录，则创建新的，更新db的信息；如果存在，则更新
            if (atlasEntity == null) {
                log.warn("There is no guid of resourc out {}, The new one will be created. ",
                        dbRecord.getResourceOutId());
                AtlasEntity newEntity = getAtlasEntity(dbRecord);
                final AtlasEntity[] ingestedEntities = atlasService.ingest(newEntity);
                if (ingestedEntities != null && ingestedEntities.length > 0) {
                    final AtlasEntity ingestedEntity = ingestedEntities[0];
                    int affectedRows = updateGuid(dbRecord.getResourceOutId(), ingestedEntity.getGuid());
                    if (affectedRows > 0) {
                        record.setGuid(ingestedEntity.getGuid());
                    }
                } else {
                    log.warn("out resource [{}]'s type created fail.", ReflectionToStringBuilder.toString(newEntity));
                }
            } else {
                log.info("update out resource {}.", dbRecord.getGuid());
                if(StringUtils.equals(record.getStatusCode(), StatusCode.RESOURCE_DESTROY.getCode())){
                    atlasService.clean(atlasEntity.getGuid());
                }else {
                    atlasService.update(assembleAtlasEntity(atlasEntity, dbRecord));
                }
            }
        } catch (AtlasServiceException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    private AtlasEntity assembleAtlasEntity(AtlasEntity atlasEntity, ResourceOut record) {
        AtlasEntity newEntity = getAtlasEntity(record);
        newEntity.setGuid(atlasEntity.getGuid());
        return newEntity;
    }

    private AtlasEntity getAtlasEntity(ResourceOut record){
        AtlasEntity newEntity = null;
        if(StringUtils.equals(record.getStorageType(), ResourceOutEnum.MYSQL.getCode())){
            newEntity = mysqlOutResourceToEntity(record);
        }
        if(StringUtils.equals(record.getStorageType(), ResourceOutEnum.FTP.getCode())){
            newEntity = ftpOutResourceToEntity(record);
        }
        return newEntity;
    }

    /**
     * 更新资源
     *
     * @param record 资源信息
     * @return 受影响的行数
     */
    @Override
    public int updateByPrimaryKey(ResourceOut record) {
        if (record != null) {
            return resourceOutMapper.updateByPrimaryKey(record);
        }
        return 0;
    }

    /**
     * 根据项目 id 查询资源列表
     *
     * @param projectId 项目 id
     * @return 资源列表
     */
    @Override
    public List<ResourceOutDO> selectResourceOutDOByProjectId(Long projectId) {
        if (projectId != null) {
            return resourceOutDOMapper.selectResourceOutDOByProjectId(projectId);
        }
        return null;
    }

    public List<ResourceOut> selectResourceOutByProjectId(Long projectId, int type) {
        if (projectId != null) {
            return resourceOutDOMapper.selectResourceOutByProjectId(projectId, type);
        }
        return null;
    }

    @Override
    public List<ResourceOut> selectAllResourceOutByProjectId(Long projectId){
        if(projectId != null){
            return resourceOutDOMapper.selectAllResourceOutByProjectId(projectId);
        }
        return null;
    }

    /**
     * 根条件查询资源列表
     *
     * @param projectId     项目 id
     * @param resourceName  资源名称
     * @param storageType   资源类型
     * @param resourceUsage 资源用途
     * @param createUserId  创建人
     * @param isAccurate    是否精确查询, 默认是
     * @param pageNum       页数
     * @param pageSize      每页总数
     * @return 资源列表
     */
    @Override
    public List<ResourceOut> selectResourceOutByCondition(Long projectId, String resourceName, String storageType, String resourceUsage, String createUserId, boolean isAccurate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return resourceOutDOMapper.selectResourceOutByCondition(projectId, resourceName, storageType, resourceUsage, createUserId, isAccurate);
    }

    /**
     * 根据资源名称查找资源
     *
     * @param projectId    项目 id
     * @param resourceName 资源名称
     * @param isAccurate   是否精确查询, 默认是
     * @param pageNum      页数
     * @param pageSize     每页总数
     * @return 资源列表
     */
    @Override
    public List<ResourceOut> selectResourceOutByName(Long projectId, String resourceName, boolean isAccurate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return resourceOutDOMapper.selectResourceOutByCondition(projectId, resourceName, null, null, null, isAccurate);
    }

    /**
     * 根据资源类型查找资源
     *
     * @param projectId   项目 id
     * @param storageType 资源类型
     * @param isAccurate  是否精确查询, 默认是
     * @param pageNum     页数
     * @param pageSize    每页总数
     * @return 资源列表
     */
    @Override
    public List<ResourceOut> selectResourceOutByType(Long projectId, String storageType, boolean isAccurate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return resourceOutDOMapper.selectResourceOutByCondition(projectId, null, storageType, null, null, isAccurate);
    }

    /**
     * 根据资源用途查找资源
     *
     * @param projectId     项目 id
     * @param resourceUsage 资源用途
     * @param isAccurate    是否精确查询, 默认是
     * @param pageNum       页数
     * @param pageSize      每页总数
     * @return 资源列表
     */
    @Override
    public List<ResourceOut> selectResourceOutByUsage(Long projectId, String resourceUsage, boolean isAccurate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return resourceOutDOMapper.selectResourceOutByCondition(projectId, null, null, resourceUsage, null, isAccurate);
    }

    /**
     * 根据创建人查找资源
     *
     * @param projectId    项目 id
     * @param createUserId 创建人
     * @param isAccurate   是否精确查询, 默认是
     * @param pageNum      页数
     * @param pageSize     每页总数
     * @return 资源列表
     */
    @Override
    public List<ResourceOut> selectResourceOutByUserId(Long projectId, String createUserId, boolean isAccurate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return resourceOutDOMapper.selectResourceOutByCondition(projectId, null, null, null, createUserId, isAccurate);
    }

    /**
     * 根据项目 ID 删除资源
     *
     * @param projectId 项目 ID
     * @return 受影响的行数
     */
    @Override
    public int deleteByProjectId(Long projectId) {
        if (projectId != null) {
            final List<ResourceOut> resourceOuts = resourceOutDOMapper.selectAllResourceOutByProjectId(projectId);
            if (CollectionUtils.isNotEmpty(resourceOuts)) {
                cleanOutResourceMetaData(resourceOuts);
                return resourceOutDOMapper.deleteByProjectId(projectId);
            }
        }
        return 0;
    }

    @Override
    public List<ResourceOut> getUnSyncedMySQLResource(int storageType) {
        return resourceOutMapper.unSyncedResourceOut(storageType);
    }
}
