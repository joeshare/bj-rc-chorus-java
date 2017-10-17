package cn.rongcapital.chorus.resourcemanager.service;

/**
 * Created by alan on 12/8/16.
 */
public interface RescueService {

    void rescueAfterXdRestarted();

    void rescueAfterXdRestarted(Long projectId);
}
