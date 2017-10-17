package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ResourceOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResourceOutMapper {
    int deleteByPrimaryKey(Long resourceOutId);

    int insert(ResourceOut record);

    int insertSelective(ResourceOut record);

    ResourceOut selectByPrimaryKey(Long resourceOutId);

    int updateByPrimaryKeySelective(ResourceOut record);

    int updateByPrimaryKey(ResourceOut record);

    int updateGuid(@Param(value = "resourceOutId") Long resourceOutId, @Param(value = "guid") String guid);

    List<ResourceOut> unSyncedResourceOut(@Param("storageType") int storageType);
}
