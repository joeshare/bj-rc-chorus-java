package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.dao.ResourceOutMapper;
import cn.rongcapital.chorus.das.entity.ResourceOut;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
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

import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yimin
 */

@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@Configuration
@ContextConfiguration(classes = {
        ResourceOutDOMapperTest.class,
        EmbeddedDataSourceConfiguration.class,
        MybatisAutoConfiguration.class
})
@TestPropertySource(properties = {
        "spring.datasource.schema=cn/rongcapital/chorus/das/dao/resource_out.sql",
        "mybatis.mapperLocations=classpath*:cn/rongcapital/chorus/das/dao/ResourceOutMapper.xml"
})
@Profile({"single"})
public class ResourceOutDOMapperTest {
    @Autowired
    private ResourceOutMapper resourceOutMapper;

    @Test
    public void insert() throws Exception {
        ResourceOut record = new ResourceOut();
        record.setProjectId(222667L);
        record.setResourceName(randomAlphabetic(20));
        record.setResourceDesc(randomAlphabetic(20));
        record.setStorageType("1");
        record.setConnUrl("jdbc:mysql://10.200.48.79:3306/chorus");
        record.setConnPort("3306");
        record.setConnUser("dps");
        record.setConnPass("Dps@10.200.48.MySQL");
        record.setCreateUserId("10970");
        record.setCreateTime(new Date());
        record.setDatabaseName("chorus");
        record.setConnHost("10.200.48.79");
        record.setCreateUserName("guoyemeng");
        record.setGuid("");

        assertThat(record.getResourceOutId()).isNull();
        resourceOutMapper.insert(record);
        assertThat(record.getResourceOutId()).isNotNull();
    }

    @Test
    public void unSyncedResourceOut() throws Exception {
        final List<ResourceOut> resourceOuts = resourceOutMapper.unSyncedResourceOut(1);
        assertThat(resourceOuts).hasSize(1);
        assertThat(resourceOuts.get(0).getGuid()).isEmpty();
    }

    @Test
    public void updateGuid() throws Exception {
        final String guid = randomAlphabetic(20);
        final long resourceOutId = 34L;
        int affectedRows = resourceOutMapper.updateGuid(resourceOutId, guid);
        assertThat(affectedRows).isEqualTo(1);
        final ResourceOut resourceOut = resourceOutMapper.selectByPrimaryKey(resourceOutId);
        assertThat(resourceOut.getGuid()).isEqualTo(guid);
    }

}
