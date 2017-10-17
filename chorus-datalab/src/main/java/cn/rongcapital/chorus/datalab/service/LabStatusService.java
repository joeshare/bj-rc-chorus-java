package cn.rongcapital.chorus.datalab.service;

/**
 * Created by abiton on 16/03/2017.
 */
public interface LabStatusService {
    boolean isAlive(String projectCode,String labCode);
    String getStatus(String projectCode,String labCode);
    void delete(String projectCode,String labCode);
}
