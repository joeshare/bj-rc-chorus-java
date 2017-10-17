package cn.rongcapital.chorus.test.monitor.ranger;

import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.monitor.ranger.HdfsPolicyProjectFinder;
import cn.rongcapital.chorus.monitor.ranger.HivePolicyProjectFinder;
import cn.rongcapital.chorus.monitor.ranger.RangerAuditModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

public class HdfsPolicyProjectFinderTest {

    @Mock
    private ProjectInfoService projectInfoService;
    @InjectMocks
    private HdfsPolicyProjectFinder hdfsPolicyProjectFinder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFind() {
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setProjectCode("mbxpro5");
        when(projectInfoService.selectByProjectCode("mbxpro5")).thenReturn(projectInfo);
        RangerAuditModel auditModel = new RangerAuditModel();
        auditModel.setResource("/chorus/project/mbxpro5/testa/aa");
        ProjectInfo findProj = hdfsPolicyProjectFinder.find(auditModel);
        Assert.assertEquals(projectInfo.getProjectCode(), findProj.getProjectCode());
    }

}
