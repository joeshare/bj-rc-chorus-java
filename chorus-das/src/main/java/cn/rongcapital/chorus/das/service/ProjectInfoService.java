package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.ProjectInfoDO;

import java.util.List;

/**
 * 项目操作服务接口
 * <p>用于操作项目的各项工作, 如: 添加项目, 删除项目, 修改项目, 更新项目等</p>
 *
 * @author shicheng
 * @version 1.0
 * @since <pre>十一月 23, 2016</pre>
 */
public interface ProjectInfoService {

    /**
     * 创建项目
     * <p>该操作进行各个字段的非空验证, null 的字段数据将无法插入到数据库中</p>
     *
     * @param record 项目信息
     * @return 受影响的行数
     */
    int insertSelective(ProjectInfo record);

    /**
     * 创建项目
     * <p>该操作将传递的数据结果直接入库</p>
     *
     * @param record 项目信息
     * @return 受影响的行数
     */
    int insert(ProjectInfo record);

    /**
     * 更新项目
     * <p>该操作进行各个字段的非空验证, null 的字段数据将无法更新到数据库中</p>
     *
     * @param record 项目信息
     * @return 受影响的行数
     */
    int updateByPrimaryKeySelective(ProjectInfo record);

    /**
     * 更新项目
     * <p>该操作将传递的数据结果直接更新</p>
     *
     * @param record 项目信息
     * @return 受影响的行数
     */
    int updateByPrimaryKey(ProjectInfo record);

    /**
     * 根据用户 ID 查询项目列表
     *
     * @param userId 用户 id
     * @return 项目列表
     */
    List<ProjectInfoDO> selectAllProjectByUserId(String userId, int pageNum, int pageSize);
    List<ProjectInfoDO> selectAllProjectByUserId(String userId);

    /**
     * 根据项目 ID 查询项目
     *
     * @param projectId 项目 ID
     * @return 项目信息
     */
    ProjectInfo selectByPrimaryKey(Long projectId);

    /**
     * 根据项目名称查询项目
     *
     * @param projectName 项目名称
     * @return 项目信息
     */
    ProjectInfo selectByProjectName(String projectName);

    /**
     * 根据项目编码查询项目
     *
     * @param projectCode 项目编码
     * @return 项目信息
     */
    ProjectInfo selectByProjectCode(String projectCode);

    /**
     * 根据项目 ID 删除项目
     *
     * @param projectId 项目 ID
     * @return 受影响的行数
     */
    int deleteByPrimaryKey(Long projectId);

    /**
     * 根据项目名称删除项目
     *
     * @param projectName 项目名称
     * @return 受影响的行数
     */
    int deleteByProjectName(String projectName);

    /**
     * 根据项目编码删除项目
     *
     * @param projectCode 项目编码
     * @return 受影响的行数
     */
    int deleteByProjectCode(String projectCode);

    /**
     * 验证项目是否存在
     * <p>该验证会执行以下操作:</p>
     * <ul>
     * <li>1. 通过 ID 验证, 如果存在直接返回存在结果, 否则执行下一步</li>
     * <li>2. 通过名称验证, 如果存在直接返回存在结果, 否则执行下一步</li>
     * <li>3. 通过编码验证, 如果存在直接返回存在结果, 否则执行下一步</li>
     * </ul>
     *
     * @param object 验证数据
     * @return true | false (存在 | 不存在)
     */
    boolean validateProjectExists(String object);

    boolean validateProjectExistsByCode(String projectCode);

    /**
     * 根据条件查询项目信息列表
     *
     * @param projectName 项目名称
     * @param projectCode 项目编码
     * @param isAccurate  是否精确查询
     * @return 项目列表
     */
    List<ProjectInfo> selectProjectInfoByCondition(String projectName, String projectCode, String userId, boolean isAccurate, int pageNum, int pageSize);

    /**
     * 添加数据返回主键
     *
     * @param record 数据记录
     * @return 项目信息
     */
    ProjectInfo insertAndGetKey(ProjectInfo record);
    /**
     * 注销项目并释放资源（释放cpu memory queue）
     * @param projectId
     * @return
     */
    void canceledProjectAndReleaseResource(Long projectId ,String updateUserId) throws Exception;

    List<ProjectInfo> queryAll();
    
    List<ProjectInfo> queryAllActive();
}
