package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.CommonStatus;

public interface CommonStatusMapper {
    int deleteByPrimaryKey(Long statusId);

    int insert(CommonStatus record);

    int insertSelective(CommonStatus record);

    CommonStatus selectByPrimaryKey(Long statusId);

    int updateByPrimaryKeySelective(CommonStatus record);

    int updateByPrimaryKey(CommonStatus record);
}