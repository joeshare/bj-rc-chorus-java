package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.DatalabInfo;

import java.util.List;

/**
 * Created by abiton on 16/03/2017.
 */
public interface DatalabInfoService {

    void create(DatalabInfo lab);

    void delete(String projectCode, String labCode);

    DatalabInfo get(String projectCode, String labCode);

    List<DatalabInfo> get(String projectCode);

    List<DatalabInfo> get(String projectCode, int pageNum, int pageSize);

}
