package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ProjectInfo;

public interface ProjectInfoMapper {
    int deleteByPrimaryKey(Long projectId);

    int insert(ProjectInfo record);

    int insertSelective(ProjectInfo record);

    ProjectInfo selectByPrimaryKey(Long projectId);

    int updateByPrimaryKeySelective(ProjectInfo record);

    int updateByPrimaryKey(ProjectInfo record);

    int queryCountNum();
}