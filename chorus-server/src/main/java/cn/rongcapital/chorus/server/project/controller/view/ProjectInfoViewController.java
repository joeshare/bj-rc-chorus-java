package cn.rongcapital.chorus.server.project.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by shicheng on 2016/11/29.
 */
@Controller
@RequestMapping(value = "/view")
public class ProjectInfoViewController {

    /**
     * 项目列表路径
     * @return 页面视图
     */
    @RequestMapping(value = "projectinfo/list", method = RequestMethod.GET)
    String listView() {
        return "project/projectList";
    }

}
