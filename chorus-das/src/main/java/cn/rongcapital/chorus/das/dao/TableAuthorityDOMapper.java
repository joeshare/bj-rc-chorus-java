package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.TableAuthorityDO;
import cn.rongcapital.chorus.das.entity.TableAuthorityWithTableDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TableAuthorityDOMapper {
    //查询库，表，字段权限
    List<TableAuthorityDO> selectByUserId(String userId);

    List<TableAuthorityDO> selectByAppliedUserId(String userId);

    List<TableAuthorityDO> selectByAdminAndOwnerId(String userId);

    List<TableAuthorityWithTableDO> selectTableByAppliedUserId(String userId);

    List<TableAuthorityWithTableDO> selectTableByAdminAndOwnerId(String userId);

    //查询库，表，字段权限
    List<TableAuthorityDO> selectByUserIdAndTableInfoId(@Param("userId") String userId,
                                                        @Param("tableInfoId") Long tableInfoId,
                                                        @Param("statusCodeList") List<String> statusCodeList);

}
