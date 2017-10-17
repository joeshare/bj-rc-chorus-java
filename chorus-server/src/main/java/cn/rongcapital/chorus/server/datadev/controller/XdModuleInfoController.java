package cn.rongcapital.chorus.server.datadev.controller;

import cn.rongcapital.chorus.common.constant.XdModuleConstants;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.xd.XDClient;
import cn.rongcapital.chorus.server.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.xd.rest.domain.DetailedModuleDefinitionWithPageElementResource;

/**
 * Created by alan on 12/04/2017.
 */
@Slf4j
@Controller
@RequestMapping("/xd_module")
public class XdModuleInfoController {

    @Autowired
    private XDClient xdClient;

    @RequestMapping(value = "/detail/with_page_element/{moduleType}/{moduleName}", method = RequestMethod.GET)
    @ResponseBody
    public ResultVO<DetailedModuleDefinitionWithPageElementResource> getDetailWithPageElement(
            @PathVariable("moduleType") int moduleType, @PathVariable("moduleName") String moduleName) {
        try {
            return ResultVO.success(xdClient.getModuleDefinitionWithPageElement(
                    XdModuleConstants.XdModuleTypeConstants.getModuleTypeByInt(moduleType),
                    moduleName));
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.error("Caught error in getDetailWithPageElement: moduleType: {}, moduleName: {}",
                    moduleType, moduleName);
            log.error("Caught error in getDetailWithPageElement !", e);
            return ResultVO.error();
        }
    }
}
