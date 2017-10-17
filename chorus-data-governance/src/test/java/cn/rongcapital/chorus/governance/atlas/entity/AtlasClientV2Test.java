package cn.rongcapital.chorus.governance.atlas.entity;

import org.apache.atlas.model.SearchFilter;
import org.apache.atlas.model.typedef.AtlasClassificationDef;
import org.apache.atlas.model.typedef.AtlasEntityDef;
import org.apache.atlas.model.typedef.AtlasEnumDef;
import org.apache.atlas.model.typedef.AtlasStructDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.atlas.ApplicationProperties.ATLAS_CONFIGURATION_DIRECTORY_PROPERTY;

/**
 * @author yimin
 */
public class AtlasClientV2Test extends BaseResourceIT {
    @Before
    public void setUp() throws Exception {
        System.setProperty(ATLAS_CONFIGURATION_DIRECTORY_PROPERTY, "src/test/resources");
        super.setUp();
    }

    @Test
    public void testGetAllTypeDefs() throws Exception {
        final AtlasTypesDef allTypeDefs = atlasClientV2.getAllTypeDefs(new SearchFilter());
        soutTypeNameAndAttributes(allTypeDefs);
    }

    private void soutTypeNameAndAttributes(AtlasTypesDef allTypeDefs) {
        final List<AtlasClassificationDef> classificationDefs = allTypeDefs.getClassificationDefs();
        System.out.println("CLASSIFICATIONS:");
        classificationDefs.forEach(classificationDef -> {
            final String typeName = classificationDef.getName();
            System.out.println("\t" + typeName + " extends " + classificationDef.getSuperTypes().toString());
            final List<AtlasStructDef.AtlasAttributeDef> attributeDefs = classificationDef.getAttributeDefs();
            descAttributeDef(attributeDefs);
        });
        final List<AtlasEntityDef> entityDefs = allTypeDefs.getEntityDefs();
        System.out.println("ENTITIES:");
        entityDefs.forEach(entityDef -> {
            final String typeName = entityDef.getName();
            System.out.println("\t" + typeName + " extends " + entityDef.getSuperTypes().toString());
            descAttributeDef(entityDef.getAttributeDefs());
        });
        final List<AtlasEnumDef> enumDefs = allTypeDefs.getEnumDefs();
        System.out.println("ENUMS:");
        enumDefs.forEach(enumDef -> {
            final String typeName = enumDef.getName();
            System.out.println("\t" + typeName);
            final List<AtlasEnumDef.AtlasEnumElementDef> elementDefs = enumDef.getElementDefs();
            elementDefs.forEach(ele -> {
                System.out.println("\t\t" + ele.toString());
            });
        });
        final List<AtlasStructDef> structDefs = allTypeDefs.getStructDefs();
        System.out.println("STRUCTS:");
        structDefs.forEach(struct -> {
            final String typeName = struct.getName();
            System.out.println("\t" + typeName);
            descAttributeDef(struct.getAttributeDefs());
        });
    }

    private void descAttributeDef(List<AtlasStructDef.AtlasAttributeDef> attributeDefs) {
        attributeDefs.forEach(atlasAttributeDef -> {
            System.out.println("\t\t" + atlasAttributeDef.toString());
        });
    }
}
