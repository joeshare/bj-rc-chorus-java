package cn.rongcapital.chorus.server.dynamic_source.controller;

import cn.rongcapital.chorus.das.entity.ExternalDataSourceCSVField;
import cn.rongcapital.chorus.das.entity.ExternalDataSourceRDBField;
import cn.rongcapital.chorus.das.entity.ExternalDataSourceRDBTable;
import cn.rongcapital.chorus.das.entity.ResourceOut;
import cn.rongcapital.chorus.das.service.ExternalDataSourceService;
import cn.rongcapital.chorus.das.service.ResourceOutService;
import cn.rongcapital.chorus.server.database.controller.util.MySQLUtils;
import cn.rongcapital.chorus.server.dynamic_source.param.ExternalDataSourceCSVFieldParam;
import cn.rongcapital.chorus.server.dynamic_source.param.ExternalDataSourceRDBFieldParam;
import cn.rongcapital.chorus.server.dynamic_source.param.ExternalDataSourceRDBParam;
import cn.rongcapital.chorus.server.dynamic_source.param.ProjectIdParam;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by abiton on 18/07/2017.
 */
@Slf4j
@RunWith(PowerMockRunner.class)
@PrepareForTest(MySQLUtils.class)
public class ExternalDataSourceControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    ExternalDataSourceController controller;
    @Mock
    ResourceOutService resourceOutService;
    @Mock
    ExternalDataSourceService externalDataSourceService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @After
    public void tearDown() {
        System.out.println("external datasource controller test, end......");
    }

    @Test
    public void listRDBs() throws Exception {
        List<ResourceOut> resourceOutList = new ArrayList<>();
        ResourceOut out = new ResourceOut();
        out.setConnUrl("jdbc:mysql://localhost/abiton");
        out.setConnUser("root");
        out.setConnPass("admin");
        out.setResourceName("externalRDB1");
        out.setResourceOutId(22L);
        resourceOutList.add(out);
        ProjectIdParam param = new ProjectIdParam();
        param.setProjectId(10000L);
        when(resourceOutService.selectResourceOutByProjectId(eq(10000L),1)).thenReturn(resourceOutList);

        mockMvc.perform(post("/external_datasource/rdb").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSONObject.toJSONString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data.[0].connUrl").value("jdbc:mysql://localhost/abiton"))
                .andExpect(jsonPath("$.data.[0].connUser").value("root"))
                .andExpect(jsonPath("$.data.[0].connPass").value("admin"))
                .andExpect(jsonPath("$.data.[0].name").value("externalRDB1"))
                .andExpect(jsonPath("$.data.[0].id").value(22))
        ;
    }

    @Test
    public void listTables() throws Exception {
        String url = "jdbc:mysql://localhost/abiton";
        String user = "root";
        String password = "admin";
        ExternalDataSourceRDBParam param = new ExternalDataSourceRDBParam();
        param.setUrl(url);
        param.setPassword(password);
        param.setUserName(user);
        List<ExternalDataSourceRDBTable> tableList = new ArrayList<>();
        ExternalDataSourceRDBTable table1 = new ExternalDataSourceRDBTable();
        table1.setName("table1");
        tableList.add(table1);

        ExternalDataSourceRDBTable table2 = new ExternalDataSourceRDBTable();
        table2.setName("table2");
        tableList.add(table2);

        when(externalDataSourceService.listRDBTables(eq(url), eq(user), eq(password))).thenReturn(tableList);

        mockMvc.perform(post("/external_datasource/table").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSONObject.toJSONString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data.[0].name").value("table1"))
                .andExpect(jsonPath("$.data.[1].name").value("table2"))
        ;
    }

    @Test
    public void listFields() throws Exception {
        String url = "jdbc:mysql://localhost/abiton";
        String user = "root";
        String password = "admin";
        String table = "table1";
        ExternalDataSourceRDBFieldParam param = new ExternalDataSourceRDBFieldParam();
        param.setUrl(url);
        param.setPassword(password);
        param.setUserName(user);
        param.setTable(table);

        List<ExternalDataSourceRDBField> fieldList = new ArrayList<>();
        ExternalDataSourceRDBField field1 = new ExternalDataSourceRDBField();
        field1.setName("col1");
        fieldList.add(field1);
        ExternalDataSourceRDBField field2 = new ExternalDataSourceRDBField();
        field2.setName("col2");
        fieldList.add(field2);
        when(externalDataSourceService.listRDBFields(eq(url),eq(user),eq(password),eq(table))).thenReturn(fieldList);


        mockMvc.perform(post("/external_datasource/field").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSONObject.toJSONString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data.[0].name").value("col1"))
                .andExpect(jsonPath("$.data.[1].name").value("col2"))
        ;

    }

    @Test
    public void listCsvFields() throws Exception {

        String csvFilePath = "/user/maboxiao/csv/2017-09-05/notHaveTiile.csv";
        String dwUserName = "maboxiao";
        String hasTitle = "NOT_HAVE";
        ExternalDataSourceCSVFieldParam param = new ExternalDataSourceCSVFieldParam();
        param.setCsvFilePath(csvFilePath);
        param.setDwUserName(dwUserName);
        param.setHasTitle(hasTitle);

        List<ExternalDataSourceCSVField> fieldList = new ArrayList<>();
        ExternalDataSourceCSVField field1 = new ExternalDataSourceCSVField();
        field1.setName("col1");
        fieldList.add(field1);
        ExternalDataSourceCSVField field2 = new ExternalDataSourceCSVField();
        field2.setName("col2");
        fieldList.add(field2);
        when(externalDataSourceService.listCSVFields(eq(csvFilePath),eq(dwUserName),eq(hasTitle))).thenReturn(fieldList);


        mockMvc.perform(post("/external_datasource/csvfield").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSONObject.toJSONString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data.[0].name").value("col1"))
                .andExpect(jsonPath("$.data.[1].name").value("col2"))
        ;

    }

    @Test
    public void getCSVSample() throws Exception {

        String csvFilePath = "/user/maboxiao/csv/2017-09-05/haveTiile.csv";
        String dwUserName = "maboxiao";
        String hasTitle = "HAVE";
        Integer displayLineCount = 2;
        ExternalDataSourceCSVFieldParam param = new ExternalDataSourceCSVFieldParam();
        param.setCsvFilePath(csvFilePath);
        param.setDwUserName(dwUserName);
        param.setHasTitle(hasTitle);
        param.setDisplayLineCount(displayLineCount);

        String result = "name,age,address,tel<br/>11,22,33,44<br/>55,66,77,88";
        when(externalDataSourceService.getCSVSample(eq(csvFilePath),eq(dwUserName),eq(hasTitle), eq(displayLineCount))).thenReturn(result);


        mockMvc.perform(post("/external_datasource/csvsample").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSONObject.toJSONString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data").value("name,age,address,tel<br/>11,22,33,44<br/>55,66,77,88"));
        ;

    }
}
