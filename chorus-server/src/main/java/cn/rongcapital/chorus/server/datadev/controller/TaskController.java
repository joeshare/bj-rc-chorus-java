package cn.rongcapital.chorus.server.datadev.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.rongcapital.chorus.das.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.rongcapital.chorus.das.service.TaskService;
import cn.rongcapital.chorus.das.service.XdModuleService;
import cn.rongcapital.chorus.das.entity.XdPropElement;
import cn.rongcapital.chorus.das.entity.web.TaskCause;

@Controller
public class TaskController {

	/**
	 * 任务步骤
	 */
	@Autowired
	private TaskService taskService = null;
	@Autowired
	private XdModuleService xdModuleService = null;
	/**
	 * 立即执行
	 * 
	 * @param cause
	 * @return
	 */
	@RequestMapping(value = "/task/getTaskInfo", method = RequestMethod.POST)
	@ResponseBody
	public Task getTaskInfo(@RequestBody TaskCause cause) {
		return taskService.selectTaskByName(cause.getJobId(), cause.getTaskName());
	}

	/**
	 * 取得执行单元信息
	 *
	 * @param moduleId
	 * @return
	 */
	@RequestMapping(value = "/xdmodule/propInfo/{moduleId}", method = RequestMethod.GET)
	@ResponseBody
	public List<XdPropElement> instanceInfo(@PathVariable Integer moduleId) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		List<XdPropElement> XdPropElementList = xdModuleService.selectXdPropElementByModuleId(moduleId);
		// 如果查询数据存在
		if (XdPropElementList != null) {
			return XdPropElementList;
		} else {
			return null;
		}
	}
}