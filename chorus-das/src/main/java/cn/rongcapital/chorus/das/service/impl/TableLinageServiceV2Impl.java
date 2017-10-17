package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.ChorusConstants;
import cn.rongcapital.chorus.das.dao.JobMapper;
import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.entity.TreeNode;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import cn.rongcapital.chorus.das.service.TableLinageServiceV2;
import cn.rongcapital.chorus.governance.AtlasService;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasEntityHeader;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by alan on 11/22/16.
 */
@Slf4j
@Service
public class TableLinageServiceV2Impl implements TableLinageServiceV2 {
    private static Splitter splitter = Splitter.on(',');
    @Autowired
    private JobMapper          jobMapper;
    @Autowired
    private TableInfoServiceV2 tableInfoServiceV2;
    @Autowired
    private AtlasService atlasService;

    private static final int depth = 5;
    private final Random r = new Random(112216L);

    @Cacheable("JobOutputTableLineage")
    public TreeNode getJobOutputTableLineage(String tableId) {
        return tableIntoTreeNode(tableId, 0);
    }

    private TreeNode tableIntoTreeNode(String tableId, Integer pathLevel) {
        String nodeId = "t_" + tableId + r.nextInt();
        TableInfoV2 tableInfo = tableInfoServiceV2.selectByID(tableId);
        if (tableInfo == null) {
            return new TreeNode(
                    tableId + r.nextInt(),
                    tableId,
                    ChorusConstants.TREE_NODE_TYPE_TABLE,
                    pathLevel,
                    TreeNode.NilChildren()
            );
        }
        TreeNode treeNode = new TreeNode(nodeId,
                tableInfo.getTableName(),
                ChorusConstants.TREE_NODE_TYPE_TABLE,
                pathLevel,
                searchJobByOutput(tableId, nextLevel(pathLevel)));
        return treeNode;
    }

    private List<TreeNode> searchJobByOutput(String tableId, Integer pathLevel) {
        List<Job> jobs = jobMapper.getJobByOutputTable("[[:<:]]" + tableId + "[[:>:]]");
        if (jobs.size() < 0) {
            return TreeNode.NilChildren();
        }
        return jobs.stream()
                .map(j -> jobIntoTreeNode(j, pathLevel))
                .collect(Collectors.toList());
    }

    private TreeNode jobIntoTreeNode(Job job, Integer pathLevel) {
        String nodeId = "j_" + job.getJobId() + r.nextInt();
        List<TreeNode> children = splitter.splitToList(job.getDataInput()).stream()
                .map(tId -> tableIntoTreeNode(tId, nextLevel(pathLevel)))
                .collect(Collectors.toList());
        TreeNode treeNode = new TreeNode(nodeId, job.getJobAliasName(), ChorusConstants.TREE_NODE_TYPE_JOB,
                pathLevel, children);
        return treeNode;
    }

    private int nextLevel(Integer pathLevel) {
        return pathLevel + 1;
    }


    @Override
    public TreeNode getLineageByTableInfoId(String tableInfoId) throws AtlasServiceException{
        AtlasLineageInfo atlasLineageInfo = atlasService.getLineageByGuid(tableInfoId, AtlasLineageInfo.LineageDirection.INPUT, depth);

        Set<AtlasLineageInfo.LineageRelation> relationSet = atlasLineageInfo.getRelations();
        Map<String, AtlasEntityHeader> headerMap = atlasLineageInfo.getGuidEntityMap();

        Map<String, Integer> nodeMap = new HashMap<>();
        nodeMap.put(tableInfoId,0);
        return getTableTreeNode(tableInfoId, headerMap, 0, relationSet, nodeMap);
    }

    private List<TreeNode> getTableTreeNodeInfo(String jobEntityId,Set<AtlasLineageInfo.LineageRelation> relationSet, Map<String, AtlasEntityHeader> headerMap, Integer pathLevel, Map<String, Integer> nodeMap){
        return relationSet.parallelStream().map(l -> {
            if(l.getToEntityId().equals(jobEntityId)){
                if(nodeMap.containsKey(l.getFromEntityId())&&pathLevel!=nodeMap.get(l.getFromEntityId())){
                    return null;
                }
                nodeMap.put(l.getFromEntityId(), pathLevel);
                return getTableTreeNode(l.getFromEntityId(),
                        headerMap,
                        pathLevel,
                        relationSet,
                        nodeMap);
            }
            return null;
        }).filter(l -> l != null).collect(Collectors.toList());
    }

    private TreeNode getTableTreeNode(String tableInfoId, Map<String, AtlasEntityHeader> headerMap, Integer pathLevel, Set<AtlasLineageInfo.LineageRelation> relationSet, Map<String, Integer> nodeMap){
        if(MapUtils.isEmpty(headerMap) || CollectionUtils.isEmpty(relationSet)){
            try {
                AtlasEntity atlasEntity = atlasService.getByGuid(tableInfoId);
                return new TreeNode(tableInfoId,
                        atlasEntity.getAttribute("name").toString(),
                        ChorusConstants.TREE_NODE_TYPE_TABLE,
                        pathLevel,
                        TreeNode.NilChildren());
            } catch (AtlasServiceException e) {
                e.printStackTrace();
                log.error("get table entity failed! guid : {}",tableInfoId);
            }
        }

        return new TreeNode(tableInfoId,
                headerMap == null ? null : headerMap.get(tableInfoId).getAttribute("name").toString(),
                ChorusConstants.TREE_NODE_TYPE_TABLE,
                pathLevel,
                getJobTreeNodeInfo(tableInfoId, relationSet, headerMap, nextLevel(pathLevel), nodeMap));
    }

    private List<TreeNode> getJobTreeNodeInfo(String guid, Set<AtlasLineageInfo.LineageRelation> relationSet, Map<String, AtlasEntityHeader> headerMap, Integer pathLevel, Map<String, Integer> nodeMap){
        return relationSet.parallelStream().map(l -> {
            if (l.getToEntityId().equals(guid)) {
                if(nodeMap.containsKey(l.getFromEntityId())&&pathLevel!=nodeMap.get(l.getFromEntityId())){
                    return null;
                }
                nodeMap.put(l.getFromEntityId(), pathLevel);
                return getJobTreeNode(l.getFromEntityId(), relationSet, headerMap, pathLevel, nodeMap);
            }
            return null;
        }).filter(l -> l != null).collect(Collectors.toList());
    }

    private TreeNode getJobTreeNode(String jobEntityId,Set<AtlasLineageInfo.LineageRelation> relationSet, Map<String, AtlasEntityHeader> headerMap, Integer pathLevel, Map<String, Integer> nodeMap){
        return new TreeNode(jobEntityId,
                headerMap.get(jobEntityId).getAttribute("name").toString(),
                ChorusConstants.TREE_NODE_TYPE_JOB,
                pathLevel,
                getTableTreeNodeInfo(jobEntityId, relationSet, headerMap, nextLevel(pathLevel), nodeMap));
    }

    @Override
    public AtlasLineageInfo getLineageByTableInfoIdV2(String tableInfoId) throws AtlasServiceException{
        AtlasLineageInfo atlasLineageInfo = atlasService.getLineageByGuid(tableInfoId, AtlasLineageInfo.LineageDirection.INPUT, depth);
        return atlasLineageInfo;
    }
}
