package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.ResourceTemplateDOMapper;
import cn.rongcapital.chorus.das.dao.ResourceTemplateMapper;
import cn.rongcapital.chorus.das.entity.ResourceTemplate;
import cn.rongcapital.chorus.das.service.ResourceTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alan on 12/2/16.
 */
@Slf4j
@Service
public class ResourceTemplateServiceImpl implements ResourceTemplateService {

    @Autowired
    private ResourceTemplateMapper resourceTemplateMapper;
    @Autowired
    private ResourceTemplateDOMapper resourceTemplateDOMapper;

    @Override
    public ResourceTemplate getById(Long resourceTemplateId) {
        return resourceTemplateMapper.selectByPrimaryKey(resourceTemplateId);
    }

    @Override
    public List<ResourceTemplate> listAll() {
        return resourceTemplateDOMapper.selectAll();
    }
}
