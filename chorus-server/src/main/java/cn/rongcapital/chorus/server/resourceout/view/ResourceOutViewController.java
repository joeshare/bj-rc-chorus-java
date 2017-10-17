package cn.rongcapital.chorus.server.resourceout.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by shicheng on 2016/12/1.
 */
@Controller
@RequestMapping(value = "/view")
public class ResourceOutViewController {

    /**
     * 项目列表路径
     *
     * @return 页面视图
     */
    @RequestMapping(value = "/resource/out/list", method = RequestMethod.GET)
    String view() {
        return "resourceout/list";
    }

}
