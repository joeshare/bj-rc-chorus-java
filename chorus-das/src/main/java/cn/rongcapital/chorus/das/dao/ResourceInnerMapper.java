package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ResourceInner;

import java.util.Map;

public interface ResourceInnerMapper {
    int deleteByPrimaryKey(Long resourceInnerId);

    int insert(ResourceInner record);

    int insertSelective(ResourceInner record);

    ResourceInner selectByPrimaryKey(Long resourceInnerId);

    int updateByPrimaryKeySelective(ResourceInner record);

    int updateByPrimaryKey(ResourceInner record);

    Map<String, Object> queryUseRate();

    int queryCountNum();

    /**
     * 查询项目的资源。project_id有唯一建，所以只会返回单条记录。
     * 
     * @param projectId
     * @return
     * @author yunzhong
     * @time 2017年8月16日上午10:09:50
     */
    ResourceInner selectByProjectId(Long projectId);
}