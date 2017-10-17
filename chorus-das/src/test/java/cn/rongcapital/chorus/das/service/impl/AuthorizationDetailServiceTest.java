package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.AuthorizationDetailDOMapper;
import cn.rongcapital.chorus.das.dao.AuthorizationDetailMapper;
import cn.rongcapital.chorus.das.entity.AuthorizationDetail;
import cn.rongcapital.chorus.das.service.impl.AuthorizationDetailServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by hhlfl on 2017-8-10.
 */
public class AuthorizationDetailServiceTest {
    @Mock
    private AuthorizationDetailDOMapper authorizationDetailDOMapper;
    @Mock
    private AuthorizationDetailMapper authorizationDetailMapper;
    @InjectMocks
    private AuthorizationDetailServiceImpl authorizationDetailService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void insertOrUpdate() throws Exception {
        int code = authorizationDetailService.insertOrUpdate(null);
        Assert.assertEquals(0, code);

        when(authorizationDetailMapper.insert(any())).thenReturn(1);
        when(authorizationDetailMapper.updateByPrimaryKey(any())).thenReturn(1);

        AuthorizationDetail old = new AuthorizationDetail();
        old.setId(2);
        old.setPolicyName("test_policy_column");
        old.setProjectId(1l);
        old.setUserId("1");

        AuthorizationDetail record = new AuthorizationDetail();
        record.setProjectId(1l);
        record.setUserId("1");
        record.setPolicyName("test_policy_column");
        code = authorizationDetailService.insertOrUpdate(record);
        Assert.assertEquals(1, code);
        when(authorizationDetailDOMapper.selectByUserIdAndProjectIdAndName(eq("1"), eq("1"), eq("test_policy_column"))).thenReturn(old);
        code = authorizationDetailService.insertOrUpdate(record);
        Assert.assertEquals(2, code);
        Assert.assertEquals(2, record.getId().intValue());
    }

}