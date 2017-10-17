package cn.rongcapital.chorus.das.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.ProjectMemberMapping;
import cn.rongcapital.chorus.das.entity.ProjectMemberRoleCount;

public interface ProjectMemberMappingDOMapper {

    List<ProjectInfoDO> selectMemberByProjectId(Long projectId);

    int selectCountMemberByProjectId(Long projectId);

    List<ProjectInfoDO> selectMemberByProjectIdAndRole(@Param("projectId") Long projectId,
            @Param("roleId") String roleId);

    int deleteByProjectAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    int updateByProjectAndUserId(ProjectMemberMapping vo);

    /**
     * @param projectId
     * @return
     * @author yunzhong
     * @time 2017年6月16日下午3:58:41
     */
    List<ProjectMemberRoleCount> statProjectMember(@Param("projectId") Long projectId);

    /***
     *
     *
     * @param projectId
     * @param userId
     * @return
     */
    ProjectInfoDO selectMemberByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") String userId);

    /**
     * @param projectId
     * @param roleId
     * @return
     * @author yunzhong
     * @time 2017年8月22日下午1:45:43
     */
    List<ProjectInfoDO> selectMembers(@Param("projectId") Long projectId, @Param("roleId") String roleId);
}