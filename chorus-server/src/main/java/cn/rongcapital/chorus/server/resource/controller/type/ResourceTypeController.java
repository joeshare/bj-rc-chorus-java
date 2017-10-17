package cn.rongcapital.chorus.server.resource.controller.type;

import cn.rongcapital.chorus.das.service.ResourceTypeService;
import cn.rongcapital.chorus.server.vo.ResultVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by shicheng on 2016/12/5.
 */
@RestController
@RequestMapping(value = {"/resource/type"})
public class ResourceTypeController {

    private ResourceTypeService resourceTypeService;

    @Resource(name = "ResourceTypeService")
    public void setService(ResourceTypeService service) {
        this.resourceTypeService = service;
    }

    @RequestMapping(value = {"get/list"}, method = RequestMethod.POST)
    ResultVO selectResourceTypes() {
        return ResultVO.success(resourceTypeService.selectResourceTypes());
    }

}
