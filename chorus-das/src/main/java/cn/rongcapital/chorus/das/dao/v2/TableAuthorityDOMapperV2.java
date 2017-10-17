package cn.rongcapital.chorus.das.dao.v2;

import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.TableAuthorityDOV2;
import cn.rongcapital.chorus.das.entity.TableAuthorityV2;
import cn.rongcapital.chorus.das.entity.TableAuthorityWithTableDOV2;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TableAuthorityDOMapperV2 {
    List<TableAuthorityDOV2> selectByAppliedUserId(String userId);

    List<TableAuthorityWithTableDOV2> selectTableByAppliedUserId(String userId);

    //查询库，表，字段权限
    List<TableAuthorityDOV2> selectByUserIdAndTableInfoId(
            @Param("userId") String userId,
            @Param("tableInfoId") String tableGuid,
            @Param("statusCodeList") List<String> statusCodeList
    );

    /***
     * (userId,columnInfoId)为唯一性约束，根据唯一性约束查询
     *
     * @param userId
     * @param columnInfoId
     * @return
     */
    TableAuthorityV2 selectByUnique(@Param("userId") String userId, @Param("columnInfoId") String columnInfoId);

    List<ProjectInfoDO> projectsOfAuthorizedTable(@Param("userId") String userId);

    List<TableAuthorityDOV2> tablesOfProjectAndUser(@Param("projectId") Long projectId, @Param("userId") String userId);

    List<TableAuthorityDOV2> columnsOfTable(@Param("tableId") String tableId, @Param("userId") String userId);
}
