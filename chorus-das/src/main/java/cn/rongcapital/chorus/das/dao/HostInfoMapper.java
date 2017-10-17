package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.HostInfo;

public interface HostInfoMapper {
    int deleteByPrimaryKey(Long hostId);

    int insert(HostInfo record);

    int insertSelective(HostInfo record);

    HostInfo selectByPrimaryKey(Long hostId);

    int updateByPrimaryKeySelective(HostInfo record);

    int updateByPrimaryKey(HostInfo record);
}