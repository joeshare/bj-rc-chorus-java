package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.XdPropElement;
import cn.rongcapital.chorus.das.entity.web.R;
import cn.rongcapital.chorus.das.entity.web.XdModulePropCause;

import java.util.List;

/**
 * Spring XD Module属性信息
 *
 * @author lengyang
 */
public interface XdModulePropService {

	/**
	 * 取得组件属性信息列表
	 *
	 * @param cause
	 */
	List<XdPropElement> getModulePropList(XdModulePropCause cause);

	/**
	 * 根据条件查询数据条数
	 *
	 * @param cause
	 */
	int getModulePropCount(XdModulePropCause cause);

	/**
	 * 根据主键取得组件属性信息
	 *
	 * @param moduleId
	 */
	XdPropElement getModuleProp(int moduleId);

	/**
	 * 根据主键逻辑删除数据
	 *
	 * @param modulePropId
	 */
	R logicDelete(int modulePropId);

	/**
	 * 根据组件属性名取得组件属性信息
	 *
	 * @param cause
	 */
	List<XdPropElement> getModulePropByName(XdModulePropCause cause);

	/**
	 * 添加组件属性信息
	 *
	 * @param xdModuleProp
	 */
	R saveModuleProp(XdPropElement xdModuleProp);

	/**
	 * 更新组件属性信息
	 *
	 * @param xdModuleProp
	 */
	R updateModuleProp(XdPropElement xdModuleProp);
}
