package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ResourceOut;
import cn.rongcapital.chorus.das.entity.ResourceOutDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by shicheng on 2016/11/24.
 */
public interface ResourceOutDOMapper {

    /**
     * 根据项目 id 查询资源列表
     *
     * @param projectId 项目 id
     * @return 资源列表
     */
    List<ResourceOut> selectResourceOutByProjectId(@Param("projectId")Long projectId, @Param("type")int type);
    List<ResourceOutDO> selectResourceOutDOByProjectId(Long projectId);

    /**
     * 根条件查询资源列表
     *
     * @param projectId     项目 id
     * @param resourceName  资源名称
     * @param storageType   资源类型
     * @param resourceUsage 资源用途
     * @param createUserId  创建人
     * @param isAccurate    是否精确查询, 默认是
     * @return 资源列表
     */
    List<ResourceOut> selectResourceOutByCondition(@Param("projectId") Long projectId,
                                                   @Param("resourceName") String resourceName,
                                                   @Param("storageType") String storageType,
                                                   @Param("resourceUsage") String resourceUsage,
                                                   @Param("createUserId") String createUserId,
                                                   @Param("isAccurate") boolean isAccurate);

    /**
     * 根据项目 ID 删除资源
     *
     * @param projectId 项目 ID
     * @return 受影响的行数
     */
    int deleteByProjectId(Long projectId);

    /**
     * 查询这个项目下所有的资源信息
     * @param projectId
     * @return
     */
    List<ResourceOut> selectAllResourceOutByProjectId(Long projectId);
}
