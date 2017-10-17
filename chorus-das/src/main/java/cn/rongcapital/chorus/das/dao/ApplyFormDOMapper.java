package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ApplyForm;
import cn.rongcapital.chorus.das.entity.ApplyFormDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApplyFormDOMapper {

    List<ApplyFormDO> selectAllForm(@Param("userId") String userId);

    List<ApplyFormDO> selectForm(@Param("userId") String userId,
                                 @Param("approved") boolean approved);

    int approve(ApplyForm form);

}