package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.PlatformResourceSnapshot;

import java.util.List;

/**
 * Created by Athletics on 2017/7/19.
 */
public interface PlatformResourceSnapshotService {
    /**
     * 获取30天内资源走势
     * @return
     */
    List<PlatformResourceSnapshot> getUseRateTrend();
}
