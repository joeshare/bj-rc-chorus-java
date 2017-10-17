package cn.rongcapital.chorus.server.resourceInner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.rongcapital.chorus.common.hadoop.QuotaEnum;
import cn.rongcapital.chorus.das.entity.ResourceInner;
import cn.rongcapital.chorus.das.entity.ResourceInnerLeft;
import cn.rongcapital.chorus.das.service.ResourceInnerService;
import cn.rongcapital.chorus.das.service.TableMonitorServiceV2;
import cn.rongcapital.chorus.server.vo.ResultVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by alan on 12/2/16.
 */
@Slf4j
@RestController
public class ResourceInnerController {

    @Autowired
    private ResourceInnerService resourceInnerService;

    @Autowired
    private TableMonitorServiceV2 tableMonitorService;

    @RequestMapping("/resource_inner/get_by_project_id/{projectId}")
    public ResultVO<ResourceInner> getByProjectId(@PathVariable Long projectId) {
        try {
            return ResultVO.success(resourceInnerService.getByProjectId(projectId));
        } catch (Exception e) {
            log.warn("Caught exception in getByProjectId, projectId: {}, error: {}", projectId, e);
            return ResultVO.error();
        }
    }

    @ApiOperation(value = "获取项目剩余资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", paramType = "path", dataType = "Long", required = true, value = "项目id") })
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = "/resource_inner/get_left/{projectId}", method = RequestMethod.GET)
    public ResultVO<ResourceInnerLeft> getLeftByProjectId(@PathVariable Long projectId) {
        try {
            ResourceInner inner = resourceInnerService.getLeftByProjectId(projectId);
            ResourceInnerLeft left = new ResourceInnerLeft();
            if (inner == null) {
                left.setProjectId(projectId);
                left.setResourceCpu(0);
                left.setResourceMemory(0);
                left.setResourceStorage(0D);
            } else {
                left.setProjectId(inner.getProjectId());
                left.setResourceCpu(inner.getResourceCpu());
                left.setResourceMemory(inner.getResourceMemory());
                long usedStorage = tableMonitorService.selectStorageTotal(projectId);
                int innerStorage = inner.getResourceStorage() == null ? 0 : inner.getResourceStorage();
                if (usedStorage > 0) {
                    double used = innerStorage * QuotaEnum.G.getValue() - usedStorage;
                    left.setResourceStorage(used / QuotaEnum.G.getValue());
                } else {
                    left.setResourceStorage(Double.valueOf(innerStorage));
                }
            }
            return ResultVO.success(left);
        } catch (Exception e) {
            log.warn("Caught exception in getByProjectId, projectId: {}, error: {}", projectId, e);
            return ResultVO.error();
        }
    }
}
