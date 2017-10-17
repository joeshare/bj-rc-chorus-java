package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.ResourceUsedStatsDOMMapper;
import cn.rongcapital.chorus.das.entity.ResourceUsedStats;
import cn.rongcapital.chorus.das.service.ResourceUsedStatsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hhlfl on 2017-6-19.
 */
@Component
@Slf4j
@Service("ResourceUsedStatsService")
@Transactional
public class ResourceUsedStatsServiceImpl implements ResourceUsedStatsService {
    @Autowired
    private ResourceUsedStatsDOMMapper resourceUsedStatsDOMMapper;
    @Override
    public int batchInsert(List<ResourceUsedStats> records) {
        int len = records.size();
        int batchSize=5000;
        int count = 0;
        for(int i=0; i<len; i+=batchSize){
            int from = i;
            int to = (from+batchSize)>len?len:from+batchSize;
            List<ResourceUsedStats> subList = records.subList(from,to);
            count += resourceUsedStatsDOMMapper.batchInsert(subList);
        }

        return count;
    }

    @Override
    public List<ResourceUsedStats> statsTableUsedTopN(long projectId, List<String> resourceTypes, int topN) {
        return resourceUsedStatsDOMMapper.statsTableUsedTopN(projectId,resourceTypes,topN);
    }

    @Override
    public void batchInsertWitchOverride(List<ResourceUsedStats> records) {
        deleteAll();
        batchInsert(records);
    }

    @Override
    public int deleteAll(){
        return resourceUsedStatsDOMMapper.deleteAll();
    }
}
