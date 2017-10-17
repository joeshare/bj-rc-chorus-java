package cn.rongcapital.chorus.governance.autoconfigure;

import cn.rongcapital.chorus.governance.atlas.types.DummyAtlasEntityDefinitionAndBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yimin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AtlasTypeDefinitionAutoConfiguration.class,DummyAtlasEntityDefinitionAndBuilder.class})
public class AtlasTypeDefinitionAutoConfigurationTest {
    @Autowired
    DummyAtlasEntityDefinitionAndBuilder dummyAtlasEntityDefinitionAndBuilder;

    @Test
    public void beanExist() throws Exception {
        assertThat(dummyAtlasEntityDefinitionAndBuilder).isNotNull();
    }
}
