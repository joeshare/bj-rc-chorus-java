package cn.rongcapital.chorus.governance.autoconfigure;

import cn.rongcapital.chorus.governance.AtlasServiceImpl;
import cn.rongcapital.chorus.governance.bridge.ChorusMetaStoreBridge;
import org.apache.atlas.AtlasClientV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static cn.rongcapital.chorus.governance.bridge.ChorusMetaStoreBridge.*;

/**
 * @author yimin
 */
@Configuration
public class ChorusMetaStoreBridgeAutoConfiguration {

    @Bean(initMethod = "importMetadata")
    ChorusMetaStoreBridge chorusMetaStoreBridge(AtlasClientV2 atlasClient){
        return new ChorusMetaStoreBridge(new AtlasServiceImpl(atlasClient), TYPE_VERSION, TYPES_CHOR_HIVE_DB, TYPES_CHOR_HIVE_TABLE, TYPES_CHOR_HIVE_COLUMN,TYPES_CHOR_HIVE_PROCESS);
    }
}
