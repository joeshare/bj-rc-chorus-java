package cn.rongcapital.chorus.server.metadata.controller;

import cn.rongcapital.chorus.authorization.api.UserDataAuthorization;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationData;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationPerm;
import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.entity.*;
import cn.rongcapital.chorus.das.service.*;
import cn.rongcapital.chorus.server.metadata.param.ApplyStatusParam;
import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by hhlfl on 2017-8-10.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
public class DataAccessControllerTest {
    private MockMvc mockMvc;
    @Mock
    private ApplyFormServiceV2 applyFormService;
    @Mock
    private ApplyDetailServiceV2 applyDetailService;
    @Mock
    private TableAuthorityServiceV2 tableAuthorityServiceV2;
    @Mock
    private TableInfoServiceV2 tableInfoServiceV2;
    @Mock
    private ColumnInfoServiceV2 columnInfoServiceV2;
    @Mock
    private ProjectInfoService         projectInfoService;
    @Mock
    private AuthorizationDetailService authorizationDetailService;
    @Mock
    private UserDataAuthorization authorization;
    @InjectMocks
    private DataAccessController dataAccessController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dataAccessController).build();
    }

    @Test
    public void approve() throws Exception {
        //case 1: 被拒绝
        ApplyStatusParam param = new ApplyStatusParam();
        param.setApplyFormId(1l);
        param.setDealInstruction("拒绝");
        param.setDealUserId("2");
        param.setStatusCode(StatusCode.APPLY_TREATED.getCode());
        param.setUserName("apply");

        ApplyFormV2 applyFormV2 = new ApplyFormV2();
        applyFormV2.setProjectId(1l);
        applyFormV2.setStatusCode(param.getStatusCode());
        applyFormV2.setApplyUserId("2");
        applyFormV2.setApplyUserName("test");
        applyFormV2.setTableInfoId("table01");
        TableInfoV2 tableInfoV2 = new TableInfoV2();
        tableInfoV2.setTableName("t_test");
        tableInfoV2.setProjectId(1l);
        List<ApplyDetailDOV2> applyDetailDOList = new ArrayList<>();
        ApplyDetailDOV2 detailDOV2 = new ApplyDetailDOV2();
        detailDOV2.setApplyDetailId(1l);
        detailDOV2.setColumnDesc("col1");
        detailDOV2.setColumnName("colA");

        ApplyDetailDOV2 detailDOV22 = new ApplyDetailDOV2();
        detailDOV22.setApplyDetailId(2l);
        detailDOV22.setColumnDesc("col2");
        detailDOV22.setColumnName("colC");

        ApplyDetailDOV2 detailDOV222 = new ApplyDetailDOV2();
        detailDOV222.setApplyDetailId(3l);
        detailDOV222.setColumnDesc("col3");
        detailDOV222.setColumnName("colB");

        applyDetailDOList.add(detailDOV2);
        applyDetailDOList.add(detailDOV22);
        applyDetailDOList.add(detailDOV222);

        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setProjectId(1l);
        projectInfo.setProjectCode("p_test");

        when(applyFormService.selectByPrimaryKey(eq(param.getApplyFormId()))).thenReturn(applyFormV2);
        when(tableInfoServiceV2.selectByID(eq(applyFormV2.getTableInfoId()))).thenReturn(tableInfoV2);
        when(applyDetailService.selectApplyFormDetail(eq(param.getApplyFormId()))).thenReturn(applyDetailDOList);
        when(projectInfoService.selectByPrimaryKey(eq(tableInfoV2.getProjectId()))).thenReturn(projectInfo);
//        when(authorization.authorizationSearch(any())).thenThrow(new Exception("this case can not call this method."));
        mockMvc.perform(post("/data_access/approve").contentType(MediaType.APPLICATION_JSON_UTF8).content(JSONObject.toJSONString(param)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
        ;

        AuthorizationData authorizationData = new AuthorizationData();
        authorizationData.setAuthorizationId("1");
        authorizationData.setAuthorizationName("test_policy_1");
        List<AuthorizationPerm> perms = new ArrayList<>();
        AuthorizationPerm perm = new AuthorizationPerm();
//        perm.setPermUserList();
        authorizationData.setAuthorizationPermMapList(perms);

        //case2: 通过并插入.
        param.setStatusCode(StatusCode.APPLY_UNTREATED.getCode());
        when(authorization.authorizationSearch(any())).thenReturn(null);
        when(authorization.authorizationAdd(any())).thenReturn(authorizationData);
        when(authorizationDetailService.insertOrUpdate(any())).thenReturn(1);
        mockMvc.perform(post("/data_access/approve").contentType(MediaType.APPLICATION_JSON_UTF8).content(JSONObject.toJSONString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
        ;

        //case3:通过并更新
        when(authorization.authorizationSearch(any())).thenReturn(authorizationData);
        when(authorization.authorizationUpdate(any())).thenReturn(true);
        when(authorizationDetailService.insertOrUpdate(any())).thenReturn(1);
        mockMvc.perform(post("/data_access/approve").contentType(MediaType.APPLICATION_JSON_UTF8).content(JSONObject.toJSONString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
        ;


        //case3:通过并 但更新失败
        when(authorization.authorizationSearch(any())).thenReturn(authorizationData);
        when(authorization.authorizationUpdate(any())).thenReturn(false);
        when(authorizationDetailService.insertOrUpdate(any())).thenReturn(1);
        mockMvc.perform(post("/data_access/approve").contentType(MediaType.APPLICATION_JSON_UTF8).content(JSONObject.toJSONString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SYSTEM_ERR.getCode()))
        ;

    }

}