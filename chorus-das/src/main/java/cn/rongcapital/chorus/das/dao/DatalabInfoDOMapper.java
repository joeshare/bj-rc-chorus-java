package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.DatalabInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by abiton on 16/03/2017.
 */
public interface DatalabInfoDOMapper {

    void deleteByProjectCodeAndLabCode(@Param("projectCode") String projectCode,@Param("labCode") String labCode);

    DatalabInfo query(@Param("projectCode") String projectCode,@Param("labCode") String labCode);
    List<DatalabInfo> queryByProjectCode(@Param("projectCode") String projectCode);
}
