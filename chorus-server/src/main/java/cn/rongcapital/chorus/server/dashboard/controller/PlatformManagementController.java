package cn.rongcapital.chorus.server.dashboard.controller;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.hadoop.HadoopClient;
import cn.rongcapital.chorus.common.util.DateUtils;
import cn.rongcapital.chorus.common.util.NumUtils;
import cn.rongcapital.chorus.das.entity.*;
import cn.rongcapital.chorus.das.service.JobService;
import cn.rongcapital.chorus.das.service.PlatformResourceSnapshotService;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.das.service.ProjectResourceKpiSnapshotService;
import cn.rongcapital.chorus.resourcemanager.service.TotalResourceService;
import cn.rongcapital.chorus.server.dashboard.vo.PlatformResourcInfoVO;
import cn.rongcapital.chorus.server.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Athletics on 2017/7/18.
 */
@Slf4j
@Api(value = "dashboard 平台管理")
@RestController
@RequestMapping(value = {"/dashboard"})
public class PlatformManagementController {

    @Autowired
    private ProjectResourceKpiSnapshotService projectResourceKpiSnapshotService;
    @Autowired
    private PlatformResourceSnapshotService platformResourceSnapshotService;
    @Autowired
    private JobService jobService;
    @Autowired
    private TotalResourceService totalResourceService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private HadoopClient hClient;

    @ApiOperation(value = "平台管理使用率")
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"/platform/use_rate"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO<Map<String, String>> getUseRate(){
        ResultVO<Map<String, String>> vo = new ResultVO<>(StatusCode.SYSTEM_ERR);
        Map<String, String> resultMap = new HashMap<>();
        try{
            //项目已申请总资源
            Map<String, Long> resourceMap = projectResourceKpiSnapshotService.queryUseRate();
            //平台总资源
            TotalResource totalResource = totalResourceService.getTotalResourceWithQueueCapacity();

            if(totalResource!=null){
                resultMap.put("cpu", NumUtils.percent(resourceMap == null ? 0L : resourceMap.get("cpu"),totalResource.getCpu().longValue()));
                resultMap.put("memory", NumUtils.percent(resourceMap == null ? 0L : resourceMap.get("memory"),totalResource.getMemory().longValue()));
                resultMap.put("storage", NumUtils.percent(resourceMap == null ? 0L : resourceMap.get("storage"), totalResource.getStorage().longValue()));

                vo.setData(resultMap);
                vo.setCode(StatusCode.SUCCESS.getCode());
                vo.setMsg(StatusCode.SUCCESS.getDesc());
            }
        }catch (Exception ex){
            log.error("获取平台管理资源使用率失败。", ex);
        }
        return vo;
    }

    @ApiOperation(value = "平台管理项目数")
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"/platform/project_num"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO<Integer> getTotalProjectNum(){
        ResultVO<Integer> vo = new ResultVO<>(StatusCode.SYSTEM_ERR);
        try{
            vo.setData(projectResourceKpiSnapshotService.getTotalProjectNum());
            vo.setCode(StatusCode.SUCCESS.getCode());
            vo.setMsg(StatusCode.SUCCESS.getDesc());
        }catch (Exception ex){
            log.error("获取平台管理项目数失败。", ex);
        }
        return vo;
    }

    @ApiOperation(value = "平台管理数据总量")
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"/platform/total_data"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO<Long> getPlatformTotalData(){
        ResultVO<Long> vo = new ResultVO<>(StatusCode.SYSTEM_ERR);
        try{
            List<ProjectInfo> projects = projectInfoService.queryAll();
            Set<String> projectCodes = projects.parallelStream().map(p -> p.getProjectCode()).collect(Collectors.toSet());
            vo.setData(hClient.getTotalDataNum(projectCodes));
            vo.setCode(StatusCode.SUCCESS.getCode());
            vo.setMsg(StatusCode.SUCCESS.getDesc());
        }catch (Exception ex){
            log.error("获取平台数据总量失败。", ex);
        }
        return vo;
    }

