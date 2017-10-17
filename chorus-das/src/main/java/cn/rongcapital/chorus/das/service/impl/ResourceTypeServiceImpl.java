package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.ResourceTypeDOMapper;
import cn.rongcapital.chorus.das.dao.ResourceTypeMapper;
import cn.rongcapital.chorus.das.entity.ResourceType;
import cn.rongcapital.chorus.das.service.ResourceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by shicheng on 2016/12/4.
 */
@Service("ResourceTypeService")
public class ResourceTypeServiceImpl implements ResourceTypeService {

    @Autowired
    private ResourceTypeMapper resourceTypeMapper;

    @Autowired
    private ResourceTypeDOMapper resourceTypeDOMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return resourceTypeMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(ResourceType record) {
        return resourceTypeMapper.insert(record);
    }

    @Override
    public int insertSelective(ResourceType record) {
        return resourceTypeMapper.insertSelective(record);
    }

    @Override
    public ResourceType selectByPrimaryKey(Integer id) {
        return resourceTypeMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(ResourceType record) {
        return resourceTypeMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(ResourceType record) {
        return resourceTypeMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<ResourceType> selectResourceTypes() {
        return resourceTypeDOMapper.selectResourceTypes();
    }
}
