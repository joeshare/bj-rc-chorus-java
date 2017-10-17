package cn.rongcapital.chorus.server.metadata.controller;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.das.entity.TreeNode;
import cn.rongcapital.chorus.das.service.TableLinageServiceV2;
import cn.rongcapital.chorus.server.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by alan on 11/23/16.
 */
@Controller
@Slf4j
public class TableLinageController {

    @Autowired
    private TableLinageServiceV2 tableLinageServiceV2;

    @RequestMapping(value = "/metadata/table_linage/{tableId}", method = RequestMethod.GET)
    @ResponseBody
    public ResultVO<TreeNode> getTableLinage(@PathVariable("tableId") String tableId) {
        try {
//            TreeNode tableLineage = tableLinageServiceV2.getJobOutputTableLineage(tableId);
            TreeNode tableLineage = tableLinageServiceV2.getLineageByTableInfoId(tableId);
//            AtlasLineageInfo tableLineage = tableLinageServiceV2.getLineageByTableInfoIdV2(tableId);
            return ResultVO.success(tableLineage);
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.error("Caught exception in getTableLinage, tableId: {}", tableId);
            log.error("Caught exception in getTableLinage, exception: {}", e);
            return ResultVO.error(StatusCode.SYSTEM_ERR);
        }
    }

    @RequestMapping(value = "/metadata/table_linagev2/{tableId}", method = RequestMethod.GET)
    @ResponseBody
    public ResultVO<AtlasLineageInfo> getTableLinageV2(@PathVariable("tableId") String tableId) {
        try {
//            TreeNode tableLineage = tableLinageServiceV2.getJobOutputTableLineage(tableId);
//            TreeNode tableLineage = tableLinageServiceV2.getLineageByTableInfoId(tableId);
            AtlasLineageInfo tableLineage = tableLinageServiceV2.getLineageByTableInfoIdV2(tableId);
            return ResultVO.success(tableLineage);
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.error("Caught exception in getTableLinage, tableId: {}", tableId);
            log.error("Caught exception in getTableLinage, exception: {}", e);
            return ResultVO.error(StatusCode.SYSTEM_ERR);
        }
    }
}
