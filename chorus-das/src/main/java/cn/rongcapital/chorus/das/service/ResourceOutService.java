package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ResourceOut;
import cn.rongcapital.chorus.das.entity.ResourceOutDO;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 外部资源操作接口
 * <p>用于操作外部资源的各项工作, 如: 添加资源, 删除资源, 修改资源, 更新资源等</p>
 *
 * @author shicheng
 * @version 1.0
 * @since <pre>十一月 23, 2016</pre>
 */
public interface ResourceOutService {

    /**
     * 根据 id 删除资源
     *
     * @param resourceOutId 资源 id
     * @return 受影响的行数
     */
    int deleteByPrimaryKey(Long resourceOutId);

    /**
     * 创建资源
     *
     * @param record 资源信息
     * @return 受影响的行数
     */
    int insert(ResourceOut record);

    ResourceOut syncToMetaDataCenter(@Nonnull ResourceOut record);

    /**
     * 创建资源
     *
     * @param record 资源信息
     * @return 受影响的行数
     */
    int insertSelective(ResourceOut record);

    /**
     * 根据资源 id 查询资源信息
     *
     * @param resourceOutId 资源 ID
     * @return 资源信息
     */
    ResourceOut selectByPrimaryKey(Long resourceOutId);

    /**
     * 更新资源
     *
     * @param record 资源信息
     * @return 受影响的行数
     */
    int updateByPrimaryKeySelective(ResourceOut record);

    /**
     * 更新资源
     *
     * @param record 资源信息
     * @return 受影响的行数
     */
    int updateByPrimaryKey(ResourceOut record);

    /**
     * 根据项目 id 查询资源列表
     *
     * @param projectId 项目 id
     * @return 资源列表
     */
    List<ResourceOutDO> selectResourceOutDOByProjectId(Long projectId);

    /**
     * 根据项目ID、type 查询项目资源列表
     * @param projectId
     * @return
     */
    List<ResourceOut> selectResourceOutByProjectId(Long projectId, int type);

    /**
     * 根据projectId查询该项目下所有类型的资源列表
     * @param projectId
     * @return
     */
    List<ResourceOut> selectAllResourceOutByProjectId(Long projectId);

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
    List<ResourceOut> selectResourceOutByCondition(Long projectId, String resourceName, String storageType, String resourceUsage, String createUserId, boolean isAccurate, int pageNum, int pageSize);

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
    List<ResourceOut> selectResourceOutByName(Long projectId, String resourceName, boolean isAccurate, int pageNum, int pageSize);

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
    List<ResourceOut> selectResourceOutByType(Long projectId, String storageType, boolean isAccurate, int pageNum, int pageSize);

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
    List<ResourceOut> selectResourceOutByUsage(Long projectId, String resourceUsage, boolean isAccurate, int pageNum, int pageSize);

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
    List<ResourceOut> selectResourceOutByUserId(Long projectId, String createUserId, boolean isAccurate, int pageNum, int pageSize);

    /**
     * 根据项目 ID 删除资源
     *
     * @param projectId 项目 ID
     * @return 受影响的行数
     */
    int deleteByProjectId(Long projectId);

    List<ResourceOut> getUnSyncedMySQLResource(int storageType);
}
