package cn.rongcapital.chorus.server.platform.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cn.rongcapital.chorus.common.constant.CommonAttribute;
import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.entity.UnexecutedJob;
import cn.rongcapital.chorus.das.entity.UnexecutedJobs;
import cn.rongcapital.chorus.das.service.PlatformMaintenanceService;
import cn.rongcapital.chorus.server.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kevin.gong
 * @Time 2017年9月19日 下午3:34:27
 */
@Slf4j
@Api(value = "平台维护")
@RestController
@RequestMapping(value = { "/platform" })
public class PlatformMaintenanceController {

    @Autowired
    private PlatformMaintenanceService platformMaintenanceService;
    
    @ApiOperation(value = "获取平台维护状态")
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "/maintenance/status" }, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO<Integer> getPlatformMaintenanceStatus() {
        return ResultVO.success(platformMaintenanceService.getPlatformMaintenanceStatus());
    }
    
    @ApiOperation(value = "设置平台维护状态")
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误"), 
            @ApiResponse(code = 8000, message = "平台已维护"), @ApiResponse(code = 8001, message = "平台已启动")})
    @RequestMapping(value = { "/maintenance/status/{status}" }, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO<Boolean> setPlatformMaintenanceStatus(@PathVariable("status") int status) {
        int mstatus = this.platformMaintenanceService.getPlatformMaintenanceStatus();
        if(mstatus == status) {
            if(status == CommonAttribute.MAINTENANCE_STATUS_MAINTAINED) {
                return ResultVO.error(StatusCode.PLATFORM_MAINTAINED_ERROR, false);
            }
            if(status == CommonAttribute.MAINTENANCE_STATUS_STARTED) {
                return ResultVO.error(StatusCode.PLATFORM_STARTED_ERROR, false);
            }
        }
        return ResultVO.success(platformMaintenanceService.setPlatformMaintenanceStatus(status));
    }
    
    @ApiOperation(value = "获取待执行任务列表")
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")})
    @RequestMapping(value = { "/maintenance/unexecuted_job/{pageNum}/{pageSize}" }, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO<UnexecutedJobs> getUnexecutedJobList(@PathVariable int pageNum, @PathVariable int pageSize) {
        UnexecutedJobs uj = new UnexecutedJobs();
        uj.setTotal(platformMaintenanceService.getWaitExecuteJobsCount());
        uj.setList(platformMaintenanceService.getWaitExecuteJobs(pageNum, pageSize));
        return ResultVO.success(uj);
    }

}
