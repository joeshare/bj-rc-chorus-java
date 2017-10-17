package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.TRole;

import java.util.List;

public interface TRoleDOMapper {

    TRole selectByRoleCode(String roleCode);

    TRole selectByRoleName(String roleName);

    List<TRole> selectAll();

}