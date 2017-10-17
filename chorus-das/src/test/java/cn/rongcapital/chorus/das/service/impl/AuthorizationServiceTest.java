package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.authorization.api.UserDataAuthorization;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationData;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataDW;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataFS;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataType;
import cn.rongcapital.chorus.das.entity.AuthorizationDetailCategory;
import cn.rongcapital.chorus.das.service.AuthorizationService;
import cn.rongcapital.chorus.das.service.impl.AuthorizationServiceImpl;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.when;

/**
 * Created by hhlfl on 2017-8-28.
 */

public class AuthorizationServiceTest {
    @Mock
    private UserDataAuthorization userDataAuthRangerHDFS;
    @Mock
    private UserDataAuthorization UserDataAuthorizationByRanger;
    @InjectMocks
    private AuthorizationServiceImpl authorizationService;
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void disable() throws Exception {
        String policyId = "13";
        String policyId2="14";
        Matcher<AuthorizationData> matcher = new Matcher<AuthorizationData>() {
            @Override
            public boolean matches(Object o) {
                if(o != null) return ((AuthorizationData)o).getAuthorizationId().equals(policyId);

                return false;
            }

            @Override
            public void describeMismatch(Object o, Description description) {

            }

            @Override
            public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {

            }

            @Override
            public void describeTo(Description description) {

            }
        };


        AuthorizationDataFS dataFS = new AuthorizationDataFS();
        dataFS.setEnabled(true);
        dataFS.setAuthorizationId(policyId);
        dataFS.setAuthorizationRepositoryType(AuthorizationDataType.FS);
        when(userDataAuthRangerHDFS.authorizationGet(argThat(matcher))).thenReturn(dataFS);

        when(userDataAuthRangerHDFS.authorizationUpdate(argThat(matcher))).thenReturn(true);

        boolean r = authorizationService.disable(policyId, AuthorizationDetailCategory.HDFS);
        assertTrue(r);

        r = authorizationService.disable(policyId2,AuthorizationDetailCategory.HDFS);
        assertTrue(r);

        when(userDataAuthRangerHDFS.authorizationGet(argThat(matcher))).thenThrow(new Exception("disable authorization Exception."));
        try{
            authorizationService.disable(policyId, AuthorizationDetailCategory.HDFS);
            assertFalse(true);
        }catch (Exception ex){
            assertTrue(true);
        }


        AuthorizationDataDW dw = new AuthorizationDataDW();
        dw.setEnabled(true);
        dw.setAuthorizationId(policyId);
        dw.setAuthorizationRepositoryType(AuthorizationDataType.DW);
        when(UserDataAuthorizationByRanger.authorizationGet(argThat(matcher))).thenReturn(dw);

        when(UserDataAuthorizationByRanger.authorizationUpdate(argThat(matcher))).thenReturn(true);

        r = authorizationService.disable(policyId, AuthorizationDetailCategory.HIVE);
        assertTrue(r);

        r = authorizationService.disable(policyId2,AuthorizationDetailCategory.HIVE);
        assertTrue(r);

        when(UserDataAuthorizationByRanger.authorizationGet(argThat(matcher))).thenThrow(new Exception("disable authorization Exception."));
        try{
            authorizationService.disable(policyId, AuthorizationDetailCategory.HIVE);
            assertFalse(true);
        }catch (Exception ex){
            assertTrue(true);
        }
    }

}