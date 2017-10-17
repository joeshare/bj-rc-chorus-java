package cn.rongcapital.chorus.governance.atlas.types;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;

/**
 * @author yimin
 */
@Slf4j
class EnableAtlasTyeDefinitionImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        String[] imports = {AtlasTypeDefinitionPostProcessorRegistrar.class.getName()};
        log.info("selected imports {}", Arrays.toString(imports));
        return imports;
    }
}
