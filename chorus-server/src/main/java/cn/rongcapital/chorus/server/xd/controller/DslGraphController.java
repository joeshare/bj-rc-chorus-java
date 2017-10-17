package cn.rongcapital.chorus.server.xd.controller;

import cn.rongcapital.chorus.common.xd.service.DslGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * DSL和Graph定义相互转换接口
 *
 * @author li.hzh
 * @date 2016-11-15 15:12
 */
@RestController
public class DslGraphController {

    @Autowired
    private DslGraphService dslGraphService;

    @RequestMapping(value = "/tools/convertJobGraphToDsl", method = RequestMethod.POST)
    public String graphToDsl(@RequestBody String text) {
        return dslGraphService.fromGraphToDSL(text);
    }

    @RequestMapping(value = "/tools/fromDslToGraph", method = RequestMethod.POST)
    public String dslToGraph(@RequestBody String text) {
        return dslGraphService.fromDSLToGraph(text);
    }

}
