package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.TotalResource;

public interface TotalResourceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TotalResource record);

    int insertSelective(TotalResource record);

    TotalResource selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TotalResource record);

    int updateByPrimaryKey(TotalResource record);
}