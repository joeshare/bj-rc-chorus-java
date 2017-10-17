package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.TreeNode;

/**
 * Created by alan on 11/22/16.
 */
public interface TableLinageService {

    TreeNode getJobOutputTableLineage(Long tableId);
}
