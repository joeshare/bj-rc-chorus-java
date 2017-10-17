package cn.rongcapital.chorus.governance.atlas.types;

import cn.rongcapital.chorus.governance.atlas.entity.AbstractAtlasAtlasEntityBuilder;
import cn.rongcapital.chorus.governance.autoconfigure.AtlasTypeDefinitionAutoConfiguration;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.typedef.AtlasEntityDef;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yimin
 */

public class AtlasTypeDefinitionPostProcessorTest {
    private AnnotationConfigApplicationContext context;

    @After
    public void close() {
        if (this.context != null) {
            this.context.close();
        }
    }

    @Test
    public void setEntityFieldToBean() throws Exception {
        this.context = new AnnotationConfigApplicationContext();
        this.context.register(AtlasTypeDefinitionAutoConfiguration.class);
        this.context.register(DummyAtlasEntityDefinitionAndBuilder.class);
        this.context.refresh();

        DummyAtlasEntityDefinitionAndBuilder dummyAtlasEntityBuilderAndDefinition = this.context.getBean(DummyAtlasEntityDefinitionAndBuilder.class);
        AtlasEntityDef entityDef = dummyAtlasEntityBuilderAndDefinition.getDefinition();
        assertThat(entityDef).isNotNull();
        assertThat(entityDef.getAttributeDefs()).hasSize(4);
        assertThat(entityDef.getAttributeDefs().stream().filter(def -> StringUtils.equals(def.getName(), AbstractAtlasAtlasEntityBuilder.UNIQUE)).count()).isEqualTo(1L);

        AtlasEntity entity = dummyAtlasEntityBuilderAndDefinition.getEntity();
        dummyAtlasEntityBuilderAndDefinition.name(entity, randomAlphabetic(10));
        dummyAtlasEntityBuilderAndDefinition.requiredUnique(entity, randomAlphabetic(10));
        dummyAtlasEntityBuilderAndDefinition.required(entity, randomAlphabetic(10));
        dummyAtlasEntityBuilderAndDefinition.build(entity);
    }

}
