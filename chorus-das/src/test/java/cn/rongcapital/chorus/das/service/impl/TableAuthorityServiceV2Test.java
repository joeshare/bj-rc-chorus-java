package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.v2.TableAuthorityDOMapperV2;
import cn.rongcapital.chorus.das.entity.*;
import cn.rongcapital.chorus.das.service.ProjectMemberMappingService;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import cn.rongcapital.chorus.das.service.impl.TableAuthorityServiceV2Impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Athletics on 2017/8/9.
 */
public class TableAuthorityServiceV2Test {

    @InjectMocks
    private TableAuthorityServiceV2Impl tableAuthorityServiceV2;

    @Mock
    private TableAuthorityDOMapperV2 tableAuthorityDOMapperV2;

    @Mock
    private TableInfoServiceV2 tableInfoServiceV2;

    @Mock
    private ProjectMemberMappingService projectMemberMappingService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void selectByUserIdTest(){
        List<TableAuthorityDOV2> tableAuthorityDOV2s = new ArrayList<>();
        TableAuthorityDOV2 tableAuthorityDOV21 = new TableAuthorityDOV2();
        tableAuthorityDOV21.setProjectId(111L);
        tableAuthorityDOV21.setProjectName("aa");
        tableAuthorityDOV21.setProjectCode("code1");
        tableAuthorityDOV2s.add(tableAuthorityDOV21);
        TableAuthorityDOV2 tableAuthorityDOV22 = new TableAuthorityDOV2();
        tableAuthorityDOV22.setProjectId(222L);
        tableAuthorityDOV22.setProjectName("bb");
        tableAuthorityDOV22.setProjectCode("code2");
        tableAuthorityDOV2s.add(tableAuthorityDOV22);
        when(tableAuthorityDOMapperV2.selectByAppliedUserId(any())).thenReturn(tableAuthorityDOV2s);

        List<ProjectInfoDO> projectInfoDOs = new ArrayList<>();
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(111L);
        projectInfoDO.setProjectCode("code1");
        projectInfoDO.setProjectName("aa");
        projectInfoDOs.add(projectInfoDO);
        when(projectMemberMappingService.getProjectByUser(any())).thenReturn(projectInfoDOs);

        Map<TableInfoV2,List<ColumnInfoV2>> map = new HashMap<>();
        TableInfoV2 tableInfoV2 = new TableInfoV2();
        tableInfoV2.setTableInfoId("1alsdf-1211h-124hj12-lasdf1");
        tableInfoV2.setCreateTime(new Date());
        tableInfoV2.setIsOpen((byte) 1);
        tableInfoV2.setProjectId(111L);
        tableInfoV2.setProjectCode("code1");

        List<ColumnInfoV2> columnInfoV2s = new ArrayList<>();
        ColumnInfoV2 columnInfoV21 = new ColumnInfoV2();
        columnInfoV21.setColumnInfoId("aldalf-121l1l-124l1-111r23");
        columnInfoV21.setCreateTime(new Date());
        columnInfoV21.setColumnName("aa");
        columnInfoV2s.add(columnInfoV21);
        ColumnInfoV2 columnInfoV22 = new ColumnInfoV2();
        columnInfoV22.setColumnInfoId("1231l1-350l-124k-zdf9");
        columnInfoV22.setColumnName("bb");
        columnInfoV22.setCreateTime(new Date());
        columnInfoV2s.add(columnInfoV22);
        map.put(tableInfoV2, columnInfoV2s);

        when(tableInfoServiceV2.tableWithColumnsOfProject(any())).thenReturn(map);
        List<TableAuthorityDOV2> result = tableAuthorityServiceV2.selectByUserId("11111");
        assertThat(result).isNotNull();

        result.clear();
        map.clear();
        when(tableInfoServiceV2.tableWithColumnsOfProject(any())).thenReturn(map);
        result = tableAuthorityServiceV2.selectByUserId("11111");
        assertThat(result).isNotNull();

        tableAuthorityDOV2s.clear();
        result.clear();
        when(projectMemberMappingService.getProjectByUser(any())).thenReturn(projectInfoDOs);
        result = tableAuthorityServiceV2.selectByUserId("11111");
        assertThat(result).isNotNull();
    }

