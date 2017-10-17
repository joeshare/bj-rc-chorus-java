package cn.rongcapital.chorus.modules.resource.kpi.snapshot;

import cn.rongcapital.chorus.modules.resource.kpi.snapshot.bean.PlatformResourceKPISnapshot;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsStatus;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by hhlfl on 2017-7-20.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class,PlatformResourceStats.class,PlatformResourceStatsTest.class})
@Configuration
public class PlatformResourceStatsTest {
    @Autowired
    private PlatformResourceStats platformResourceStats;

    @Bean
    YarnClient getYarnClient() throws IOException, YarnException {
        YarnClient yarnClient = mock(YarnClient.class);
        List<NodeReport> nodeReports = new ArrayList<>();
        NodeReport nodeReport1 = mock(NodeReport.class);
        NodeReport nodeReport2 = mock(NodeReport.class);
        Resource resource1 = mock(Resource.class);
        Resource resource2 = mock(Resource.class);
        when(nodeReport1.getCapability()).thenReturn(resource1);
        when(resource1.getVirtualCores()).thenReturn(2);
        when(resource1.getMemory()).thenReturn(4*1024);
        when(nodeReport2.getCapability()).thenReturn(resource2);
        when(resource2.getVirtualCores()).thenReturn(4);
        when(resource2.getMemory()).thenReturn(1*1024);

        nodeReports.add(nodeReport1);
        nodeReports.add(nodeReport2);
        when(yarnClient.getNodeReports(NodeState.RUNNING)).thenReturn(nodeReports);
        return yarnClient;
    }

    @Bean
    FileSystem fileSystem() throws IOException {
        FileSystem fileSystem = mock(FileSystem.class);
        FsStatus fsStatus = mock(FsStatus.class);
        when(fileSystem.getStatus()).thenReturn(fsStatus);
        when(fsStatus.getCapacity()).thenReturn(19L);
        platformResourceStats.setFileSystem(fileSystem);
        return fileSystem;
    }

    @Test
    public void snapshot() throws Exception {
        platformResourceStats.snapshot();
    }

    @Test
    public void statsPlatformTotalResource() throws Exception {
        PlatformResourceKPISnapshot kpi = new PlatformResourceKPISnapshot();
        platformResourceStats.statsPlatformTotalResource(kpi);

        Assert.assertEquals(6,kpi.getCpuTotal());
        Assert.assertEquals(5,kpi.getMemoryTotal());
        Assert.assertEquals(19l,kpi.getStorageTotal());

    }

}