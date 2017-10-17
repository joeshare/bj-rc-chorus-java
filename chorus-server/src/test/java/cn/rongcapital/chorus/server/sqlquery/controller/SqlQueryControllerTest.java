package cn.rongcapital.chorus.server.sqlquery.controller;

import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.TableAuthorityDOV2;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.service.ColumnInfoServiceV2;
import cn.rongcapital.chorus.das.service.ProjectMemberMappingService;
import cn.rongcapital.chorus.das.service.SqlQueryService;
import cn.rongcapital.chorus.das.service.TableAuthorityServiceV2;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.List;

import static cn.rongcapital.chorus.server.sqlquery.controller.NodeVO.APPLIED_COLUMNS;
import static cn.rongcapital.chorus.server.sqlquery.controller.NodeVO.APPLIED_TABLE;
import static cn.rongcapital.chorus.server.sqlquery.controller.NodeVO.AUTHORIZED_COLUMNS;
import static cn.rongcapital.chorus.server.sqlquery.controller.NodeVO.AUTHORIZED_TABLE;
import static cn.rongcapital.chorus.server.sqlquery.controller.NodeVO.PROJECT_AUTHORIZED;
import static cn.rongcapital.chorus.server.sqlquery.controller.NodeVO.PROJECT_TABLE_AUTHORIZED;
import static com.alibaba.fastjson.JSON.parseObject;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author yimin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SqlQueryController.class, SqlQueryControllerTest.class})
@Configuration
@WebAppConfiguration
@EnableWebMvc
public class SqlQueryControllerTest {
    private final String userId = "1234";
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc                     mockMvc;
    private List<ProjectInfoDO>         authorizedProjects;
    private List<ProjectInfoDO>         appliedProject;

    @Autowired
    private ColumnInfoServiceV2         columnInfoServiceV2;
    @Autowired
    private TableAuthorityServiceV2     tableAuthorityServiceV2;
    @Autowired
    private ProjectMemberMappingService projectMemberMappingService;
    @Autowired
    private TableInfoServiceV2          tableInfoServiceV2;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    private List<ProjectInfoDO> appliedProject() {
        appliedProject = new ArrayList<>();
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(2L);
        projectInfoDO.setProjectCode("p_code_2");
        projectInfoDO.setProjectName("项目2");
        appliedProject.add(projectInfoDO);
        projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(3L);
        projectInfoDO.setProjectCode("p_code_3");
        projectInfoDO.setProjectName("项目3");
        appliedProject.add(projectInfoDO);
        return appliedProject;
    }

    private List<ProjectInfoDO> authorizedProjects() {
        authorizedProjects = new ArrayList<>();
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(1L);
        projectInfoDO.setProjectCode("p_code_1");
        projectInfoDO.setProjectName("项目1");
        authorizedProjects.add(projectInfoDO);
        projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(2L);
        projectInfoDO.setProjectCode("p_code_2");
        projectInfoDO.setProjectName("项目2");
        authorizedProjects.add(projectInfoDO);
        return authorizedProjects;
    }

    @Test
    public void authorizedDataProjects() throws Exception {
        when(projectMemberMappingService.getProjectByUser(userId)).thenReturn(authorizedProjects());
        when(tableAuthorityServiceV2.projectsOfAuthorizedTables(userId)).thenReturn(appliedProject());

        MvcResult mvcResult = mockMvc.perform(get("/sqlQuery/tables/{userId}", userId))
                                     .andExpect(status().isOk())
                                     .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        List<NodeVO> nodes = JSONArray.parseArray(parseObject(content).getString("data"), NodeVO.class);
        assertThat(nodes.stream().filter(n -> StringUtils.equals(n.getType(), "project-authorized")).count()).isEqualTo(2L);
        NodeVO nodeVO = nodes.stream().filter(n -> !StringUtils.equals(n.getType(), PROJECT_AUTHORIZED)).findFirst().get();
        assertThat(nodeVO.getCode()).isEqualTo("3");
        assertThat(nodeVO.getText()).isEqualTo("chorus_p_code_3");
        assertThat(nodeVO.getType()).isEqualTo(PROJECT_TABLE_AUTHORIZED);
    }


    @Test
    public void grantedProjectTables() throws Exception {

        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(1L);
        projectInfoDO.setProjectCode("p_code_1");
        projectInfoDO.setProjectName("项目1");
        when(projectMemberMappingService.getProject(userId, 1L)).thenReturn(projectInfoDO);
        when(tableInfoServiceV2.listAllTableInfo(1L, 1, 100)).thenReturn(_factory(100));
        when(tableInfoServiceV2.listAllTableInfo(1L, 2, 100)).thenReturn(_factory(1));

        MvcResult mvcResult = mockMvc.perform(get("/sqlQuery/tables/{userId}", userId).param("parent", "1").param("parentType", PROJECT_AUTHORIZED))
                                     .andExpect(status().isOk())
                                     .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        List<NodeVO> nodes = JSONArray.parseArray(parseObject(content).getString("data"), NodeVO.class);
        assertThat(nodes).hasSize(101);
        assertThat(nodes.stream().filter(n -> StringUtils.equals(n.getType(), NodeVO.AUTHORIZED_TABLE)).count()).isEqualTo(101L);
    }

