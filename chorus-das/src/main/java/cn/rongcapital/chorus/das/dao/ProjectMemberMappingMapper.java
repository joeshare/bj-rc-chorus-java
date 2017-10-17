package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.ProjectMemberMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectMemberMappingMapper {
    int deleteByPrimaryKey(Long projectMemberId);

    int insert(ProjectMemberMapping record);

    int insertSelective(ProjectMemberMapping record);

    ProjectMemberMapping selectByPrimaryKey(Long projectMemberId);

    int updateByPrimaryKeySelective(ProjectMemberMapping record);

    int updateByPrimaryKey(ProjectMemberMapping record);

    List<ProjectInfoDO> selectProjectByUserId(String userId);

    ProjectInfoDO getProject(@Param("userId") String userId,@Param("projectId") Long projectId);
}
