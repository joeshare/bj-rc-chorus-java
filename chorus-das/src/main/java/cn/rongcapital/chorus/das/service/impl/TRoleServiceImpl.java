package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.TRoleDOMapper;
import cn.rongcapital.chorus.das.dao.TRoleMapper;
import cn.rongcapital.chorus.das.entity.TRole;
import cn.rongcapital.chorus.das.service.TRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by shicheng on 2017/4/26.
 */
@Service(value = "TRoleService")
public class TRoleServiceImpl implements TRoleService {

    @Autowired(required = false)
    TRoleMapper tRoleMapper;

    @Autowired(required = false)
    TRoleDOMapper tRoleDOMapper;

    @Override
    public int deleteByPrimaryKey(String roleId) {
        return tRoleMapper.deleteByPrimaryKey(roleId);
    }

    @Override
    public int insert(TRole record) {
        return tRoleMapper.insert(record);
    }

    @Override
    public int insertSelective(TRole record) {
        return tRoleMapper.insertSelective(record);
    }

    @Override
    public TRole selectByPrimaryKey(String roleId) {
        return tRoleMapper.selectByPrimaryKey(roleId);
    }

    @Override
    public int updateByPrimaryKeySelective(TRole record) {
        return tRoleMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(TRole record) {
        return tRoleMapper.updateByPrimaryKey(record);
    }

    @Override
    public TRole selectByRoleCode(String roleCode) {
        return tRoleDOMapper.selectByRoleCode(roleCode);
    }

    @Override
    public TRole selectByRoleName(String roleName) {
        return tRoleDOMapper.selectByRoleName(roleName);
    }

    @Override
    public List<TRole> selectAll() {
        return tRoleDOMapper.selectAll();
    }
}
