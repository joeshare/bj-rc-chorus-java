package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ApplyForm;

public interface ApplyFormMapper {
    int deleteByPrimaryKey(Long applyFormId);

    int insert(ApplyForm record);

    int insertSelective(ApplyForm record);

    ApplyForm selectByPrimaryKey(Long applyFormId);

    int updateByPrimaryKeySelective(ApplyForm record);

    int updateByPrimaryKey(ApplyForm record);
}