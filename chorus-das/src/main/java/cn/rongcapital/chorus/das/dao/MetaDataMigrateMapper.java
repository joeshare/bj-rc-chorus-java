package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.TableInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by hhlfl on 2017-7-25.
 */
public interface MetaDataMigrateMapper {
    List<ProjectInfo> getAllProjectInfos();
    List<TableInfo> getAllTableInfos();
    int updateTableInfoGuid(@Param("guid") String guid, @Param("tableInfoId") long tableInfoId);
    int updateColumnInfoGuid(@Param("guid") String guid, @Param("columnInfoId") long columnInfoId);
}
