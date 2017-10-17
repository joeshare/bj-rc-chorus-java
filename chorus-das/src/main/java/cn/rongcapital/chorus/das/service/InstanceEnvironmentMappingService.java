package cn.rongcapital.chorus.das.service;

import java.util.List;

/**
 * Created by alan on 12/5/16.
 */
public interface InstanceEnvironmentMappingService {

    /**
     * 批量保存执行单元组环境关系
     *
     * @param instanceId        执行单元组Id
     * @param environmentIdList 运行环境Id列表
     */
    void save(Long instanceId, List<Long> environmentIdList);

}