    @Test
    public void selectTableByUserIdTest(){
        List<TableAuthorityWithTableDOV2> list = new ArrayList<>();
        TableAuthorityWithTableDOV2 do1 = new TableAuthorityWithTableDOV2();
        do1.setProjectId(111L);
        do1.setProjectCode("code01");
        do1.setProjectName("name01");
        list.add(do1);
        TableAuthorityWithTableDOV2 do2 = new TableAuthorityWithTableDOV2();
        do2.setProjectId(222L);
        do2.setProjectCode("code02");
        do2.setProjectName("name02");
        list.add(do2);
        when(tableAuthorityDOMapperV2.selectTableByAppliedUserId(any())).thenReturn(list);

        List<ProjectInfoDO> projectInfoDOs = new ArrayList<>();
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(111L);
        projectInfoDO.setProjectCode("code01");
        projectInfoDO.setProjectName("name01");
        projectInfoDOs.add(projectInfoDO);
        when(projectMemberMappingService.getProjectByUser(any())).thenReturn(projectInfoDOs);

        Map<String,TableInfoV2>  tablesOfProject = new HashMap<>();
        TableInfoV2 tableInfoV2 = new TableInfoV2();
        tableInfoV2.setTableInfoId("1alsdf-1211h-124hj12-lasdf1");
        tableInfoV2.setCreateTime(new Date());
        tableInfoV2.setIsOpen((byte) 1);
        tableInfoV2.setProjectId(111L);
        tableInfoV2.setProjectCode("code01");
        tablesOfProject.put("aa",tableInfoV2);
        when(tableInfoServiceV2.tablesOfProject(any())).thenReturn(tablesOfProject);
        List<TableAuthorityWithTableDOV2> result = tableAuthorityServiceV2.selectTableByUserId("111");
        assertThat(result).isNotNull();
    }

    @Test
    public void selectByUserIdAndTableInfoTest(){
        String userId = "111";

        List<HashMap<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("guid","aldalf-121l1l-124l1-111r23");
        list.add(map);

        TableInfoV2 tableInfoV2 = new TableInfoV2();
        tableInfoV2.setTableInfoId("1alsdf-1211h-124hj12-lasdf1");
        tableInfoV2.setCreateTime(new Date());
        tableInfoV2.setIsOpen((byte) 1);
        tableInfoV2.setProjectId(111L);
        tableInfoV2.setColumns(list);

        List<ColumnInfoV2> columnInfoV2s = new ArrayList<>();
        ColumnInfoV2 columnInfoV21 = new ColumnInfoV2();
        columnInfoV21.setColumnInfoId("aldalf-121l1l-124l1-111r23");
        columnInfoV21.setCreateTime(new Date());
        columnInfoV21.setColumnName("aa");
        columnInfoV2s.add(columnInfoV21);
        ColumnInfoV2 columnInfoV22 = new ColumnInfoV2();
        columnInfoV22.setColumnInfoId("1231l1-350l-124k-zdf9");
        columnInfoV22.setColumnName("bb");
        columnInfoV22.setCreateTime(new Date());
        columnInfoV2s.add(columnInfoV22);

        List<TableAuthorityDOV2> authorizedProjects = new ArrayList<>();
        TableAuthorityDOV2 tableAuthorityDOV2 = new TableAuthorityDOV2();
        tableAuthorityDOV2.setColumnInfoId("1041h-129d-adfu1-gj23");
        tableAuthorityDOV2.setProjectId(111L);
        tableAuthorityDOV2.setProjectName("aa");
        authorizedProjects.add(tableAuthorityDOV2);

        List<ProjectInfoDO> projectInfoDOs = new ArrayList<>();
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(111L);
        projectInfoDO.setProjectCode("code1");
        projectInfoDO.setProjectName("aa");
        projectInfoDOs.add(projectInfoDO);
        when(projectMemberMappingService.getProjectByUser(any())).thenReturn(projectInfoDOs);
        List<String> result = tableAuthorityServiceV2.selectByUserIdAndTableInfo(userId, tableInfoV2, columnInfoV2s);
        assertThat(result).isNotNull();
        assertEquals(result.get(0), "aldalf-121l1l-124l1-111r23");

        result.clear();
        authorizedProjects.get(0).setProjectId(112L);
        projectInfoDOs.get(0).setProjectId(112L);
        when(projectMemberMappingService.getProjectByUser(any())).thenReturn(projectInfoDOs);
        when(tableAuthorityDOMapperV2.selectByUserIdAndTableInfoId(any(), any(), any())).thenReturn(authorizedProjects);
        result = tableAuthorityServiceV2.selectByUserIdAndTableInfo(userId, tableInfoV2, columnInfoV2s);
        assertEquals(result.get(0),"1041h-129d-adfu1-gj23");
    }
}
