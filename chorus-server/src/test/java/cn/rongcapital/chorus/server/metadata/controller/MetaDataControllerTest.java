package cn.rongcapital.chorus.server.metadata.controller;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.das.service.HiveTableInfoServiceV2;
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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by alan on 11/24/16.
 */
@Slf4j
@RunWith(PowerMockRunner.class)
public class MetaDataControllerTest {
    private MockMvc                mockMvc;
    @Mock
    private HiveTableInfoServiceV2 hiveTableInfoServiceV2;

    @InjectMocks
    private MetaDataController metaDataController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(metaDataController).build();
    }

    @Test
    public void sampleData() throws Exception {
        String  tableId = "1";
        int size = 2;
        String userId = "3";
        String id = "id";
        String name = "name";
        List<Map<String, Object>> res = new LinkedList<>();
        Map<String, Object> map0 = new LinkedHashMap<>();
        map0.put(id, 1);
        map0.put(name, "milk");
        Map<String, Object> map1 = new LinkedHashMap<>();
        map1.put(id, 2);
        map1.put(name, "chocolate");
        res.add(map0);
        res.add(map1);

        when(hiveTableInfoServiceV2.getSampleDataFromHive(tableId, size)).thenReturn(res);
        mockMvc.perform(get("/metadata/sample_data/" + tableId + "/" + size + "?userId=" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.data").isArray());

        ServiceException se = new ServiceException(StatusCode.TABLE_NOT_EXISTS);
        when(hiveTableInfoServiceV2.getSampleDataFromHive(tableId, size)).thenThrow(se).thenThrow(Exception.class);
        mockMvc.perform(get("/metadata/sample_data/" + tableId + "/" + size + "?userId=" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.TABLE_NOT_EXISTS.getCode()));

        mockMvc.perform(get("/metadata/sample_data/" + tableId + "/" + size + "?userId=" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SYSTEM_ERR.getCode()));
    }

}