    @Test
    public void appliedTables() throws Exception {
        when(tableAuthorityServiceV2.tablesOfProjectAndUser(3L, userId)).thenReturn(_appliedTable(10, 3L));

        MvcResult mvcResult = mockMvc.perform(get("/sqlQuery/tables/{userId}", userId).param("parent", "3").param("parentType", NodeVO.PROJECT_TABLE_AUTHORIZED))
                                     .andExpect(status().isOk())
                                     .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        List<NodeVO> nodes = JSONArray.parseArray(parseObject(content).getString("data"), NodeVO.class);
        assertThat(nodes).hasSize(10);
        assertThat(nodes.stream().filter(n -> StringUtils.equals(n.getType(), APPLIED_TABLE)).count()).isEqualTo(10L);
        assertThat(nodes.stream().filter(NodeVO::isLeaf).count()).isEqualTo(0);
    }

    @Test
    public void grantedProjectTableColumns() throws Exception {
        String grantedProjectTableId = randomAlphabetic(10);
        when(columnInfoServiceV2.selectColumnInfo(grantedProjectTableId)).thenReturn(_columns(101));
        MvcResult mvcResult = mockMvc.perform(get("/sqlQuery/tables/{userId}", userId).param("parent", grantedProjectTableId).param("parentType", AUTHORIZED_TABLE))
                                     .andExpect(status().isOk())
                                     .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        List<NodeVO> nodes = JSONArray.parseArray(parseObject(content).getString("data"), NodeVO.class);
        assertThat(nodes).hasSize(101);
        assertThat(nodes.stream().filter(n -> StringUtils.equals(n.getType(), AUTHORIZED_COLUMNS)).count()).isEqualTo(101L);
        assertThat(nodes.stream().filter(NodeVO::isLeaf).count()).isEqualTo(101);
    }

    @Test
    public void appliedTableColumns() throws Exception {
        String appliedTableId = randomAlphabetic(10);
        when(tableAuthorityServiceV2.columnsOfTable(userId, appliedTableId)).thenReturn(_appliedColumns(10, appliedTableId));
        MvcResult mvcResult = mockMvc.perform(get("/sqlQuery/tables/{userId}", userId).param("parent", appliedTableId).param("parentType", APPLIED_TABLE))
                                     .andExpect(status().isOk())
                                     .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        List<NodeVO> nodes = JSONArray.parseArray(parseObject(content).getString("data"), NodeVO.class);
        assertThat(nodes).hasSize(10);
        assertThat(nodes.stream().filter(n -> StringUtils.equals(n.getType(), APPLIED_COLUMNS)).count()).isEqualTo(10L);
    }

    @Test
    public void exception() throws Exception {
        when(projectMemberMappingService.getProjectByUser(userId)).thenThrow(new RuntimeException("mock exception"));
        MvcResult mvcResult = mockMvc.perform(get("/sqlQuery/tables/{userId}", userId))
                                     .andExpect(status().isOk())
                                     .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        assertThat(parseObject(content).getString("code")).isEqualTo("-1");

    }

    @Bean
    SqlQueryService service() {
        return Mockito.mock(SqlQueryService.class);
    }

    @Bean
    TableAuthorityServiceV2 tableAuthorityServiceV2() {
        return Mockito.mock(TableAuthorityServiceV2.class);
    }

    private List<TableAuthorityDOV2> _appliedColumns(int num, String tableId) {
        List<TableAuthorityDOV2> result = new ArrayList<>(num);
        while (num-- > 0) {
            TableAuthorityDOV2 tableInfo = new TableAuthorityDOV2();
            tableInfo.setTableInfoId(tableId);
            tableInfo.setColumnInfoId(randomAlphabetic(10));
            tableInfo.setColumnName(randomAlphabetic(5));
            result.add(tableInfo);
        }
        return result;
    }

    private List<TableAuthorityDOV2> _appliedTable(int num, long projectId) {
        List<TableAuthorityDOV2> result = new ArrayList<>(num);
        while (num-- > 0) {
            TableAuthorityDOV2 tableInfo = new TableAuthorityDOV2();
            tableInfo.setProjectId(projectId);
            tableInfo.setTableInfoId(randomAlphabetic(10));
            tableInfo.setTableName(randomAlphabetic(5));
            result.add(tableInfo);
        }
        return result;
    }

    @Bean
    ProjectMemberMappingService projectMemberMappingService() {
        return Mockito.mock(ProjectMemberMappingService.class);
    }

    @Bean
    TableInfoServiceV2 tableInfoServiceV2() {
        return Mockito.mock(TableInfoServiceV2.class);
    }

    private List<TableInfoV2> _factory(int num) {
        List<TableInfoV2> result = new ArrayList<>(num);
        while (num-- > 0) {
            result.add(TableInfoV2.builder().tableInfoId(randomAlphabetic(10)).tableName(randomAlphabetic(5)).build());
        }
        return result;
    }

    @Bean
    ColumnInfoServiceV2 columnInfoServiceV2() {
        return Mockito.mock(ColumnInfoServiceV2.class);
    }

    private List<ColumnInfoV2> _columns(int num) {
        List<ColumnInfoV2> result = new ArrayList<>(num);
        while (num-- > 0) {
            result.add(ColumnInfoV2.builder().tableInfoId(randomAlphabetic(10)).columnInfoId(randomAlphabetic(5)).columnName(randomAlphabetic(5)).build());
        }
        return result;
    }
}
