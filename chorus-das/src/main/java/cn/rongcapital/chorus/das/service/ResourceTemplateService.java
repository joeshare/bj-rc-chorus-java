package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ResourceTemplate;

import java.util.List;

/**
 * Created by alan on 12/2/16.
 */
public interface ResourceTemplateService {

    ResourceTemplate getById(Long resourceTemplateId);

    List<ResourceTemplate> listAll();
}
