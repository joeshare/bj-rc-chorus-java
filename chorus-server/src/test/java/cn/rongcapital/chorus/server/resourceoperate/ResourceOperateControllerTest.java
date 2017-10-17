package cn.rongcapital.chorus.server.resourceoperate;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.entity.ResourceOperationDO;
import cn.rongcapital.chorus.das.service.ResourceOperationService;
import cn.rongcapital.chorus.server.resourceoperate.controller.ResourceOperateController;
import cn.rongcapital.chorus.server.resourceoperate.param.ResourceApplyQuery;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
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

import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by abiton on 25/11/2016.
 */
@Slf4j
@RunWith(PowerMockRunner.class)
public class ResourceOperateControllerTest {
    private MockMvc mockMvc;
    @Mock
    ResourceOperationService resourceOperateService;

    @InjectMocks
    ResourceOperateController resourceOperateController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(resourceOperateController).build();
    }

    @Test
    public void applyForProject() throws Exception {

        List<ResourceOperationDO> operationDOList = new Page<>();
        operationDOList.add(new ResourceOperationDO());
        operationDOList.add(new ResourceOperationDO());
        operationDOList.add(new ResourceOperationDO());
        operationDOList.add(new ResourceOperationDO());
        operationDOList.add(new ResourceOperationDO());
        operationDOList.add(new ResourceOperationDO());
        operationDOList.add(new ResourceOperationDO());
        operationDOList.add(new ResourceOperationDO());

        when(resourceOperateService.
                getResourceOperationByStatus(eq(StatusCode.RESOURCE_OPERATE_APPLY.getCode()),eq(1),eq(2)))
        .thenReturn(operationDOList);
        ResourceApplyQuery query = new ResourceApplyQuery();
        query.setStatus(StatusCode.RESOURCE_OPERATE_APPLY.getCode());

        mockMvc.perform(post("/resource/apply/1/2").contentType(MediaType.APPLICATION_JSON_UTF8).content(JSONObject.toJSONString(query)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data.pageNum").value(1));


    }





}
