package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.TotalResource;
import org.apache.ibatis.annotations.Param;

/**
 * Created by abiton on 22/11/2016.
 */
public interface TotalResourceDOMapper {
    TotalResource sumResourceByStatus(@Param("statusCode") String statusCode);
}