    @ApiOperation(value = "平台管理日增数据量")
    @ApiResponses({
                          @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
                          @ApiResponse(code = 403, message = "服务器资源不可用"),
                          @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
                  })
    @RequestMapping(value = {"/platform/data_daily_incr"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO<Long> getPlatformDataDailyIncr() {
        ResultVO<Long> vo = new ResultVO<>(StatusCode.SYSTEM_ERR);
        try {
            Long result = projectResourceKpiSnapshotService.getPlatformDataDailyIncr();
            vo.setData(result == null ? 0L : result);
            vo.setCode(StatusCode.SUCCESS.getCode());
            vo.setMsg(StatusCode.SUCCESS.getDesc());
        } catch (Exception ex) {
            log.error("获取平台日增数据量失败。", ex);
            vo.setData(0L);
        }
        return vo;
    }

    @ApiOperation(value = "平台管理任务成功率")
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"/platform/task_success_rate"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO<String> getPlatformTaskSuccessRate(){
        ResultVO<String> vo = new ResultVO<>(StatusCode.SYSTEM_ERR);
        try{
            vo.setData(projectResourceKpiSnapshotService.getTaskSuccessRate());
            vo.setCode(StatusCode.SUCCESS.getCode());
            vo.setMsg(StatusCode.SUCCESS.getDesc());
        }catch (Exception ex){
            log.error("获取平台任务成功率失败。", ex);
        }
        return vo;
    }

    @ApiOperation(value = "平台管理 30天内资源走势")
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"/platform/resource/trend"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO<List<PlatformResourcInfoVO>> getResourceTrend(){
        ResultVO<List<PlatformResourcInfoVO>> vo = new ResultVO<>(StatusCode.SYSTEM_ERR);
        Map<String ,PlatformResourcInfoVO> map = new HashMap<>();
        try{
            List<PlatformResourceSnapshot> resultList = platformResourceSnapshotService.getUseRateTrend();
            if(CollectionUtils.isNotEmpty(resultList)){
                resultList.forEach(platformResourceSnapshot -> {
                    String date = DateUtils.format(platformResourceSnapshot.getSnapshotDate(), DateUtils.FORMATER_DAY);
                    PlatformResourcInfoVO platformResourcInfoVO = new PlatformResourcInfoVO();
                    platformResourcInfoVO.setDate(date);
                    platformResourcInfoVO.setCpuUseRate(NumUtils.percent(platformResourceSnapshot.getAppliedCpu().longValue(), platformResourceSnapshot.getPlatformCpu().longValue()));
                    platformResourcInfoVO.setMemoryUseRate(NumUtils.percent(platformResourceSnapshot.getAppliedMemory().longValue(), platformResourceSnapshot.getPlatformMemory().longValue()));
                    platformResourcInfoVO.setStorageUseRate(NumUtils.percent(platformResourceSnapshot.getAppliedStorage(), platformResourceSnapshot.getPlatformStorage()));
                    map.put(date,platformResourcInfoVO);
                });
                vo.setData(doTrendList(map));
                vo.setCode(StatusCode.SUCCESS.getCode());
                vo.setMsg(StatusCode.SUCCESS.getDesc());
            }
        }catch (Exception ex){
            log.error("获取平台30天内资源走势失败。", ex);
        }
        return vo;
    }

    @ApiOperation(value = "平台管理 30天内日增数据量走势")
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"/platform/data_daily/trend"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO<List<PlatformResourcInfoVO>> getDataDailyTrend(){
        ResultVO<List<PlatformResourcInfoVO>> vo = new ResultVO<>(StatusCode.SYSTEM_ERR);
        Map<String ,PlatformResourcInfoVO> map = new HashMap<>();
        try{
            List<Map<String, Object>> resultList = projectResourceKpiSnapshotService.getTrend();
            if(CollectionUtils.isNotEmpty(resultList)){
                resultList.forEach(resultMap -> {
                    String date = DateUtils.format((Date) resultMap.get("kpiDate"), DateUtils.FORMATER_DAY);
                    PlatformResourcInfoVO platformResourcInfoVO = new PlatformResourcInfoVO();
                    platformResourcInfoVO.setDate(date);
                    platformResourcInfoVO.setDataDailyIncrTotal(resultMap.get("dataDailyIncrTotal").toString());
                    map.put(date, platformResourcInfoVO);
                });
                vo.setData(doTrendList(map));
                vo.setCode(StatusCode.SUCCESS.getCode());
                vo.setMsg(StatusCode.SUCCESS.getDesc());
            }
        }catch (Exception ex){
            log.error("获取平台30天内日增数据量走势失败。", ex);
        }
        return vo;
    }

    @ApiOperation(value = "平台管理 30天内任务完成率走势")
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"/platform/task_success_rate/trend"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO<List<PlatformResourcInfoVO>> getTaskSuccessRateTrend(){
        ResultVO<List<PlatformResourcInfoVO>> vo = new ResultVO<>(StatusCode.SYSTEM_ERR);
        Map<String ,PlatformResourcInfoVO> map = new HashMap<>();
        try{
            List<Map<String, Object>> resultList = projectResourceKpiSnapshotService.getTrend();
            if(CollectionUtils.isNotEmpty(resultList)){
                resultList.forEach(resultMap -> {
                    String date = DateUtils.format((Date) resultMap.get("kpiDate"), DateUtils.FORMATER_DAY);
                    PlatformResourcInfoVO platformResourcInfoVO = new PlatformResourcInfoVO();
                    platformResourcInfoVO.setDate(DateUtils.format((Date) resultMap.get("kpiDate"), DateUtils.FORMATER_DAY));
                    platformResourcInfoVO.setTaskSuccessRate(NumUtils.percent(((BigDecimal) resultMap.get("taskSuccess")).longValue(), ((BigDecimal) resultMap.get("taskTotal")).longValue()));
                    map.put(date, platformResourcInfoVO);
                });
                vo.setData(doTrendList(map));
                vo.setCode(StatusCode.SUCCESS.getCode());
                vo.setMsg(StatusCode.SUCCESS.getDesc());
            }
        }catch (Exception ex){
            log.error("获取平台30天内任务完成率走势失败。", ex);
        }
        return vo;
    }
    
    @ApiOperation(value = "当前正在执行任务列表 ")
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"/platform/executing_job/{jobType}/{pageNum}/{pageSize}"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO<PageInfo<ExecutingJobInfo>> getExecutingJobList(@PathVariable int jobType, @PathVariable int pageNum, @PathVariable int pageSize){
        if(pageNum <= 0 || pageSize <= 0) {
            log.error("分页参数错误：pageNum={}, pageSize={}", pageNum, pageSize);
            return ResultVO.error(StatusCode.PARAM_ERROR);
        }
        
        if(jobType == Job.JOB_TYPE_REAL) {
            return ResultVO.success(jobService.getExecutingStreamJobList(pageNum, pageSize));
        } else if (jobType == Job.JOB_TYPE_SYNC) {
            return ResultVO.success(jobService.getExecutingBatchJobList(pageNum, pageSize));
        } else {
            log.error("未知任务类型：{}", jobType);
            return ResultVO.error(StatusCode.PARAM_ERROR);
        }
    }

    private List<PlatformResourcInfoVO> doTrendList(Map<String, PlatformResourcInfoVO> map){
        List<String> dates = DateUtils.getDateList();
        return dates.stream().map(date->{
            if(map.containsKey(date)){
                return map.get(date);
            }else{
                PlatformResourcInfoVO vo = new PlatformResourcInfoVO();
                vo.setTaskSuccessRate("0");
                vo.setStorageUseRate("0");
                vo.setMemoryUseRate("0");
                vo.setCpuUseRate("0");
                vo.setDataDailyIncrTotal("0");
                vo.setDate(date);
                return vo;
            }
        }).collect(Collectors.toList());
    }
    
}
