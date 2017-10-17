package cn.rongcapital.chorus.governance.bridge;

import cn.rongcapital.chorus.governance.AtlasService;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.typedef.AtlasBaseTypeDef;
import org.apache.atlas.model.typedef.AtlasClassificationDef;
import org.apache.atlas.model.typedef.AtlasEntityDef;
import org.apache.atlas.model.typedef.AtlasEnumDef;
import org.apache.atlas.model.typedef.AtlasStructDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.apache.atlas.type.AtlasTypeUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * @author yimin
 */
@Slf4j
public abstract class AtlasMetadataStoreBridge implements Bridge {
    protected final AtlasService atlasService;

    public AtlasMetadataStoreBridge(AtlasService atlasService) {
        this.atlasService = atlasService;
    }

    @Override
    public void importMetadata() throws AtlasServiceException {
        List<AtlasEnumDef> enums = enums();
        List<AtlasStructDef> structs = structs();
        List<AtlasClassificationDef> traits = traits();
        List<AtlasEntityDef> classes = classes();

        final List<String> TYPES = new ArrayList<>();
        ingestTypeName(TYPES, enums, structs, traits, classes);
        int expectedCount = TYPES.size();
        List<String> existTypes = existTyps(TYPES);
        int existTypesCount = existTypes.size();
        if (expectedCount == existTypesCount) {
            log.info("all types def exist already");
            return;
        }

        //clean exist types def
        enums = enums.parallelStream().filter(typeDef -> !existTypes.contains(typeDef.getName())).collect(toList());
        structs = structs.parallelStream().filter(typeDef -> !existTypes.contains(typeDef.getName())).collect(toList());
        traits = traits.parallelStream().filter(typeDef -> !existTypes.contains(typeDef.getName())).collect(toList());
        classes = classes.parallelStream().filter(typeDef -> !existTypes.contains(typeDef.getName())).collect(toList());

        final AtlasTypesDef typesDef = AtlasTypeUtil.getTypesDef(enums, structs, traits, classes);
        atlasService.newTypes(typesDef);
        verifyTypesCreated(TYPES);
    }

    private List<String> existTyps(List<String> types) {
        return types.parallelStream().filter(typeName -> {
            try {
                return atlasService.existAtlasTypeDef(typeName);
            } catch (AtlasServiceException e) {
                log.error(e.getLocalizedMessage(), e);
            }
            return false;
        }).collect(toList());
    }

    private void verifyTypesCreated(List<String> types) throws AtlasServiceException {
        for (String typeName : types) {
            final boolean exist = atlasService.existAtlasTypeDef(typeName);
            if (exist) {
                log.info("Created type [" + typeName + "]");
            } else {
                log.info("Type [" + typeName + "] create failure");
            }
        }
    }

    private void ingestTypeName(
            List<String> TYPES, List<? extends AtlasBaseTypeDef> enums,
            List<? extends AtlasBaseTypeDef> structs,
            List<? extends AtlasBaseTypeDef> traits, List<? extends AtlasBaseTypeDef> classes
    ) {
        ingest(TYPES, enums);
        ingest(TYPES, structs);
        ingest(TYPES, traits);
        ingest(TYPES, classes);
    }

    private void ingest(List<String> TYPES, List<? extends AtlasBaseTypeDef> typeDefs) {
        if (CollectionUtils.isNotEmpty(typeDefs)) {
            TYPES.addAll(typeDefs.parallelStream().filter(Objects::nonNull).map(AtlasBaseTypeDef::getName).collect(toList()));
        }
    }

    public List<AtlasEnumDef> enums() {
        return ImmutableList.of();
    }

    public List<AtlasStructDef> structs() {
        return ImmutableList.of();
    }

    public List<AtlasClassificationDef> traits() {
        return ImmutableList.of();
    }

    public List<AtlasEntityDef> classes() {
        return ImmutableList.of();
    }

}
