package cn.rongcapital.chorus.das.dao.v2;

import cn.rongcapital.chorus.das.entity.ApplyFormV2;

public interface ApplyFormMapperV2 {
    int deleteByPrimaryKey(Long applyFormId);

    int insert(ApplyFormV2 record);

    int insertSelective(ApplyFormV2 record);

    ApplyFormV2 selectByPrimaryKey(Long applyFormId);

    int updateByPrimaryKeySelective(ApplyFormV2 record);

    int updateByPrimaryKey(ApplyFormV2 record);
}
