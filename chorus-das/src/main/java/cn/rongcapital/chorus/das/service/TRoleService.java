package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.TRole;

import java.util.List;

public interface TRoleService {
    int deleteByPrimaryKey(String roleId);

    int insert(TRole record);

    int insertSelective(TRole record);

    TRole selectByPrimaryKey(String roleId);

    int updateByPrimaryKeySelective(TRole record);

    int updateByPrimaryKey(TRole record);

    TRole selectByRoleCode(String roleCode);

    TRole selectByRoleName(String roleName);

    List<TRole> selectAll();

}