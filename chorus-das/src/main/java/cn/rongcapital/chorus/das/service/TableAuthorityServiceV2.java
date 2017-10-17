package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.TableAuthorityDOV2;
import cn.rongcapital.chorus.das.entity.TableAuthorityWithTableDOV2;
import cn.rongcapital.chorus.das.entity.TableInfoV2;

import java.util.List;

/**
 * Created by fuxiangli on 2016-11-30.
 */
public interface TableAuthorityServiceV2 {
    List<TableAuthorityDOV2> selectByUserId(String userId);

    List<TableAuthorityWithTableDOV2> selectTableByUserId(String userId);

    List<String> selectByUserIdAndTableInfoId(String userId, String tableInfoId);

    List<String> selectByUserIdAndTableInfo(String userId, TableInfoV2 tableInfoV2 ,List<ColumnInfoV2> columnInfoList);

    List<ProjectInfoDO> projectsOfAuthorizedTables(String userId);

    List<TableAuthorityDOV2> tablesOfProjectAndUser(Long projectId, String userId);

    List<TableAuthorityDOV2> columnsOfTable(String userId, String tableId);
}
