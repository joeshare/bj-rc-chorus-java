package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.XdModuleMapper;
import cn.rongcapital.chorus.das.entity.XdModuleDO;
import cn.rongcapital.chorus.das.service.OperLogService;
import cn.rongcapital.chorus.das.service.XdModuleService;
import cn.rongcapital.chorus.das.entity.XdModule;
import cn.rongcapital.chorus.das.entity.XdPropElement;
import cn.rongcapital.chorus.das.entity.web.XdModuleCause;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * XD组件管理SERVICE实现类
 *
 * @author lengyang
 */
@Service(value = "XdModuleService")
@Transactional
public class XdModuleServiceImpl implements XdModuleService {

	@Autowired(required = false)
	private XdModuleMapper xdModuleMapper;
	@Autowired(required = false)
	private OperLogService log;

	/**
	 * 列出所有Spring XD Module信息
	 */
	@Override
	public List<XdModule> selectAll() {
		return xdModuleMapper.selectAll();
	}

	/**
	 * 根据类型Spring XD Module信息
	 *
	 * @param moduleType
	 * @return
	 */
	@Override
	public List<XdModule> selectByType(int moduleType) {
		return xdModuleMapper.selectByType(moduleType);
	}

	/**
	 * 取得Spring XD Stream Module信息
	 *
	 * @return
	 */
	@Override
	public List<XdModule> selectStreamModules() {
		return xdModuleMapper.selectStreamModules();
	}

	/**
	 * 取得Spring XD JobDefinition信息
	 *
	 * @return
	 */
	@Override
	public List<XdModule> selectJobDefinition() {
		return xdModuleMapper.selectJobDefinition();
	}

	/**
	 * 通过xdmoduleid获取属性信息
	 *
	 * @return
	 */
	@Override
	public List<XdPropElement> selectXdPropElementByModuleId(Integer moduleId) {
		return xdModuleMapper.selectXdPropElementByModuleId(moduleId);
	}

	@Override
	public List<XdModuleDO> getModuleList(XdModuleCause cause) {
		return xdModuleMapper.getModuleList(cause);
	}

	@Override
	public int getModuleCount(XdModuleCause cause) {
		return xdModuleMapper.getModuleCount(cause);
	}

	@Override
	public XdModule getModule(int moduleId) {
		return xdModuleMapper.getModule(moduleId);
	}

	@Override
	public void logicDelete(int moduleId) {
		xdModuleMapper.logicDelete(moduleId);
		// 表名
//		String table = "xd_module";
		// 操作
//		String oper_log = "动作：删除组件 主键值：" + moduleId;
		// log添加
//		log.save(table, oper_log, String.valueOf(moduleId));

//		return true;
	}

	@Override
	public List<XdModule> getModuleByName(XdModuleCause cause) {
		return xdModuleMapper.getModuleByName(cause);
	}

	@Override
	public boolean saveModule(XdModule xdModule) {
		XdModuleCause cause = new XdModuleCause();
		cause.setModuleName(xdModule.getModuleName());
		cause.setModuleType(xdModule.getModuleType());
		List<XdModule> nameList = getModuleByName(cause);
		// 验证失败
		if (CollectionUtils.isNotEmpty(nameList)) {
			return false;
		}
		xdModuleMapper.saveModule(xdModule);
		return true;
	}

	@Override
	public void updateModule(XdModule xdModule) {
		xdModuleMapper.updateModule(xdModule);
	}

	@Override
	public void saveOrUpdate(XdModule xdModule) {
		XdModuleCause cause = new XdModuleCause();
		cause.setModuleName(xdModule.getModuleName());
		cause.setModuleType(xdModule.getModuleType());
		List<XdModule> nameList = getModuleByName(cause);
		if(nameList == null || nameList.isEmpty()){
			xdModuleMapper.saveModule(xdModule);
		}else{
			xdModule.setModuleId(nameList.iterator().next().getModuleId());
			xdModule.setUpdateUser(xdModule.getCreateUser());
			xdModule.setUpdateTime(xdModule.getCreateTime());
			xdModule.setUpdateUserName(xdModule.getCreateUserName());
			xdModuleMapper.updateModule(xdModule);
		}

	}

	@Override
	public List<XdModuleDO> getModuleListByPage(XdModuleCause cause,int pageNum, int pageSize) {
		PageHelper.startPage(pageNum,pageSize);
		return xdModuleMapper.getModuleList(cause);
	}


}
