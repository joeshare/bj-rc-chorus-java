package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.dao.ProjectMemberMappingMapper;
import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.ProjectMemberMapping;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by hhlfl on 2017-8-9.
 */
public class ProjectMemberMappingMapperTest extends H2BaseTest{
    @Autowired
    private ProjectMemberMappingMapper projectMemberMappingMapper;

    @Test
    public void getProject() throws Exception {
        Long projectId = 2L;
        String userId = "1234";
        ProjectInfoDO project = projectMemberMappingMapper.getProject(userId, projectId);
        assertThat(project).isNotNull();

        assertThat(project.getProjectId()).isEqualTo(projectId);
        assertThat(project.getUserId()).isEqualTo(userId);
        assertThat(project.getProjectCode()).isEqualTo("first_project_code");
        assertThat(project.getProjectName()).isEqualTo("first_project_name");
        assertThat(project.getRoleId()).isEqualTo("1");
        assertThat(project.getRoleCode()).isEqualTo("911");
        assertThat(project.getRoleName()).isEqualTo("数据开发人员");
    }

    @Test
    public void selectByPrimaryKey() throws Exception {
        ProjectMemberMapping po = projectMemberMappingMapper.selectByPrimaryKey(1l);
        Assert.assertNotNull(po);
        Assert.assertEquals(1l,po.getProjectId().longValue());
        Assert.assertEquals("1",po.getUserId());
        Assert.assertEquals("4",po.getRoleId());
        Assert.assertEquals(1l,po.getProjectMemberId().longValue());
        Assert.assertNotNull(po.getCreateTime());
    }

    @Test
    public void updateByPrimaryKey() throws Exception {
        ProjectMemberMapping po = new ProjectMemberMapping();
        po.setProjectMemberId(1l);
        po.setRoleId("4");
        po.setUserId("1");
        po.setProjectId(1l);
        long ms = System.currentTimeMillis();
        po.setUpdateTime(new Timestamp(ms));

        projectMemberMappingMapper.updateByPrimaryKey(po);

        ProjectMemberMapping npo = projectMemberMappingMapper.selectByPrimaryKey(po.getProjectMemberId());

        Assert.assertEquals(po.getProjectId(),npo.getProjectId());
        Assert.assertEquals(po.getUserId(), npo.getUserId());
        Assert.assertEquals(po.getRoleId(),npo.getRoleId());
        Assert.assertEquals(po.getUpdateTime().getTime(), npo.getUpdateTime().getTime());

    }
}
