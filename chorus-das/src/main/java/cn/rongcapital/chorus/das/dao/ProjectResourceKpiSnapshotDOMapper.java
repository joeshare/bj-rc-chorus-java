package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ProjectResourceKpiSnapshotVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectResourceKpiSnapshotDOMapper {
    List<ProjectResourceKpiSnapshotVO> selectByKpiDate(Date kpiDate);
    List<ProjectResourceKpiSnapshotVO> selectByKpiDateWithOrder(Map paramMap);
}