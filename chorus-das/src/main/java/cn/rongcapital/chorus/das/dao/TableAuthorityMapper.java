package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.TableAuthority;

public interface TableAuthorityMapper {
    int deleteByPrimaryKey(Long tableAuthorityId);

    int insert(TableAuthority record);

    int insertSelective(TableAuthority record);

    TableAuthority selectByPrimaryKey(Long tableAuthorityId);

    int updateByPrimaryKeySelective(TableAuthority record);

    int updateByPrimaryKey(TableAuthority record);

}
