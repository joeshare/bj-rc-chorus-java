package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.AuthorizationDetail;

public interface AuthorizationDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AuthorizationDetail record);

    int insertSelective(AuthorizationDetail record);

    AuthorizationDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AuthorizationDetail record);

    int updateByPrimaryKey(AuthorizationDetail record);
}