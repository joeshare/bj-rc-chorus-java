package cn.rongcapital.chorus.server.dashboard.controller;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.util.OrikaBeanMapper;
import cn.rongcapital.chorus.das.entity.ResourceUsedStats;
import cn.rongcapital.chorus.das.service.ResourceUsedStatsService;
import cn.rongcapital.chorus.server.dashboard.vo.ResourceUsedStatsVO;
import cn.rongcapital.chorus.server.vo.ResultVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hhlfl on 2017-6-20.
 */
@Slf4j
@RestController
@RequestMapping(value = {"/dashboard"})
public class ResourceUsedStatsController {
    @Autowired
    private ResourceUsedStatsService resourceUsedStatsService;

    @ApiOperation(value = "表使用度TOP5查询")
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"tableUsed/{projectId}/top5"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO<List<ResourceUsedStatsVO>> getTableUsedTopN(@PathVariable("projectId") Long projectId){
        ResultVO<List<ResourceUsedStatsVO>> rvo = new ResultVO<List<ResourceUsedStatsVO>>(StatusCode.SYSTEM_ERR);
        List<ResourceUsedStatsVO> result = new ArrayList<ResourceUsedStatsVO>();
        try {
            List<String> types = new ArrayList<>();
            types.add("table");
            types.add("column");
            List<ResourceUsedStats> resourceUsedStatsList = resourceUsedStatsService.statsTableUsedTopN(projectId, types, 5);

            if(resourceUsedStatsList != null) {
                for (ResourceUsedStats resourceUsedStats : resourceUsedStatsList) {
                    ResourceUsedStatsVO usedStatsVO = new ResourceUsedStatsVO();
                    String resourceName = resourceUsedStats.getResourceName();
                    if(resourceName != null && resourceName.contains(".")){
                        int indx = resourceName.lastIndexOf(".");
                        if(indx < resourceName.length())
                            resourceName = resourceName.substring(indx+1);
                    }
                    usedStatsVO.setResourceName(resourceName);
                    usedStatsVO.setUsedCount(resourceUsedStats.getUsedCount());
                    result.add(usedStatsVO);
                }
            }
            rvo.setCode(StatusCode.SUCCESS.getCode());
            rvo.setMsg(StatusCode.SUCCESS.getDesc());
        }catch (Exception ex){
            log.error("获取项目[%s]下表的使用度TOP5 失败。", ex);
        }

        rvo.setData(result);

        return rvo;
    }

}
