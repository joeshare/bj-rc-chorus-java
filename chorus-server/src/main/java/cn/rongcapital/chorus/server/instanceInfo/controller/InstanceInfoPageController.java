package cn.rongcapital.chorus.server.instanceInfo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by alan on 12/2/16.
 */
@Controller
public class InstanceInfoPageController {

    @RequestMapping("/instance_info/list_page")
    public String listPage() {
        return "instance_info/list_page";
    }
}
