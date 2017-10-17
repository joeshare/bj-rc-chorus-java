package cn.rongcapital.chorus.server.environmentInfo.controller;

import cn.rongcapital.chorus.das.entity.EnvironmentInfo;
import cn.rongcapital.chorus.das.service.EnvironmentInfoService;
import cn.rongcapital.chorus.server.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by alan on 12/2/16.
 */
@Slf4j
@RestController
public class EnvironmentInfoController {

    @Autowired
    private EnvironmentInfoService environmentInfoService;

    @RequestMapping("/environment/list_all")
    public ResultVO<List<EnvironmentInfo>> listAll() {
        try {
            return ResultVO.success(environmentInfoService.listAll());
        } catch (Exception e) {
            log.warn("Caught exception in listAll", e);
            return ResultVO.error();
        }
    }
}
