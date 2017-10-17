package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ResourceInner;

/**
 * Created by alan on 12/2/16.
 */
public interface ResourceInnerService {

    ResourceInner getByProjectId(Long projectId);
    ResourceInner getLeftByProjectId(Long projectId);
    int updateByPrimaryKeySelective(ResourceInner record);
}
