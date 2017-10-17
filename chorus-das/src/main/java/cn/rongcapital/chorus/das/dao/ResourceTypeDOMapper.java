package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ResourceType;

import java.util.List;

/**
 * Created by shicheng on 2016/12/4.
 */
public interface ResourceTypeDOMapper {

    /**
     * 获取所有的资源类型
     *
     * @return 资源类型列表
     */
    List<ResourceType> selectResourceTypes();

}
