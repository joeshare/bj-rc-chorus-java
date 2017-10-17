package cn.rongcapital.chorus.das.dao.v2;

import cn.rongcapital.chorus.das.entity.ApplyFormDOV2;
import cn.rongcapital.chorus.das.entity.ApplyFormV2;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApplyFormDOMapperV2 {

    List<ApplyFormDOV2> selectAllForm(@Param("userId") String userId);

    List<ApplyFormDOV2> selectForm(
            @Param("userId") String userId,
            @Param("approved") boolean approved
    );

    int approve(ApplyFormV2 form);

}
