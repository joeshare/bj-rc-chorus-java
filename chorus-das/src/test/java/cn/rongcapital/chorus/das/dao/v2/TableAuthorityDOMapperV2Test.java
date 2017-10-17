package cn.rongcapital.chorus.das.dao.v2;

import cn.rongcapital.chorus.das.dao.v2.TableAuthorityDOMapperV2;
import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.TableAuthorityDOV2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yimin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@Configuration
@ContextConfiguration(classes = {
        TableAuthorityDOMapperV2Test.class,
        EmbeddedDataSourceConfiguration.class,
        MybatisAutoConfiguration.class
})
@TestPropertySource(properties = {
        "spring.datasource.schema=table_authority_v2.sql",
        "mybatis.mapperLocations=classpath*:com/rongcapital/chorus/das/dao/v2/TableAuthorityDOMapperV2.xml"
})
@Profile({"single"})
public class TableAuthorityDOMapperV2Test {
    private final String table_1_id = "alj0-fda-11";
    private final String table_2_id = "alj0-faa-11";
    private final String userId     = "1234";
    @Autowired
    private TableAuthorityDOMapperV2 authorityDOMapperV2;

    @Test
    public void projectsOfAuthorizedTable() throws Exception {
        List<ProjectInfoDO> projectInfoDOS = authorityDOMapperV2.projectsOfAuthorizedTable(userId);
        assertThat(projectInfoDOS).hasSize(1);
        ProjectInfoDO actual = projectInfoDOS.get(0);
        assertThat(actual.getProjectId()).isEqualTo(1L);
        assertThat(actual.getProjectCode()).isEqualTo("first_project_code");
        assertThat(actual.getProjectName()).isEqualTo("first_project_name");
        assertThat(actual.getUserId()).isEqualTo(userId);
    }

    @Test
    public void tablesOfProjectAndUser() throws Exception {
        List<TableAuthorityDOV2> authorityDOV2s = authorityDOMapperV2.tablesOfProjectAndUser(1L, userId);
        assertThat(authorityDOV2s).hasSize(2);
        TableAuthorityDOV2 table = authorityDOV2s.get(0);
        assertThat(table.getTableName()).isEqualTo("tableName1");
        assertThat(table.getTableInfoId()).isEqualTo(table_1_id);

        table = authorityDOV2s.get(1);
        assertThat(table.getTableName()).isEqualTo("tableName2");
        assertThat(table.getTableInfoId()).isEqualTo(table_2_id);
    }

    @Test
    public void columnsOfTable() throws Exception {
        assertThat(authorityDOMapperV2.columnsOfTable(table_1_id, userId)).hasSize(2);
        assertThat(authorityDOMapperV2.columnsOfTable(table_2_id, userId)).hasSize(1);
    }
}
