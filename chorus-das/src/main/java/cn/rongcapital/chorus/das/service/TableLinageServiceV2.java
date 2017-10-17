package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.TreeNode;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.lineage.AtlasLineageInfo;

/**
 * Created by alan on 11/22/16.
 */
public interface TableLinageServiceV2 {

    TreeNode getJobOutputTableLineage(String tableId);

    /**
     * 根据table guid查询atles血缘关系
     * @param tableInfoId
     * @return
     */
    TreeNode getLineageByTableInfoId(String tableInfoId) throws AtlasServiceException;

    AtlasLineageInfo getLineageByTableInfoIdV2(String tableInfoId) throws AtlasServiceException;
}
