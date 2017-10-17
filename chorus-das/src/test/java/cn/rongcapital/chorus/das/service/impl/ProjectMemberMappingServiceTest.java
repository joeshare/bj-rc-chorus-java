package cn.rongcapital.chorus.das.service.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import cn.rongcapital.chorus.das.dao.ProjectMemberMappingDOMapper;
import cn.rongcapital.chorus.das.dao.ProjectMemberMappingMapper;
import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.ProjectMemberMapping;
import cn.rongcapital.chorus.das.entity.ProjectMemberRoleCount;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import cn.rongcapital.chorus.das.entity.ProjectMemberCount;
import cn.rongcapital.chorus.das.service.ProjectMemberMappingService;
import cn.rongcapital.chorus.das.service.impl.ProjectMemberMappingServiceImpl;
import cn.rongcapital.chorus.das.util.BasicSpringTest;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;


public class ProjectMemberMappingServiceTest{
    @Mock
    private ProjectMemberMappingDOMapper projectMemberMappingDOMapper;
    @Mock
    private ProjectMemberMappingMapper projectMemberMappingMapper;

    @InjectMocks
    private ProjectMemberMappingServiceImpl memberService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    /**
     * 
     * @author yunzhong
     * @time 2017年6月16日下午3:57:23
     */
    @Test
    public void teststatProjectMember(){
        Long projectId = 222597L;
        List<ProjectMemberRoleCount> counts = new ArrayList<>();
        when(projectMemberMappingDOMapper.statProjectMember(projectId)).thenReturn(counts);
        ProjectMemberCount count = memberService.statProjectMember(projectId);
        assertNotNull(count);
        assertNotNull(count.getDatas());
        assertTrue(count.getDatas().isEmpty());
    }

    @Test
    public void insert(){
        ProjectMemberMapping mm = new ProjectMemberMapping();
        mm.setProjectId(1l);
        mm.setUserId("2");
        ProjectInfoDO info = new ProjectInfoDO();
        info.setProjectMemberId(2l);

        int code = memberService.insert(mm);
        Assert.assertEquals(1,code);

        when(projectMemberMappingDOMapper.selectMemberByProjectIdAndUserId(1l,"2")).thenReturn(info);

        code = memberService.insert(mm);
        Assert.assertEquals(2,code);

    }

}
