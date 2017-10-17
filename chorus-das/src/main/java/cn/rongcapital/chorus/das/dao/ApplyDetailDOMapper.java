package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ApplyDetail;
import cn.rongcapital.chorus.das.entity.ApplyDetailDO;

import java.util.List;

public interface ApplyDetailDOMapper {
    int deleteByPrimaryKey(Long applyDetailId);

    int insert(ApplyDetail record);

    int insertSelective(ApplyDetail record);

    ApplyDetail selectByPrimaryKey(Long applyDetailId);

    int updateByPrimaryKeySelective(ApplyDetail record);

    int updateByPrimaryKey(ApplyDetail record);

    int bulkInsert(List<ApplyDetail> applyDetails);

    int updateStatusCode(ApplyDetail record);
    //根据申请单ID,查询申请单明细数据
    List<ApplyDetailDO> selectByFormId(Long applyFormId);
}