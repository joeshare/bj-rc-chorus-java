package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.EnvironmentInfo;

import java.util.List;

/**
 * Created by alan on 12/2/16.
 */
public interface EnvironmentInfoService {

    /**
     * 根据InstanceId获取执行单元组软件环境列表.
     *
     * @param instanceId 执行单元组Id.
     * @return 执行单元组软件环境列表.
     */
    List<EnvironmentInfo> listByInstanceId(Long instanceId);

    /**
     * 获取所有执行单元组软件环境列表.
     */
    List<EnvironmentInfo> listAll();

}
