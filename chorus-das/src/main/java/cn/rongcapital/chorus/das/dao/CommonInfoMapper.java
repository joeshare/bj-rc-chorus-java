package cn.rongcapital.chorus.das.dao;

import org.apache.ibatis.annotations.Param;

import cn.rongcapital.chorus.das.entity.CommonInfoDO;

/**
 * 通用信息Mapper
 * @author kevin.gong
 * @Time 2017年9月19日 上午11:12:55
 */
public interface CommonInfoMapper {
    
    int insert(CommonInfoDO commonInfo);
    
    int update(CommonInfoDO commonInfo);
    
    int delete(CommonInfoDO commonInfo);
    
    CommonInfoDO selectByUserIdAndAttrId(@Param("userId") String userId, @Param("attributeId") String attributeId);
}