package cn.rongcapital.chorus.server.projectmembermapping.controller;

import cn.rongcapital.chorus.authorization.api.UserDataAuthorization;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationData;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataDW;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationPerm;
import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.entity.AuthorizationDetail;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.service.AuthorizationDetailService;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.das.service.ProjectMemberMappingService;
import cn.rongcapital.chorus.das.service.TRoleService;
import cn.rongcapital.chorus.server.projectmembermapping.controller.vo.ProjectMemberVo;
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
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by hhlfl on 2017-8-10.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
public class ProjectMemberMappingControllerTest {
    private MockMvc mockMvc;
    @Mock
    private ProjectInfoService projectInfoService;

    @Mock
    private ProjectMemberMappingService projectMemberMappingService;

    @Mock
    private AuthorizationDetailService authorizationDetailService;

    @Mock
    private TRoleService roleService;

    @Mock
    private UserDataAuthorization authorization;

    @InjectMocks
    private ProjectMemberMappingController projectMemberMappingController;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(projectMemberMappingController).build();
    }

    @Test
    public void update() throws Exception {
        ProjectMemberVo memberVo = new ProjectMemberVo();
        memberVo.setProjectId(1l);
        memberVo.setProjectMemberId(1l);
        memberVo.setRoleId("4");
        memberVo.setUserId("2");
        memberVo.setUserName("test");
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setProjectCode("p_test");
        projectInfo.setProjectId(memberVo.getProjectId());
        projectInfo.setCreateUserId("4");

        ArrayList userList = new ArrayList();
        userList.add("testa");
        AuthorizationDataDW authorizationData = new AuthorizationDataDW();
        authorizationData.setAuthorizationId("1");
        authorizationData.setAuthorizationName("test_policy_1");
        authorizationData.setEnabled(true);
        authorizationData.setDatabases(Collections.EMPTY_LIST);
        authorizationData.setTables(Collections.EMPTY_LIST);
        authorizationData.setColumns(Collections.EMPTY_LIST);
        List<AuthorizationPerm> perms = new ArrayList<>();
        AuthorizationPerm perm = new AuthorizationPerm();
        perm.setPermUserList(userList);
        perm.setPermGroupList(Collections.EMPTY_LIST);
        perm.setPermPermList(Collections.emptyList());
        perms.add(perm);
        authorizationData.setAuthorizationPermMapList(perms);

        //case1:develop->admin admin authorization not exist.
        when(projectMemberMappingService.updateByProjectAndUserId(any())).thenReturn(1);
        when(projectInfoService.selectByPrimaryKey(eq(memberVo.getProjectId()))).thenReturn(projectInfo);
        when(authorizationDetailService.selectByUserIdAndProjectId(eq(memberVo.getUserId()),eq(memberVo.getProjectId().toString()))).thenReturn(Collections.EMPTY_LIST);
        when(authorization.authorizationSearch(any())).thenReturn(null);
        when(authorizationDetailService.insertOrUpdate(any())).thenReturn(2);
        mockMvc.perform(put("/project/member/update").contentType(MediaType.APPLICATION_JSON_UTF8).content(JSONObject.toJSONString(memberVo)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
                ;

        //case2: develop->admin admin authorization exist and develop authorization not exist in ranger
        List<AuthorizationDetail> details = new ArrayList<>();
        AuthorizationDetail detail = new AuthorizationDetail();
        detail.setId(1);
        detail.setPolicyId("1");
        detail.setProjectId(memberVo.getProjectId());
        detail.setUserId(memberVo.getUserId());
        detail.setPolicyName("p_test_policy_1");
        details.add(detail);
        when(authorizationDetailService.selectByUserIdAndProjectId(eq(memberVo.getUserId()),eq(memberVo.getProjectId().toString()))).thenReturn(details);
        when(authorization.authorizationGet(any())).thenReturn(null);
        when(authorization.authorizationSearch(any())).thenReturn(authorizationData);
        when(authorization.authorizationUpdate(any())).thenReturn(true);

        mockMvc.perform(put("/project/member/update").contentType(MediaType.APPLICATION_JSON_UTF8).content(JSONObject.toJSONString(memberVo)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
        ;


        //case3:develop->admin develop authorization exist in ranger  and contains many users.
        when(authorization.authorizationGet(any())).thenReturn(authorizationData);
        when(authorization.authorizationUpdate(any())).thenReturn(false);
        mockMvc.perform(put("/project/member/update").contentType(MediaType.APPLICATION_JSON_UTF8).content(JSONObject.toJSONString(memberVo)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
        ;

        //case4 : admin develop authorization exist in ranger  and only contain current users
        userList.clear();
        userList.add(memberVo.getUserName());
        when(authorization.authorizationRemove(any())).thenReturn(true);
        when(authorizationDetailService.deleteByPrimaryKey(any())).thenReturn(1);
        mockMvc.perform(put("/project/member/update").contentType(MediaType.APPLICATION_JSON_UTF8).content(JSONObject.toJSONString(memberVo)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
        ;


    }

}