package cn.rongcapital.chorus.metadata.migration;

import cn.rongcapital.chorus.das.service.impl.AtlasEntityFactory;
import cn.rongcapital.chorus.governance.AtlasService;
import cn.rongcapital.chorus.governance.bridge.ChorusMetaStoreBridge;
import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.model.instance.AtlasEntity;

import java.util.*;

/**
 * Created by hhlfl on 2017-7-24.
 */
@Slf4j
public abstract class AbstractDB2AtlasMigration<T> implements DataMigration{
    protected AtlasService atlasService;
    protected AtlasEntityFactory atlasEntityFactory;

    protected final static String UNIQUE ="unique";
    private final static String DB_NAME_FORMAT = "chorus_%s";
    protected final static Integer EXISTS =1;
    protected final static Integer NON_EXISTS=0;

    public AbstractDB2AtlasMigration(AtlasService atlasService, AtlasEntityFactory atlasEntityFactory){
        this.atlasService = atlasService;
        this.atlasEntityFactory = atlasEntityFactory;
    }

    @Override
    public void migrate()throws Exception{
        T sourceEntities = getSourceEntities();
        List<AtlasEntity> atlasEntities = getAtlasEntity(sourceEntities);
        final AtlasEntity[] ingest = atlasService.ingest(atlasEntities.toArray(new AtlasEntity[atlasEntities.size()]));//TODO guid exist in the return
        //key:unique,value:entity
        Map<String, AtlasEntity> successEntities = new HashMap<>();
        for(int i=0;i<ingest.length;i++){
            if(ingest[i] == null){
                log.info("entity{} add to atlas failed.",atlasEntities.get(i).toString());
            }else {
                AtlasEntity entity = ingest[i];
                successEntities.put((String)entity.getAttribute(UNIQUE),entity);
            }
        }
        update(sourceEntities,successEntities);
    }


    protected abstract T getSourceEntities()throws Exception;

    protected abstract List<AtlasEntity> getAtlasEntity(T sourceEntities)throws Exception;

    protected abstract void update(T sourceEntities, Map<String, AtlasEntity> successEntity)throws Exception ;

    /***
     *
     * @param atlasEntities
     * @return Map<String,List<AtlasEntity>> key:0-non-exists,1-exists
     * @throws Exception
     */
    protected Map<Integer,List<AtlasEntity>> groupByExists(List<AtlasEntity> atlasEntities) throws Exception {
        List<AtlasEntity> exist = new ArrayList<>();
        List<AtlasEntity> nonExists = new ArrayList<>();
        atlasEntities.forEach(entity -> {
//            AtlasEntity qentity = exists(entity);
//            if (qentity != null) {
//                exist.add(qentity);
//                log.info("entity{} exist in atlas.", entity.toString());
//            }else{
//                nonExists.add(entity);
//            }
            nonExists.add(entity);
        });

        Map<Integer, List<AtlasEntity>> listMap = new HashMap<>();
        listMap.put(EXISTS,exist);
        listMap.put(NON_EXISTS,nonExists);
        return listMap;
    }

    protected AtlasEntity exists(AtlasEntity atlasEntity){
        List<AtlasEntity> qEntities = atlasService.dslBaseSearch(atlasEntity.getTypeName(), UNIQUE, "=", String.format("%s", atlasEntity.getAttribute(UNIQUE)));
        if (qEntities.size() > 0) {
            log.info("entity{} exist in atlas.", atlasEntity.toString());
            return qEntities.get(0);
        }else{
           return null;
        }
    }

    protected String assembleDbName(String projectCode) {
        return String.format(DB_NAME_FORMAT, projectCode);
    }
}
