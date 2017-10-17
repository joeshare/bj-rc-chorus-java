package cn.rongcapital.chorus.das.dao;

import java.util.List;

import cn.rongcapital.chorus.das.entity.OperLog;

public interface OperLogDao {

    List<OperLog> getLogList(String id, int pageindex);

    void save(OperLog operLog);

    int getCount(String id);

}
