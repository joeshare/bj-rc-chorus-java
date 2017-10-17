package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.util.DateUtils;
import cn.rongcapital.chorus.das.dao.PlatformResourceSnapshotMapper;
import cn.rongcapital.chorus.das.entity.PlatformResourceSnapshot;
import cn.rongcapital.chorus.das.service.PlatformResourceSnapshotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Athletics on 2017/7/19.
 */
@Component
@Slf4j
@Service("PlatformResourceSnapshotService")
public class PlatformResourceSnapshotServiceImpl implements PlatformResourceSnapshotService {

    @Autowired
    private PlatformResourceSnapshotMapper platformResourceSnapshotMapper;

    @Override
    public List<PlatformResourceSnapshot> getUseRateTrend() {
        return platformResourceSnapshotMapper.getUseRateTrend(DateUtils.getIntervalDay(new Date(), -30));
    }
}
