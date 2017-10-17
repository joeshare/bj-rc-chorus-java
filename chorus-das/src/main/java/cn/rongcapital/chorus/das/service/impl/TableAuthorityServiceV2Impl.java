package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.dao.v2.TableAuthorityDOMapperV2;
import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.TableAuthorityDOV2;
import cn.rongcapital.chorus.das.entity.TableAuthorityWithTableDOV2;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.service.ColumnInfoServiceV2;
import cn.rongcapital.chorus.das.service.ProjectMemberMappingService;
import cn.rongcapital.chorus.das.service.TableAuthorityServiceV2;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

/**
 * Created by fuxiangli on 2016-11-30.
 */
@Service(value = "TableAuthorityServiceV2")
public class TableAuthorityServiceV2Impl implements TableAuthorityServiceV2 {
    private static final String CACHE_NAME = "cache_table_authority";

    @Autowired
    private ColumnInfoServiceV2 columnInfoServiceV2;
    @Autowired
    private TableInfoServiceV2 tableInfoServiceV2;

    @Autowired
    private TableAuthorityDOMapperV2 tableAuthorityDOMapperV2;
    @Autowired
    private ProjectMemberMappingService projectMemberMappingService;

    @Override
    @Cacheable(value = {CACHE_NAME}, key = "'TableAuthorityServiceV2ImplselectByUserId'.concat(#userId)", unless = "#result==null")
    public List<TableAuthorityDOV2> selectByUserId(String userId) {
        List<TableAuthorityDOV2> res = Lists.newLinkedList();
        res.addAll(tableAuthorityDOMapperV2.selectByAppliedUserId(userId));
        res.addAll(getColumnAuthorityDOV2s(userId));
        return res;
    }

    @Override
    public List<ProjectInfoDO> projectsOfAuthorizedTables(String userId) {
        return tableAuthorityDOMapperV2.projectsOfAuthorizedTable(userId);
    }

    @Override
    public List<TableAuthorityDOV2> tablesOfProjectAndUser(Long projectId, String userId) {
        return tableAuthorityDOMapperV2.tablesOfProjectAndUser(projectId, userId);
    }

    @Override
    public List<TableAuthorityDOV2> columnsOfTable(String userId, String tableId) {
        return tableAuthorityDOMapperV2.columnsOfTable(tableId, userId);
    }

    @Override
    @Cacheable(value = {CACHE_NAME}, key = "'TableAuthorityServiceV2ImplselectTableByUserId'.concat(#userId)", unless = "#result==null")
    public List<TableAuthorityWithTableDOV2> selectTableByUserId(String userId) {
        List<TableAuthorityWithTableDOV2> res = Lists.newLinkedList();
        res.addAll(tableAuthorityDOMapperV2.selectTableByAppliedUserId(userId));
        res.addAll(getTableAuthorityDOV2s(userId));
        return res;
    }

    @Override
    @Cacheable(value = {CACHE_NAME}, key = "'TableAuthorityServiceV2ImplselectByUserIdAndTableInfoId'.concat(#userId).concat(#tableInfoId)", unless = "#result==null")
    public List<String> selectByUserIdAndTableInfoId(String userId, String tableInfoId) {
        boolean isOwnerOrAdmin = getColumnAuthorityDOV2s(userId).stream()
                                                                .anyMatch(ta -> ta.getTableInfoId().equals(tableInfoId));
        if (isOwnerOrAdmin) {
            return columnInfoServiceV2.selectColumnInfo(tableInfoId).stream()
                                      .map(ColumnInfoV2::getColumnInfoId)
                                      .collect(toList());
        } else {
            return tableAuthorityDOMapperV2.selectByUserIdAndTableInfoId(userId, tableInfoId,
                                                                         Arrays.asList(StatusCode.APPLY_SUBMITTED.getCode(), StatusCode.APPLY_UNTREATED.getCode())
            ).stream().map(TableAuthorityDOV2::getColumnInfoId).collect(toList());
        }
    }

