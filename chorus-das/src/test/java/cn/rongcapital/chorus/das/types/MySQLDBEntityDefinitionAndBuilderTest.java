package cn.rongcapital.chorus.das.types;

import org.apache.atlas.model.instance.AtlasEntity;
import org.junit.Test;

import cn.rongcapital.chorus.das.types.MySQLDBEntityDefinitionAndBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yimin
 */
public class MySQLDBEntityDefinitionAndBuilderTest {

    private final MySQLDBEntityDefinitionAndBuilder dbEntityBuilder = new MySQLDBEntityDefinitionAndBuilder();

    @Test
    public void preBuild() throws Exception {
        final AtlasEntity entity = dbEntityBuilder.getEntity();

        dbEntityBuilder.projectId(entity, 222646L);
        dbEntityBuilder.url(entity, "jdbc:mysql://10.200.48.79:3306/xd");
        dbEntityBuilder.connectUser(entity, "dps");
        dbEntityBuilder.build(entity);

        assertThat(dbEntityBuilder.unique(entity)).isEqualTo("222646:dps:jdbc:mysql://10.200.48.79:3306/xd");
    }

}
