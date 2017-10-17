package cn.rongcapital.chorus.das.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import cn.rongcapital.chorus.common.constant.CommonAttribute;
import cn.rongcapital.chorus.das.dao.CommonInfoMapper;
import cn.rongcapital.chorus.das.dao.JobUnexecutedMapper;
import cn.rongcapital.chorus.das.entity.CommonInfoDO;
import cn.rongcapital.chorus.das.service.JobDeploymentService;

/**
 * @author kevin.gong
 * @Time 2017年9月19日 下午3:34:01
 */
public class PlatformMaintenanceServiceImplTest {
    
    @InjectMocks
    private PlatformMaintenanceServiceImpl PlatformMaintenanceService;
    
    @Mock
    private JobUnexecutedMapper jobUnexecutedMapper;
    
    @Mock
    private JobDeploymentService jobDeploymentService;

    @Mock
    private CommonInfoMapper commonInfoMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testGetPlatformMaintenanceStatus() {
        CommonInfoDO commonInfoDO = new CommonInfoDO();
        commonInfoDO.setUserId(CommonAttribute.ALL_CHORUS_USER_ID);
        commonInfoDO.setAttributeId(CommonAttribute.MAINTENANCE_STATUS);
        commonInfoDO.setValue(String.valueOf(CommonAttribute.MAINTENANCE_STATUS_STARTED));
        when(commonInfoMapper.selectByUserIdAndAttrId("0", "0001")).thenReturn(commonInfoDO);
        
        assertEquals(0, PlatformMaintenanceService.getPlatformMaintenanceStatus());
    }

    @Test
    public void testSetPlatformMaintenanceStatus() {
        CommonInfoDO commonInfoDO = new CommonInfoDO();
        commonInfoDO.setUserId(CommonAttribute.ALL_CHORUS_USER_ID);
        commonInfoDO.setAttributeId(CommonAttribute.MAINTENANCE_STATUS);
        commonInfoDO.setValue(String.valueOf(CommonAttribute.MAINTENANCE_STATUS_STARTED));
        when(commonInfoMapper.update(commonInfoDO)).thenReturn(1);
        
        when(jobUnexecutedMapper.selectJobUnexecuted(0, 0)).thenReturn(null);
        PlatformMaintenanceService.setPlatformMaintenanceStatus(CommonAttribute.MAINTENANCE_STATUS_STARTED);
    }
}
