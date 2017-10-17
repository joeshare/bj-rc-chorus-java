package cn.rongcapital.chorus.resourcemanager.service.impl;

import cn.rongcapital.chorus.das.entity.HostInfo;
import cn.rongcapital.chorus.das.entity.ResourceTemplate;
import cn.rongcapital.chorus.das.service.InstanceHostService;
import cn.rongcapital.chorus.resourcemanager.ExecutionUnitGroup;
import cn.rongcapital.chorus.resourcemanager.service.CuratorCallBack;
import cn.rongcapital.chorus.resourcemanager.service.CuratorService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Created by abiton on 28/11/2016.
 */
public class ResourceManagerServiceImplTest {
    @Mock
    private CuratorService curatorService;
    @Mock
    private InstanceHostService instanceHostService;
    @InjectMocks
    private ResourceManagerServiceImpl resourceManagerService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testAllocateResource() throws Exception {
        Long instanceId = 1l;
        List<HostInfo> hosts = new ArrayList<>();
        HostInfo hostInfo = new HostInfo();
        hostInfo.setCpu(10);
        hostInfo.setHostId(1L);
        hostInfo.setHostName("host1");
        hostInfo.setMemory(1000);

        HostInfo hostInfo2 = new HostInfo();
        hostInfo2.setCpu(10);
        hostInfo2.setHostId(2L);
        hostInfo2.setHostName("host2");
        hostInfo2.setMemory(1000);


        HostInfo hostInfo3 = new HostInfo();
        hostInfo3.setCpu(10);
        hostInfo3.setHostId(3L);
        hostInfo3.setHostName("host3");
        hostInfo3.setMemory(1000);

        hosts.add(hostInfo);
        hosts.add(hostInfo2);
        hosts.add(hostInfo3);

        ExecutionUnitGroup eu = new ExecutionUnitGroup();
        eu.setGroupName("group1");
        eu.setProjectId(1L);
        eu.setResourceInnerId(1L);
        eu.setInstanceSize(4);
        ResourceTemplate template = new ResourceTemplate();
        template.setResourceCpu(2);
        template.setResourceMemory(2);
        eu.setResourceTemplate(template);

        doNothing().when(curatorService).executeWithLock(any(CuratorCallBack.class));

        resourceManagerService.allocateResource(instanceId, hosts, eu);
    }

    @Test
    public void testReAllocateResource() throws Exception {

    }

    @Test
    public void testDestroyResource() throws Exception {

    }
}