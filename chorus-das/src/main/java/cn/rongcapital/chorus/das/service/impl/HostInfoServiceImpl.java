package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.HostInfoDOMapper;
import cn.rongcapital.chorus.das.entity.HostInfo;
import cn.rongcapital.chorus.das.entity.HostReservationSizeDO;
import cn.rongcapital.chorus.das.service.HostInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by alan on 11/27/16.
 */
@Service
public class HostInfoServiceImpl implements HostInfoService {

    @Autowired
    private HostInfoDOMapper hostInfoDOMapper;

    @Override
    public List<HostInfo> getHostListByEnv(List<Long> environmentIdList) {
        return hostInfoDOMapper.selectByEnvironment(environmentIdList);
    }

    @Override
    public List<HostInfo> getHostList() {
        return hostInfoDOMapper.selectAll();
    }

    @Override
    public Map<Long, HostReservationSizeDO> getReservationSizeMap() {
        return hostInfoDOMapper.selectReservationList().stream()
                .collect(Collectors.toMap(HostReservationSizeDO::getHostId, Function.identity()));
    }
}
