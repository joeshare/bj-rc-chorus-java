package cn.rongcapital.chorus.server.dynamic_source.controller;

import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.service.ColumnInfoServiceV2;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import cn.rongcapital.chorus.server.dynamic_source.param.ProjectIdParam;
import cn.rongcapital.chorus.server.dynamic_source.param.TableIdParamV2;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by abiton on 18/07/2017.
 */
@Slf4j
@RunWith(PowerMockRunner.class)
public class InternalDataSourceControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    InternalDataSourceController controller;
    @Mock
    private TableInfoServiceV2  tableInfoService;
    @Mock
    private ColumnInfoServiceV2 columnInfoService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @After
    public void tearDown() {
        System.out.println("InternalDataSourceControlelr test end......");
    }

    @Test
    public void listTables() throws Exception {
        long projectId = 10000;

        ProjectIdParam param = new ProjectIdParam();
        param.setProjectId(projectId);
        List<TableInfoV2> tables = new ArrayList<>();
        TableInfoV2 table1 = new TableInfoV2();
        table1.setTableName("table1");
        table1.setTableInfoId("1");
        tables.add(table1);

        TableInfoV2 table2 = new TableInfoV2();
        table2.setTableName("table2");
        table2.setTableInfoId("2");
        tables.add(table2);

        when(tableInfoService.listAllTableInfo(eq(projectId), anyInt(), anyInt())).thenReturn(tables);
        mockMvc.perform(post("/internal_datasource/table").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSONObject.toJSONString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].id").value("1"))
                .andExpect(jsonPath("$.data.[0].name").value("table1"))
                .andExpect(jsonPath("$.data.[1].id").value("2"))
                .andExpect(jsonPath("$.data.[1].name").value("table2"))
                ;
    }

    @Test
    public void listFields() throws Exception {

        String  tableId = "100001";
        TableIdParamV2 param = new TableIdParamV2();
        param.setTableId(tableId);
        List<ColumnInfoV2> columns = new ArrayList<>();
        ColumnInfoV2 col1 = new ColumnInfoV2();
        col1.setColumnName("col1");
        ColumnInfoV2 col2 = new ColumnInfoV2();
        col2.setColumnName("col2");
        ColumnInfoV2 col3 = new ColumnInfoV2();
        col3.setColumnName("col3");
        columns.add(col1);
        columns.add(col2);
        columns.add(col3);

        when(columnInfoService.selectColumnInfo(eq(tableId))).thenReturn(columns);

        mockMvc.perform(post("/internal_datasource/field").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSONObject.toJSONString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].name").value("col1"))
                .andExpect(jsonPath("$.data.[1].name").value("col2"))
                .andExpect(jsonPath("$.data.[2].name").value("col3"))
        ;

    }

    @Test
    public void listDynamicPartitions() throws Exception {

        String tableId = "100001";
        TableIdParamV2 param = new TableIdParamV2();
        param.setTableId(tableId);
        List<ColumnInfoV2> columns = new ArrayList<>();
        ColumnInfoV2 col1 = new ColumnInfoV2();
        col1.setColumnName("col1");
        col1.setIsPartitionKey((byte)1);
        ColumnInfoV2 col2 = new ColumnInfoV2();
        col2.setColumnName("col2");
        col2.setIsPartitionKey((byte)0);
        ColumnInfoV2 pdate = new ColumnInfoV2();
        pdate.setColumnName("p_date");
        pdate.setIsPartitionKey((byte)1);
        ColumnInfoV2 col3 = new ColumnInfoV2();
        col3.setColumnName("col3");
        col3.setIsPartitionKey((byte)1);
        columns.add(col1);
        columns.add(col2);
        columns.add(pdate);
        columns.add(col3);

        when(columnInfoService.selectColumnInfo(eq(tableId))).thenReturn(columns);

        mockMvc.perform(post("/internal_datasource/partition").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSONObject.toJSONString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].name").value("col1"))
                .andExpect(jsonPath("$.data.[2].name").value("col3"))
        ;

    }

}
