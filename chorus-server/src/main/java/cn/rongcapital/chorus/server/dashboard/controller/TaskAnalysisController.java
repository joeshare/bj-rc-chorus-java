package cn.rongcapital.chorus.server.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cn.rongcapital.chorus.das.service.JobExecStatisticService;
import cn.rongcapital.chorus.das.service.JobService;
import cn.rongcapital.chorus.das.service.StreamExecStatisticService;
import cn.rongcapital.chorus.server.vo.ResultVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * Dashboard:任务子模块Controller
 * @author kevin.gong
 * @Time 2017年6月16日 下午4:13:45
 */
@Slf4j
@RestController
@RequestMapping(value = {"/dashboard/{projectId}"})
public class TaskAnalysisController {
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private JobExecStatisticService jobExecStatisticService;
    
    @Autowired
    private StreamExecStatisticService streamExecStatisticService;

    @ApiOperation(value = "任务分布")
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"task/distribution"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO getTaskDistribution(@PathVariable long projectId) {
        return ResultVO.success(jobService.getJobStatusDistribution(projectId));
    }

    @ApiOperation(value = "批量任务执行分布获得")
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"batchTask/execStatusDist/{recentDayNum}"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO getBatchTaskExexDist(@PathVariable long projectId, @PathVariable int recentDayNum) {
        return ResultVO.success(jobExecStatisticService.getJobExecDist(projectId, recentDayNum));
    }
    
    @ApiOperation(value = "流式任务执行分布获得")
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"streamTask/execStatusDist/{recentDayNum}"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO getStreamTaskExecDist(@PathVariable long projectId, @PathVariable int recentDayNum) {
        return ResultVO.success(streamExecStatisticService.getStreamExecDist(projectId, recentDayNum));
    }
    
    @ApiOperation(value = "执行时间最长的任务信息获取")
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"batchTask/longestExecTime/{topSize}"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO getBatchTasklongExecInfo(@PathVariable long projectId, @PathVariable int topSize) {
        return ResultVO.success(jobExecStatisticService.getLongestExecTimeJob(projectId, topSize));
    }
}
