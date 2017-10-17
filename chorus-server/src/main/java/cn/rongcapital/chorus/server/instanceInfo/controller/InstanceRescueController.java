package cn.rongcapital.chorus.server.instanceInfo.controller;

import cn.rongcapital.chorus.resourcemanager.service.RescueService;
import cn.rongcapital.chorus.server.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by alan on 12/8/16.
 */
@Slf4j
@RestController
public class InstanceRescueController {

    @Autowired
    private RescueService rescueService;

    @RequestMapping(value = "/instance_rescue", method = RequestMethod.POST)
    public ResultVO<Object> rescue() {
        try {
            rescueService.rescueAfterXdRestarted();
            return ResultVO.success();
        } catch (Exception e) {
            log.error("ERROR in rescue !!!", e);
            return ResultVO.error();
        }
    }

    @RequestMapping(value = "/instance_rescue/{projectId}", method = RequestMethod.POST)
    public ResultVO<Object> rescueByProject(@PathVariable Long projectId) {
        try {
            rescueService.rescueAfterXdRestarted(projectId);
            return ResultVO.success();
        } catch (Exception e) {
            log.error("ERROR in rescue !!!", e);
            return ResultVO.error();
        }
    }

}
