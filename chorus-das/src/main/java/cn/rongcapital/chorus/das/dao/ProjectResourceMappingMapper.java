package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ProjectResourceMapping;

public interface ProjectResourceMappingMapper {
    int deleteByPrimaryKey(Long projectResourceOutId);

    int insert(ProjectResourceMapping record);

    int insertSelective(ProjectResourceMapping record);

    ProjectResourceMapping selectByPrimaryKey(Long projectResourceOutId);

    int updateByPrimaryKeySelective(ProjectResourceMapping record);

    int updateByPrimaryKey(ProjectResourceMapping record);
}