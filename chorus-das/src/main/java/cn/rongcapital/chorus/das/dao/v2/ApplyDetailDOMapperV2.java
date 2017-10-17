package cn.rongcapital.chorus.das.dao.v2;

import cn.rongcapital.chorus.das.entity.ApplyDetailDOV2;
import cn.rongcapital.chorus.das.entity.ApplyDetailV2;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ApplyDetailDOMapperV2 {
    int deleteByPrimaryKey(Long applyDetailId);

    int insert(ApplyDetailV2 record);

    int insertSelective(ApplyDetailV2 record);

    ApplyDetailV2 selectByPrimaryKey(Long applyDetailId);

    int updateByPrimaryKeySelective(ApplyDetailV2 record);

    int updateByPrimaryKey(ApplyDetailV2 record);

    int bulkInsert(List<ApplyDetailV2> applyDetails);

    int updateStatusCode(ApplyDetailV2 record);

    //根据申请单ID,查询申请单明细数据
    List<ApplyDetailDOV2> selectByFormId(Long applyFormId);

    List<Map<String, Object>> getAllApplyInfoOfProject(@Param("projectId") long projectId, @Param("size") int size);
}
