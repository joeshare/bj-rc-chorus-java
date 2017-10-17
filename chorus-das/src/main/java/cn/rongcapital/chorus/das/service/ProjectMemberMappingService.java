package cn.rongcapital.chorus.das.service;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.ProjectMemberCount;
import cn.rongcapital.chorus.das.entity.ProjectMemberMapping;

/**
 * Created by shicheng on 2016/11/25.
 */
public interface ProjectMemberMappingService {

    int deleteByPrimaryKey(Long projectMemberId);

    int insert(ProjectMemberMapping record);

    int updateByPrimaryKey(ProjectMemberMapping record);

    List<ProjectInfoDO> selectMemberByProjectId(Long projectId);

    int selectCountMemberByProjectId(Long projectId);

    /**
     * 返回projectid 和roleid查到的所有user,不包含创建者
     * 
     * @param projectId
     * @param roleId
     * @return
     * @author yunzhong
     * @time 2017年8月22日下午1:39:40
     */
    List<ProjectInfoDO> selectMemberByProjectIdAndRole(Long projectId, String roleId);

    /**
     * 返回projectid 和roleid查到的所有user
     * 
     * @param projectId
     * @param roleId
     * @return
     * @author yunzhong
     * @time 2017年8月22日下午1:37:34
     */
    List<ProjectInfoDO> selectMembers(Long projectId, String roleId);

    int deleteByProjectAndUserId(Long projectId, Long userId);

    int updateByProjectAndUserId(ProjectMemberMapping vo);

    /**
     * 按照角色统计项目下的人员数目
     * 
     * @param projectId
     * @return
     * @author yunzhong
     * @time 2017年6月16日下午3:22:35
     */
    ProjectMemberCount statProjectMember(Long projectId);

    List<ProjectInfoDO> getProjectByUser(String userId);

    @Nullable
    ProjectInfoDO getProject(@Nonnull String userId, @Nonnull Long projectId);

    ProjectInfoDO selectMemberByProjectIdAndUserId(Long projectId, String userId);
}
