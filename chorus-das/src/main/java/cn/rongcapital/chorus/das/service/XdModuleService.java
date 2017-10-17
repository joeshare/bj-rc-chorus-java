package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.XdModule;
import cn.rongcapital.chorus.das.entity.XdModuleDO;
import cn.rongcapital.chorus.das.entity.XdPropElement;
import cn.rongcapital.chorus.das.entity.web.XdModuleCause;

import java.util.List;

/**
 * Spring XD Module信息
 *
 * @author lengyang
 */
public interface XdModuleService {

	/**
	 * 列出所有Spring XD Module信息
	 *
	 * @return
	 */
	List<XdModule> selectAll();

	/**
	 * 根据类型Spring XD Module信息
	 *
	 * @param moduleType
	 * @return
	 */
	List<XdModule> selectByType(int moduleType);

	/**
	 * 取得Spring XD Stream Module信息
	 *
	 * @return
	 */
	List<XdModule> selectStreamModules();

	/**
	 * 取得Spring XD JobDefinition信息
	 *
	 * @return
	 */
	List<XdModule> selectJobDefinition();

	/**
	 * 通过xdmoduleid获取属性信息
	 */
	List<XdPropElement> selectXdPropElementByModuleId(Integer moduleId);

	/**
	 * 取得组件信息列表
	 *
	 * @param cause
	 */
	List<XdModuleDO> getModuleList(XdModuleCause cause);

	/**
	 * 根据条件查询数据条数
	 *
	 * @param cause
	 */
	int getModuleCount(XdModuleCause cause);

	/**
	 * 根据主键取得组件信息
	 *
	 * @param moduleId
	 */
	XdModule getModule(int moduleId);

	/**
	 * 根据主键逻辑删除数据
	 *
	 * @param moduleId
	 */
	void logicDelete(int moduleId);

	/**
	 * 根据组件名取得组件信息
	 *
	 * @param cause
	 */
	List<XdModule> getModuleByName(XdModuleCause cause);

	/**
	 * 添加组件信息
	 *
	 * @param xdModule
	 */
	boolean saveModule(XdModule xdModule);

	/**
	 * 更新组件信息
	 *
	 * @param xdModule
	 */
	void updateModule(XdModule xdModule);

	/***
	 * 如果已经存在则更新覆盖，否则插入
	 *
	 * @param xdModule
	 * @return
	 */
	void saveOrUpdate(XdModule xdModule);


	/**
	 * 按分页获取组件信息列表
	 *
	 * @param cause
	 * @param pageNum
	 * @param pageSize
	 */
	List<XdModuleDO> getModuleListByPage(XdModuleCause cause, int pageNum, int pageSize);
}
