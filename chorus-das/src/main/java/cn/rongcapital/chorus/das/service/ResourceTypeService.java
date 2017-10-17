package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ResourceType;

import java.util.List;

/**
 * Created by shicheng on 2016/12/4.
 */
public interface ResourceTypeService {

    int deleteByPrimaryKey(Integer id);

    int insert(ResourceType record);

    int insertSelective(ResourceType record);

    ResourceType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ResourceType record);

    int updateByPrimaryKey(ResourceType record);

    List<ResourceType> selectResourceTypes();

}
