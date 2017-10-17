package cn.rongcapital.chorus.server.resourceTemplate.controller;

import cn.rongcapital.chorus.das.entity.ResourceTemplate;
import cn.rongcapital.chorus.das.service.ResourceTemplateService;
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
public class ResourceTemplateController {

    @Autowired
    private ResourceTemplateService resourceTemplateService;

    @RequestMapping("/resource_template/list_all")
    public ResultVO<List<ResourceTemplate>> listAll() {
        try {
            return ResultVO.success(resourceTemplateService.listAll());
        } catch (Exception e) {
            log.warn("Caught exception in listAll", e);
            return ResultVO.error();
        }
    }
}
