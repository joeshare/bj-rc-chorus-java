package cn.rongcapital.chorus.datalab.service;

import cn.rongcapital.chorus.das.entity.DatalabInfo;

import java.util.List;

/**
 * Created by abiton on 06/03/2017.
 */
public interface LabService {

    void createLab(DatalabInfo datalabInfoDO);

    void startLab(String projectCode, String labCode);

    void stopLab(String projectCode, String labCode);

    void deleteLab(String projectCode, String labCode);

    List<DatalabInfo> getLabsByProject(String projectCode);

    List<DatalabInfo> getLabsByProject(String projectCode, int pageNum, int pageSize);

}
