package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ResourceInner;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by abiton on 24/11/2016.
 */
public interface ResourceInnerDOMapper {
    int insertSelective(ResourceInner record);
    ResourceInner getByProjectId(@Param("projectId") Long projectId);
    Map<String,Object> queryPlatformResource(@Param("statusCode") String statusCode);
}
