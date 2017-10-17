package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.HostInfo;
import cn.rongcapital.chorus.das.entity.HostReservationSizeDO;

import java.util.List;
import java.util.Map;

/**
 * Created by alan on 11/27/16.
 */
public interface HostInfoService {

    List<HostInfo> getHostListByEnv(List<Long> environmentIdList);

    List<HostInfo> getHostList();

    /**
     * 获取'预留资源'(状态为created和stopped的执行单元组包含的已分配的Host资源)Map
     */
    Map<Long, HostReservationSizeDO> getReservationSizeMap();
}
