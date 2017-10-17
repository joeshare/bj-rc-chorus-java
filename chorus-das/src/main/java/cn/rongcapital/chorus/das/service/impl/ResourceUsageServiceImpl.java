package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.ResourceUsageDOMapper;
import cn.rongcapital.chorus.das.dao.ResourceUsageMapper;
import cn.rongcapital.chorus.das.entity.ResourceUsage;
import cn.rongcapital.chorus.das.service.ResourceUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by shicheng on 2016/12/4.
 */
@Service("ResourceUsageService")
public class ResourceUsageServiceImpl implements ResourceUsageService {

    @Autowired
    private ResourceUsageMapper resourceUsageMapper;

    @Autowired
    private ResourceUsageDOMapper resourceUsageDOMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return resourceUsageMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(ResourceUsage record) {
        return resourceUsageMapper.insert(record);
    }

    @Override
    public int insertSelective(ResourceUsage record) {
        return resourceUsageMapper.insertSelective(record);
    }

    @Override
    public ResourceUsage selectByPrimaryKey(Integer id) {
        return resourceUsageMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(ResourceUsage record) {
        return resourceUsageMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(ResourceUsage record) {
        return resourceUsageMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<ResourceUsage> selectResourceUsages() {
        return resourceUsageDOMapper.selectResourceUsages();
    }
}
