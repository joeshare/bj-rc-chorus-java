package cn.rongcapital.chorus.server.dashboard.controller;

import cn.rongcapital.chorus.common.util.SpringBeanUtils;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.entity.TableMonitorInfoV2;
import cn.rongcapital.chorus.das.service.ApplyDetailServiceV2;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import cn.rongcapital.chorus.das.service.TableMonitorServiceV2;
import cn.rongcapital.chorus.server.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(value = "dashboard 表监控")
@RestController
@RequestMapping(value = { "/dashboard" })
public class TableAnalyzeController {

    @Autowired
    private TableMonitorServiceV2 tableMonitorServiceV2;
    @Autowired
    private ApplyDetailServiceV2 applyDetailServiceV2;
    @Autowired
    private TableInfoServiceV2 tableInfoServiceV2;

    /**
     * @param projectId
     * @return
     * @author yunzhong
     * @time 2017年6月23日上午11:17:30
     */
    @ApiOperation(value = "项目表整体统计信息")
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "tableData/{projectId}/total" }, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO getTableTotal(@PathVariable Long projectId) {
        try {
            Map<String, Long> result = new HashMap<>();
            long storageTotal = tableMonitorServiceV2.selectStorageTotal(projectId);
            result.put("storage", storageTotal);
            long tablesTotal = tableMonitorServiceV2.getTablesTotal(projectId);
            result.put("tables", tablesTotal);
            return ResultVO.success(result);
        } catch (Exception e) {
            log.info("Failed to get project [" + projectId + "] monitor info.", e);
            return ResultVO.error();
        }
    }

    /**
     * @param projectId
     * @return
     * @author yunzhong
     * @time 2017年6月23日上午11:20:38
     */
    @ApiOperation(value = "项目表行数的top5")
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "tableRows/{projectId}/top" }, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO getTabelRowsTop(@PathVariable Long projectId) {
        try {
            List<TableMonitorInfoV2> rowsTop = tableMonitorServiceV2.selectRowsTop(projectId, 5);
            appendTableName(rowsTop);
            return ResultVO.success(rowsTop);
        } catch (Exception e) {
            log.info("Failed to get project [" + projectId + "] monitor info.", e);
            return ResultVO.error();
        }
    }

    //TODO 采用数据表冗余的方式，减少查询
    private void appendTableName(List<TableMonitorInfoV2> rowsTop) {
        if(CollectionUtils.isNotEmpty(rowsTop)){
            List<String> guids = new ArrayList<>();
            rowsTop.forEach(row -> {
                if(StringUtils.isNotEmpty(row.getTableName()))
                    guids.add(row.getTableInfoId());
            });
            if(CollectionUtils.isNotEmpty(guids)){
                Map<String ,TableInfoV2> tableInfoV2s = tableInfoServiceV2.selectByIds(guids);
                rowsTop.forEach(e -> {
                    if(tableInfoV2s!=null) e.setTableName(tableInfoV2s.get(e.getTableInfoId()).getTableName());
                });
            }
        }
    }

    /**
     * @param projectId
     * @return
     * @author yunzhong
     * @time 2017年6月23日上午11:20:35
     */
    @ApiOperation(value = "项目表存储的top5")
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "tableStorage/{projectId}/top" }, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO getTabelStorageTop(@PathVariable Long projectId) {
        try {
            List<TableMonitorInfoV2> rowsTop = tableMonitorServiceV2.selectStorageTop(projectId, 5);
            appendTableName(rowsTop);
            return ResultVO.success(rowsTop);
        } catch (Exception e) {
            log.info("Failed to get project [" + projectId + "] monitor info.", e);
            return ResultVO.error();
        }
    }

    @ApiOperation(value = "最高关注度表信息")
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "tableAttention/{projectId}/{topSize}" }, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO getTopAttRateTableInfo(@PathVariable long projectId, @PathVariable int topSize) {
        return ResultVO.success(applyDetailServiceV2.getTopAttRateTableInfo(projectId, topSize));
    }
}
