package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.ResourceOutDOMapper;
import cn.rongcapital.chorus.das.dao.ResourceOutMapper;
import cn.rongcapital.chorus.das.entity.ResourceOut;
import cn.rongcapital.chorus.das.service.ResourceOutService;
import cn.rongcapital.chorus.das.service.impl.ResourceOutServiceImpl;
import cn.rongcapital.chorus.das.types.MySQLDBEntityDefinitionAndBuilder;
import cn.rongcapital.chorus.governance.AtlasService;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.when;

/**
 * @author yimin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ResourceOutServiceImpl.class, ResourceOutServiceImplTest.class, MySQLDBEntityDefinitionAndBuilder.class})
@Configuration
@Profile({"single"})
public class ResourceOutServiceImplTest {
    @Autowired
    ResourceOutService recordOutService;
    @Autowired
    AtlasService       atlasService;
    @Autowired
    MySQLDBEntityDefinitionAndBuilder dbEntityBuilder;

    private ResourceOut record;

    @Test
    public void syncToMetaDataCenter() throws Exception {
        initRecord();
        when(atlasService.getEntityByUniqueAttribute(anyString(), anyString(), anyString())).thenReturn(null);
        final String guid = randomAlphabetic(20);
        when(atlasService.ingest(anyVararg())).then((Answer<AtlasEntity[]>) invocation -> {
            final AtlasEntity igestOne = (AtlasEntity) invocation.getArguments()[0];
            assertThat(dbEntityBuilder.name(igestOne)).isEqualTo(record.getResourceName());
            assertThat(dbEntityBuilder.url(igestOne)).isEqualTo(record.getConnUrl());
            assertThat(dbEntityBuilder.host(igestOne)).isEqualTo(record.getConnHost());
            assertThat(dbEntityBuilder.port(igestOne)).isEqualTo(record.getConnPort());
            assertThat(dbEntityBuilder.dataBaseName(igestOne)).isEqualTo(record.getDatabaseName());
            assertThat(dbEntityBuilder.connectUser(igestOne)).isEqualTo(record.getConnUser());
            assertThat(dbEntityBuilder.descritpion(igestOne)).isEqualTo(record.getResourceDesc());
            assertThat(dbEntityBuilder.createTime(igestOne)).isEqualTo(record.getCreateTime());
            assertThat(dbEntityBuilder.updateTime(igestOne)).isEqualTo(record.getUpdateTime());
            assertThat(dbEntityBuilder.createUser(igestOne)).isEqualTo(record.getCreateUserName());
            assertThat(dbEntityBuilder.createUserId(igestOne)).isEqualTo(record.getCreateUserId());
            assertThat(dbEntityBuilder.projectId(igestOne)).isEqualTo(record.getProjectId());
            igestOne.setGuid(guid);
            return new AtlasEntity[]{igestOne};
        });
        final ResourceOut resourceOut = recordOutService.syncToMetaDataCenter(record);
        assertThat(resourceOut.getGuid()).isEqualTo(guid);
    }

    private void initRecord() {
        record = new ResourceOut();
        record.setResourceOutId(RandomUtils.nextLong(1, 1000));
        record.setProjectId(222667L);
        record.setResourceName("chorus");
        record.setResourceDesc("chorus");
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
    }

    @Bean
    public ResourceOutMapper recordOutMapper() {
        final ResourceOutMapper mock = Mockito.mock(ResourceOutMapper.class);
        when(mock.updateGuid(anyLong(),anyString())).thenReturn(1);
        return mock;
    }

    @Bean
    public ResourceOutDOMapper recordOutDOMapper() {
        return Mockito.mock(ResourceOutDOMapper.class);
    }

    @Bean
    public AtlasService atlasService() {
        final AtlasService mock = Mockito.mock(AtlasService.class);
        return mock;
    }

}
