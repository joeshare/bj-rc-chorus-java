package cn.rongcapital.chorus.das.service.impl;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.instance.AtlasEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cn.rongcapital.chorus.authorization.plugin.ranger.UserDataAuthorizationBase;
import cn.rongcapital.chorus.das.entity.AuthorizationDetail;
import cn.rongcapital.chorus.das.entity.AuthorizationDetailCategory;
import cn.rongcapital.chorus.das.service.AuthorizationDetailService;
import cn.rongcapital.chorus.das.service.HiveTableInfoServiceV2;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.das.service.impl.TableInfoServiceV2Impl;
import cn.rongcapital.chorus.governance.AtlasService;

/**
 * @author yunzhong
 *
 * @date 2017年9月11日下午2:00:40
 */
public class TableInfoServiceV2ImplMockTest {
    @InjectMocks
    private TableInfoServiceV2Impl tableInfoService;
    
    @Mock
    private AtlasService atlasService;
    @Mock
    private ProjectInfoService projectInfoService;
    
    @Mock
    private UserDataAuthorizationBase hiveRangerAuth;
    
    @Mock
    private AuthorizationDetailService authDetailService;
    
    @Mock
    private HiveTableInfoServiceV2 hiveTableInfoServiceV2;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDelete() {
        String tableInfoId = "testTableInfoId";
        AtlasEntity atlasEntity = new AtlasEntity();
        atlasEntity.setAttribute("project", "projectCode");
        atlasEntity.setAttribute("projectId", 100L);
        atlasEntity.setGuid("testTableInfoId");
        atlasEntity.setAttribute("name", "tableName");
        atlasEntity.setAttribute("unique", "projectCode.tableName");
        
        try {
            when(atlasService.getByGuid(tableInfoId)).thenReturn(atlasEntity);
        } catch (AtlasServiceException e1) {
            e1.printStackTrace();
            fail(e1.getLocalizedMessage());
        }
        try {
            when( atlasService.clean(any())).thenReturn(1);
        } catch (AtlasServiceException e1) {
            e1.printStackTrace();
            fail(e1.getLocalizedMessage());
        }
        List<AuthorizationDetail> details =new ArrayList<>();
        AuthorizationDetail detail = new AuthorizationDetail();
        detail.setCategory(AuthorizationDetailCategory.HIVE.name());
        String policyNamePre =  "chorus_projectCode_tableName";
        detail.setPolicyName(policyNamePre + "_W34ERTAF34TWE");
        detail.setPolicyId("policyId1");
        details.add(detail);
        
        AuthorizationDetail detail2 = new AuthorizationDetail();
        detail2.setCategory(AuthorizationDetailCategory.HIVE.name());
        detail2.setPolicyName(policyNamePre + "_W34ERTAF34T545");
        detail2.setPolicyId("policyId2");
        details.add(detail2 );
        when(authDetailService.selectByProjectId(anyLong())).thenReturn(details );
        
        when(authDetailService.updateByPrimaryKey(any())).thenReturn(1);
        when(hiveTableInfoServiceV2.delete(any())).thenReturn(true);
        
        try {
            when(hiveRangerAuth.disablePolicy(anyString())).thenReturn(true);
        } catch (Exception e1) {
            e1.printStackTrace();
            fail(e1.getLocalizedMessage());
        }
        
        try {
            tableInfoService.delete(tableInfoId);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
        
    }
}
