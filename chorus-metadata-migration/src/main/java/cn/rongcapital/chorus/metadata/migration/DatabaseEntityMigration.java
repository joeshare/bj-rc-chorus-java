package cn.rongcapital.chorus.metadata.migration;

import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.service.MetaDataMigrateService;
import cn.rongcapital.chorus.das.service.impl.AtlasEntityFactory;
import cn.rongcapital.chorus.governance.AtlasService;
import cn.rongcapital.chorus.governance.bridge.ChorusMetaStoreBridge;
import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.model.instance.AtlasEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Created by hhlfl on 2017-7-24.
 */
@Slf4j
@Component
public class DatabaseEntityMigration extends AbstractDB2AtlasMigration<List<ProjectInfo>> {
    @Autowired
    private MetaDataMigrateService metaDataMigrateService;

    @Autowired
    public DatabaseEntityMigration(AtlasService atlasService,AtlasEntityFactory atlasEntityFactory){
        super(atlasService, atlasEntityFactory);
    }

    @Override
    protected List<ProjectInfo> getSourceEntities() throws Exception {
        return metaDataMigrateService.getAllProjectInfos();
    }

    @Override
    protected List<AtlasEntity> getAtlasEntity(List<ProjectInfo> sourceEntities) throws Exception {
        List<AtlasEntity> entities =  sourceEntities.stream().map(projectInfo -> {
            AtlasEntity entity =  atlasEntityFactory.getHiveDBEntity(projectInfo, projectInfo.getUserName(),assembleDbName(projectInfo.getProjectCode()));
            return entity;
        }).collect(toList());

        return groupByExists(entities).get(NON_EXISTS);

    }

    @Override
    protected void update(List<ProjectInfo> sourceEntities, Map<String,AtlasEntity> successEntities) throws Exception {
        ;
    }

}
