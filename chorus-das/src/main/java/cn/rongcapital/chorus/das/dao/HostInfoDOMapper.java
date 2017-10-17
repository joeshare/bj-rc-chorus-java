package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.HostInfo;
import cn.rongcapital.chorus.das.entity.HostReservationSizeDO;

import java.util.List;

public interface HostInfoDOMapper {
    List<HostInfo> selectByEnvironment(List<Long> environmentIdList);

    List<HostInfo> selectAll();

    List<HostReservationSizeDO> selectReservationList();
}