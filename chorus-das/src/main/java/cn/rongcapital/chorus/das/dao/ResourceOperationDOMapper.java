package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ResourceOperationDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by abiton on 21/11/2016.
 */
public interface ResourceOperationDOMapper {
   List<ResourceOperationDO> selectByStatus(@Param("statusCode") String statusCode);
   List<ResourceOperationDO> selectByCreateProjectId(@Param("projectId") Long projectId);
}
