package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ResourceUsage;

import java.util.List;

public interface ResourceUsageDOMapper {

    /**
     * 获取所有的资源引用类型
     *
     * @return 资源引用类型
     */
    List<ResourceUsage> selectResourceUsages();

}