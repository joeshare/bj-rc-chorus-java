package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.ChorusConstants;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.dao.JobMapper;
import cn.rongcapital.chorus.das.service.TableLinageService;
import cn.rongcapital.chorus.das.entity.TableInfo;
import cn.rongcapital.chorus.das.service.ResourceOutService;
import cn.rongcapital.chorus.das.service.TableInfoService;
import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.TreeNode;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by alan on 11/22/16.
 */
@Service
public class TableLinageServiceImpl implements TableLinageService {
    private static Splitter splitter = Splitter.on(',');
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private TableInfoService tableInfoService;
    @Autowired
    private ResourceOutService resourceOutService;
    Random r = new Random(112216L);

    @Cacheable("JobOutputTableLineage")
    public TreeNode getJobOutputTableLineage(Long tableId) {
        return tableIntoTreeNode(tableId, 0);
    }

    private TreeNode tableIntoTreeNode(Long tableId, Integer pathLevel) {
        String nodeId = "t_" + tableId + r.nextInt();
        TableInfo tableInfo = tableInfoService.selectByID(tableId);
        TreeNode treeNode = new TreeNode(nodeId,
                tableInfo.getTableName(),
                ChorusConstants.TREE_NODE_TYPE_TABLE,
                pathLevel,
                searchJobByOutput(tableId, nextLevel(pathLevel)));
        return treeNode;
    }

    private TreeNode tableIntoTreeNode(String tableId, Integer pathLevel) {
        if (StringUtils.isNumeric(tableId))
            return tableIntoTreeNode(Long.parseLong(tableId), pathLevel);
        return new TreeNode(tableId + r.nextInt(),
                tableId,
                ChorusConstants.TREE_NODE_TYPE_TABLE,
                pathLevel,
                TreeNode.NilChildren());
    }

    private List<TreeNode> searchJobByOutput(Long tableId, Integer pathLevel) {
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
}