    @Override
    public List<String> selectByUserIdAndTableInfo(String userId, TableInfoV2 tableInfoV2 ,List<ColumnInfoV2> columnInfoList){
        boolean isOwnerOrAdmin = false;
        final List<ProjectInfoDO> authorizedProjects = projectMemberMappingService.getProjectByUser(userId);
        List<Long> projectIds = authorizedProjects.parallelStream().map(ProjectInfoDO::getProjectId).collect(Collectors.toList());
        if(projectIds.contains(tableInfoV2.getProjectId())){
            isOwnerOrAdmin = true;
        }
        if (isOwnerOrAdmin) {
            return columnInfoList.stream().map(ColumnInfoV2::getColumnInfoId).collect(toList());
        } else {
            return tableAuthorityDOMapperV2.selectByUserIdAndTableInfoId(userId, tableInfoV2.getTableInfoId(),
                    Arrays.asList(StatusCode.APPLY_SUBMITTED.getCode(), StatusCode.APPLY_UNTREATED.getCode())
            ).stream().map(TableAuthorityDOV2::getColumnInfoId).collect(toList());
        }
    }

    private List<TableAuthorityDOV2> getColumnAuthorityDOV2s(String userId) {
        List<TableAuthorityDOV2> authorizedTableColumns = new ArrayList<>();
        final List<ProjectInfoDO> authorizedProjects = projectMemberMappingService.getProjectByUser(userId);
        Map<String, List<ProjectInfoDO>> collect =
                authorizedProjects.stream().collect(groupingBy(ProjectInfoDO::getProjectCode, mapping((ProjectInfoDO p) -> p, toList())));
        if (CollectionUtils.isEmpty(authorizedProjects)) return authorizedTableColumns;
        List<Long> projectIds = authorizedProjects.parallelStream().map(ProjectInfoDO::getProjectId).distinct().collect(toList());
        Map<TableInfoV2, List<ColumnInfoV2>> tableWithColumns = tableInfoServiceV2.tableWithColumnsOfProject(projectIds);
        if (MapUtils.isEmpty(tableWithColumns)) return authorizedTableColumns;
        tableWithColumns.forEach((t, cols) -> cols.forEach(
                col -> {
                    ProjectInfoDO project = collect.get(t.getProjectCode()).get(0);
                    final TableAuthorityDOV2 authorityDOV2 = new  TableAuthorityDOV2();
                    authorityDOV2.setProjectId(project.getProjectId());
                    authorityDOV2.setProjectCode(project.getProjectCode());
                    authorityDOV2.setProjectName(project.getProjectName());
                    authorityDOV2.setUserId(project.getUserId());
                    authorityDOV2.setTableInfoId(t.getTableInfoId());
                    authorityDOV2.setTableName(t.getTableName());
                    authorityDOV2.setColumnInfoId(col.getColumnInfoId());
                    authorityDOV2.setColumnName(col.getColumnName());
                    authorizedTableColumns.add(authorityDOV2);
                }));

        return authorizedTableColumns;
    }

    private List<TableAuthorityWithTableDOV2> getTableAuthorityDOV2s(String userId) {
        List<TableAuthorityWithTableDOV2> authorizedTableColumns = new ArrayList<>();
        final List<ProjectInfoDO> authorizedProjects = projectMemberMappingService.getProjectByUser(userId);
        if (CollectionUtils.isEmpty(authorizedProjects)) return authorizedTableColumns;

        Map<String, List<ProjectInfoDO>> collect =
                authorizedProjects.stream().collect(groupingBy(ProjectInfoDO::getProjectCode, mapping((ProjectInfoDO p) -> p, toList())));

        List<Long> projectIds = authorizedProjects.parallelStream().map(ProjectInfoDO::getProjectId).distinct().collect(toList());

        Map<String,TableInfoV2>  tablesOfProject = tableInfoServiceV2.tablesOfProject(projectIds);
        if (MapUtils.isEmpty(tablesOfProject)) return authorizedTableColumns;
        tablesOfProject.values().forEach(
                t -> {
                    ProjectInfoDO project = collect.get(t.getProjectCode()).get(0);
                    final TableAuthorityWithTableDOV2 authorityDOV2 = new TableAuthorityWithTableDOV2();
                    authorityDOV2.setProjectId(project.getProjectId());
                    authorityDOV2.setProjectCode(project.getProjectCode());
                    authorityDOV2.setProjectName(project.getProjectName());
                    authorityDOV2.setUserId(project.getUserId());
                    authorityDOV2.setTableInfoId(t.getTableInfoId());
                    authorityDOV2.setTableName(t.getTableName());
                    authorizedTableColumns.add(authorityDOV2);
                });
        return authorizedTableColumns;
    }
}
