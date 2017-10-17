package cn.rongcapital.chorus.governance.autoconfigure;

import cn.rongcapital.chorus.governance.AtlasService;
import cn.rongcapital.chorus.governance.atlas.types.AbstractAtlasEntityDefinitionAndBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.typedef.AtlasEntityDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.apache.atlas.type.AtlasTypeUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;

/**
 * @author yimin
 */
@Slf4j
@Configuration
@Import(AtlasTypeDefinitionAutoConfiguration.class)
public class AtlasEntityDefinitionInitializer implements InitializingBean {

    @Autowired
    private  AtlasService                                  atlasService;
    @Autowired
    private  List<AbstractAtlasEntityDefinitionAndBuilder> definitions;

    public AtlasEntityDefinitionInitializer() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("AtlasEntityDefinitionInitializer.afterPropertiesSet");

        if (definitions.isEmpty()) {
            log.info("there wasn't any entity type definition");
            return;
        }

        try {
            final List<AtlasEntityDef> newTypes = new ArrayList<>();
            for (AbstractAtlasEntityDefinitionAndBuilder definition : definitions) {
                AtlasEntityDef abstractAtlasEntityDefinitionAndBuilderDefinition = definition.getDefinition();
                if (!atlasService.existAtlasTypeDef(abstractAtlasEntityDefinitionAndBuilderDefinition.getName())) {
                    newTypes.add(abstractAtlasEntityDefinitionAndBuilderDefinition);
                }
            }
            final AtlasTypesDef atlasTypesDef = atlasService.newTypes(AtlasTypeUtil.getTypesDef(of(), of(), of(), newTypes));
            log.info("new atlas type definition added {}", atlasTypesDef);
        } catch (AtlasServiceException e) {
            log.error(e.getLocalizedMessage(), e);
            throw e;
        }
    }
}
