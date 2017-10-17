package cn.rongcapital.chorus.server.metadata.controller;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.entity.TreeNode;
import cn.rongcapital.chorus.das.service.TableLinageServiceV2;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by alan on 11/23/16.
 */
@Slf4j
@RunWith(PowerMockRunner.class)
public class TableLinageControllerTest {
    private MockMvc              mockMvc;
    @Mock
    private TableLinageServiceV2 tableLinageServiceV2;

    @InjectMocks
    private TableLinageController tableLinageController;
    private final String api = "/metadata/table_linage/";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tableLinageController).build();
    }

    @Test
    public void getTableLinage() throws Exception {
        String tableId = "1";
        String userId = "0";

        TreeNode treeNode = JSON.parseObject("{\"children\":[{\"children\":[{\"children\":[{\"children\":[{\"children\":[],\"nodeId\":\"t_100\",\"nodeName\":\"table100\",\"pathLevel\":4,\"type\":0}],\"nodeId\":\"j_2\",\"nodeName\":\"job2\",\"pathLevel\":3,\"type\":1}],\"nodeId\":\"t_2\",\"nodeName\":\"table2\",\"pathLevel\":2,\"type\":0},{\"children\":[{\"children\":[{\"children\":[],\"nodeId\":\"t_101\",\"nodeName\":\"table101\",\"pathLevel\":4,\"type\":0}],\"nodeId\":\"j_3\",\"nodeName\":\"job3\",\"pathLevel\":3,\"type\":1}],\"nodeId\":\"t_3\",\"nodeName\":\"table3\",\"pathLevel\":2,\"type\":0}],\"nodeId\":\"j_1\",\"nodeName\":\"job0\",\"pathLevel\":1,\"type\":1}],\"nodeId\":\"t_0\",\"nodeName\":\"table0\",\"pathLevel\":0,\"type\":0}",
                TreeNode.class);

        when(tableLinageServiceV2.getJobOutputTableLineage(tableId)).thenReturn(treeNode);
        mockMvc.perform(get(api + tableId + "?userId=" + userId))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
               .andExpect(jsonPath("$.data").isMap());

        when(tableLinageServiceV2.getJobOutputTableLineage(tableId)).thenThrow(Exception.class);
        mockMvc.perform(get(api + tableId + "?userId=" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SYSTEM_ERR.getCode()));
    }

}
