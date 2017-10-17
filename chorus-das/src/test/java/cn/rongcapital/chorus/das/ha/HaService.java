package cn.rongcapital.chorus.das.ha;

import cn.rongcapital.chorus.das.entity.HostInfo;

import java.util.List;

public interface HaService {

    void insert(int i);

    List<HostInfo> select();
}
