package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ApplyDetail;

public interface ApplyDetailMapper {
    int deleteByPrimaryKey(Long applyDetailId);

    int insert(ApplyDetail record);

    int insertSelective(ApplyDetail record);

    ApplyDetail selectByPrimaryKey(Long applyDetailId);

    int updateByPrimaryKeySelective(ApplyDetail record);

    int updateByPrimaryKey(ApplyDetail record);
}