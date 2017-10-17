package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.XdModulePropMapper;
import cn.rongcapital.chorus.das.service
		.OperLogService;
import cn.rongcapital.chorus.das.service.XdModulePropService;
import cn.rongcapital.chorus.das.entity.XdPropElement;
import cn.rongcapital.chorus.das.entity.web.R;
import cn.rongcapital.chorus.das.entity.web.XdModulePropCause;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * XD组件属性管理SERVICE实现类
 *
 * @author lengyang
 */
@Service(value = "XdModulePropService")
@Transactional
public class XdModulePropServiceImpl implements XdModulePropService {

	@Autowired(required = false)
	private XdModulePropMapper xdModulePropMapper;
	@Autowired(required = false)
	private OperLogService log;

	@Override
	public List<XdPropElement> getModulePropList(XdModulePropCause cause) {
		return xdModulePropMapper.getModulePropList(cause);
	}

	@Override
	public int getModulePropCount(XdModulePropCause cause) {
		return xdModulePropMapper.getModulePropCount(cause);
	}

	@Override
	public XdPropElement getModuleProp(int moduleId) {
		return xdModulePropMapper.getModuleProp(moduleId);
	}

	@Override
	public R logicDelete(int modulePropId) {
		xdModulePropMapper.logicDelete(modulePropId);
		// 表名
		String table = "xd_prop_element";
		// 操作
		String oper_log = "动作：删除组件属性 主键值：" + modulePropId;
		// log添加
//		log.save(table, oper_log, String.valueOf(modulePropId));

		return R.trueState("组件删除成功");
	}

	@Override
	public List<XdPropElement> getModulePropByName(XdModulePropCause cause) {
		return xdModulePropMapper.getModulePropByName(cause);
	}

	@Override
	public R saveModuleProp(XdPropElement xdPropElement) {
		XdModulePropCause cause = new XdModulePropCause();
		cause.setName(xdPropElement.getName());
		cause.setModuleType(xdPropElement.getXdModuleType());
		List<XdPropElement> nameList = getModulePropByName(cause);
		// 验证失败
		if (CollectionUtils.isNotEmpty(nameList)) {
			return R.falseState("组件名称已存在");
		}
		xdModulePropMapper.saveModuleProp(xdPropElement);
		return R.trueState("组件保存成功");
	}

	@Override
	public R updateModuleProp(XdPropElement xdModule) {
		xdModulePropMapper.updateModuleProp(xdModule);
		return R.trueState("组件保存成功");
	}

}
