package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.PlatformResourceSnapshot;

import java.util.List;

public interface PlatformResourceSnapshotMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PlatformResourceSnapshot record);

    int insertSelective(PlatformResourceSnapshot record);

    PlatformResourceSnapshot selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PlatformResourceSnapshot record);

    int updateByPrimaryKey(PlatformResourceSnapshot record);

    List<PlatformResourceSnapshot> getUseRateTrend(String date);
}