package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ProjectResourceKpiSnapshot;

import java.util.List;
import java.util.Map;

public interface ProjectResourceKpiSnapshotMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProjectResourceKpiSnapshot record);

    int insertSelective(ProjectResourceKpiSnapshot record);

    ProjectResourceKpiSnapshot selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProjectResourceKpiSnapshot record);

    int updateByPrimaryKey(ProjectResourceKpiSnapshot record);

    Long queryDataDailyIncrTotalNumByDate(String date);

    List<Map<String, Object>> getTrend(String date);
}