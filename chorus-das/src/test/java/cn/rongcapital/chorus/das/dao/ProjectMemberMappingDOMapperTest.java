package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.dao.ProjectMemberMappingDOMapper;
import cn.rongcapital.chorus.das.dao.ProjectMemberMappingMapper;
import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.ProjectMemberMapping;
import org.apache.commons.net.ntp.TimeStamp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;

import static org.junit.Assert.*;

/**
 * Created by hhlfl on 2017-8-8.
 */
public class ProjectMemberMappingDOMapperTest extends H2BaseTest{
    @Autowired
    private ProjectMemberMappingDOMapper projectMemberMappingDOMapper;

    @Autowired
    private ProjectMemberMappingMapper projectMemberMappingMapper;


    @Test
    public void selectMemberByProjectIdAndUserId() throws Exception {
        ProjectInfoDO info = projectMemberMappingDOMapper.selectMemberByProjectIdAndUserId(1l,"1");
        Assert.assertNotNull(info);
        Assert.assertEquals(1l,info.getProjectId().longValue());
        Assert.assertEquals("1",info.getUserId());
        Assert.assertEquals("p01",info.getProjectName());
        Assert.assertEquals("p01",info.getProjectCode());
        Assert.assertEquals("项目Owner", info.getRoleName());

        info = projectMemberMappingDOMapper.selectMemberByProjectIdAndUserId(1l,"NULL");
        Assert.assertNull(info);
    }

    @Test
    public void updateByProjectAndUserId(){
        ProjectMemberMapping memberMapping = new ProjectMemberMapping();
        memberMapping.setProjectMemberId(1l);
        memberMapping.setProjectId(1l);
        memberMapping.setUserId("1");
        long ntps = System.currentTimeMillis();
        memberMapping.setUpdateTime(new Timestamp(ntps));
        memberMapping.setRoleId("4");
        int i = projectMemberMappingDOMapper.updateByProjectAndUserId(memberMapping);
        ProjectMemberMapping rMapping = projectMemberMappingMapper.selectByPrimaryKey(memberMapping.getProjectMemberId());
        Assert.assertEquals(memberMapping.getUserId(),rMapping.getUserId());
        Assert.assertEquals(memberMapping.getRoleId(), rMapping.getRoleId());
        Assert.assertEquals(memberMapping.getUpdateTime().getTime(), rMapping.getUpdateTime().getTime());
        Assert.assertEquals(memberMapping.getProjectId(), rMapping.getProjectId());

    }

}