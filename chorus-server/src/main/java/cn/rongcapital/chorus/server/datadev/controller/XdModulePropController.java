package cn.rongcapital.chorus.server.datadev.controller;

import cn.rongcapital.chorus.das.service.XdModulePropService;
import cn.rongcapital.chorus.das.entity.PagingEntity;
import cn.rongcapital.chorus.das.entity.XdPropElement;
import cn.rongcapital.chorus.das.entity.web.R;
import cn.rongcapital.chorus.das.entity.web.XdModulePropCause;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class XdModulePropController {

	/**
	 * Spring XD Module信息
	 */
	@Autowired
	private XdModulePropService xdModulePropService = null;
	@RequestMapping(value = {"/xdModuleProp/index"}, method = RequestMethod.GET)
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("xdModule/modulePropList");
		return modelAndView;
	}
	/**
	 * 取得组件属性信息
	 *
	 * @param cause
	 * @return
	 */
	@RequestMapping(value = "/xdModuleProp/list", method = RequestMethod.POST)
	@ResponseBody
	public PagingEntity list(@RequestBody XdModulePropCause cause) {

		//List<XdModule> moduleList = xdModulePropService.getModulePropList(cause);
		List<XdPropElement> XdPropElementList = xdModulePropService.getModulePropList(cause);

		// list声明
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 如果查询数据存在
		if (XdPropElementList != null) {
			int i = 0;
			// 获取用户数据
			for (XdPropElement item : XdPropElementList) {
				// Map声明
				Map<String, Object> map = new HashMap<String, Object>();
				// 属性ID
				map.put("id", item.getId());
				// 属性XdModuleId
				map.put("XdModuleId", item.getXdModuleId());
				// 属性名
				map.put("name", item.getName());
				// 属性类型
				map.put("type", item.getType());
				// 显示Lable
				map.put("label", item.getLabel());
				// 描述信息
				map.put("placeholder", item.getPlaceholder());
				// 验证信息
				map.put("validate_config", item.getValidateConfig());
				// 顺序号
				map.put("sortNum", item.getSortNum());
				// 备注
				map.put("remark", item.getRemark());
				map.put("row_number", i);
				list.add(map);
				i++;
			}
		}

		int count = xdModulePropService.getModulePropCount(cause);
		PagingEntity pagingEntity = new PagingEntity();
		pagingEntity.setiTotalDisplayRecords(count);
		pagingEntity.setAaData(list);
		return pagingEntity;
	}

	/**
	 * 组件信息查询
	 *
	 * @throws IOException
	 */
	@RequestMapping(value = {"/xdModuleProp/delete/{modulePropId}"}, method = RequestMethod.POST)
	@ResponseBody
	public R delete(@PathVariable int modulePropId) throws IOException {
		return xdModulePropService.logicDelete(modulePropId);
	}
	
	/**
	 * 组件信息查询
	 *
	 * @throws IOException
	 */
	@RequestMapping(value = {"/xdModuleProp/getModule/{moduleId}"}, method = RequestMethod.POST)
	@ResponseBody
	public XdPropElement getModule(@PathVariable int moduleId) throws IOException {
		return xdModulePropService.getModuleProp(moduleId);
	}
	
	/**
	 * 组件信息更新
	 *
	 * @throws IOException
	 *
	 */
	@RequestMapping(value = { "/xdModuleProp/update" }, method = RequestMethod.POST)
	@ResponseBody
	public R update(@RequestBody XdPropElement xdModuleProp) throws IOException {
		// 保存修改
		XdModulePropCause cause = new XdModulePropCause();
		cause.setModuleId(xdModuleProp.getXdModuleId());
	 
		List<XdPropElement> modulePropList = xdModulePropService.getModulePropList(cause);
		if (CollectionUtils.isNotEmpty(modulePropList)) {
			for (XdPropElement item : modulePropList) {
				if (xdModuleProp.getId() != item.getXdModuleId() && item.getName() == xdModuleProp.getName()) {
					return R.falseState("名称重复");
				}
			}
		}
//		User user = UserSessionUtil.getUserSession();
		Date now = new Date();
		xdModuleProp.setUpdateTime(now);
		// 前端传递
//		xdModuleProp.setUpdateUser(user.getId());
		return xdModulePropService.updateModuleProp(xdModuleProp);
	}
	
	/**
	 * 组件信息更新
	 *
	 * @throws UnsupportedEncodingException
	 *
	 */
	@RequestMapping(value = { "/xdModuleProp/checkName" }, method = RequestMethod.POST)
	@ResponseBody
	public boolean checkName(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		boolean result = true;
		String modulePropId = request.getParameter("id");
		String moduleId = request.getParameter("moduleId");
		String modulePropName = URLDecoder.decode(request.getParameter("name"), "UTF-8");
		XdModulePropCause cause = new XdModulePropCause();
		cause.setModuleId(Integer.valueOf(moduleId));
		cause.setName(modulePropName);
		List<XdPropElement> modulePropList = xdModulePropService.getModulePropByName(cause);

		if (CollectionUtils.isNotEmpty(modulePropList)) {
			if (modulePropId == null) {
				result = false;
			} else {
				for (XdPropElement item : modulePropList) {
					if (!modulePropId.equals(item.getId().toString())) {
						result = false;
						break;
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 组件信息添加
	 *
	 * @throws IOException
	 *
	 */
	@RequestMapping(value = { "/xdModuleProp/save" }, method = RequestMethod.POST)
	@ResponseBody
	public R save(@RequestBody XdPropElement xdModuleProp) throws IOException {
		// 保存
		// 前端传递
//		User user = UserSessionUtil.getUserSession();
		Date now = new Date();
		xdModuleProp.setUseYn("Y");
		xdModuleProp.setCreateTime(now);
//		xdModuleProp.setCreateUser(user.getId());
		xdModuleProp.setUpdateTime(now);
//		xdModuleProp.setUpdateUser(user.getId());
		// TODO:orderNum
		//xdModuleProp.setSortNum(0);
		return xdModulePropService.saveModuleProp(xdModuleProp);
	}
}