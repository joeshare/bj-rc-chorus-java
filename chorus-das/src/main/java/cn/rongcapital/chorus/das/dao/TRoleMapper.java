package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.TRole;

public interface TRoleMapper {
    int deleteByPrimaryKey(String roleId);

    int insert(TRole record);

    int insertSelective(TRole record);

    TRole selectByPrimaryKey(String roleId);

    int updateByPrimaryKeySelective(TRole record);

    int updateByPrimaryKey(TRole record);
}