package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.XdPropElement;
import cn.rongcapital.chorus.das.entity.web.XdModulePropCause;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface XdModulePropMapper {

	/**
	 *  通过xdmoduleid获取属性信息
	 *
	 * @param cause
	 * @return
	 */
	@SelectProvider(type = XdModulePropSqlProvider.class, method = "getModulePropList")
	List<XdPropElement> getModulePropList(XdModulePropCause cause);

	/**
	 * 根据条件查询数据条数
	 *
	 * @param cause
	 */
	@SelectProvider(type = XdModulePropSqlProvider.class, method = "getModulePropCount")
	int getModulePropCount(final XdModulePropCause cause);

	/**
	 * 根据主键逻辑删除组件属性
	 *
	 * @param id
	 */
	@Update({"update xd_prop_element ",
			"set use_yn = 'N'",
			"where id = #{id}"})
	void logicDelete(int id);

	/**
	 *  保存组件属性信息
	 *
	 * @param xdPropElement
	 * @return
	 */
	@Insert({
		"insert into xd_prop_element (id, xd_module_id,xd_module_type,name,type,label,placeholder,validate_config,remark,sort_num, use_yn, " +
				"create_user, create_time, update_user, update_time) values (",
			"#{id,jdbcType=INTEGER}, ",
			"#{xdModuleId,jdbcType=INTEGER}, ",
			"#{xdModuleType,jdbcType=INTEGER}, ",
			"#{name,jdbcType=VARCHAR}, ",
			"#{type,jdbcType=VARCHAR}, ",
			"#{label,jdbcType=VARCHAR}, ",
			"#{placeholder,jdbcType=VARCHAR}, ",
			"#{validateConfig,jdbcType=VARCHAR}, ",
			"#{remark,jdbcType=VARCHAR}, ",
			"#{sortNum,jdbcType=INTEGER}, ",
			"#{useYn,jdbcType=VARCHAR}, ",
			"#{createUser,jdbcType=VARCHAR}, ",
			"#{createTime,jdbcType=TIMESTAMP}, ",
			"#{updateUser,jdbcType=VARCHAR}, ",
			"#{updateTime,jdbcType=TIMESTAMP}) "
	})
	int saveModuleProp(XdPropElement xdPropElement);

	/**
	 *  保存组件属性信息
	 *
	 * @param xdPropElement
	 * @return
	 */
	@Update({
			"update xd_prop_element set name=#{name},",
			"type=#{type},",
			"label=#{label},",
			"name=#{name},",
			"placeholder=#{placeholder},",
			"validate_config=#{validateConfig},",
			"sort_num=#{sortNum},",
			"remark=#{remark},",
			"update_user=#{updateUser},",
			"update_time=#{updateTime}",
			"where id=#{id} "
	})
	int updateModuleProp(XdPropElement xdPropElement);

	/**
	 *  通过xdmoduleid获取组件属性信息
	 *
	 * @param moduleId
	 * @return
	 */
	@Select({
		"select id as id,xd_module_id as xdModuleId,xd_module_type as xdModuleType,name as name,type as type,label as label,placeholder as placeholder,validate_config as validateConfig,remark, sort_num as sortNum, create_user as createUser, create_time as createTime, update_user as updateUser, update_time as updateTime ",
		" from xd_prop_element where use_yn = 'Y' and id = #{id}"
	})
	XdPropElement getModuleProp(int moduleId);

	/**
	 *  根据组件属性名取得组件属性信息
	 *
	 * @param cause
	 * @return
	 */
	@Select({
		"select id as id,xd_module_id as xdModuleId,xd_module_type as xdModuleType,name as name,type as type,label as label,placeholder as placeholder,validate_config as validateConfig,remark, sort_num as sortNum, create_user as createUser, create_time as createTime, update_user as updateUser, update_time as updateTime ",
		" from xd_prop_element where use_yn = 'Y' and name = #{name} and xd_module_id = #{moduleId}"
	})
	List<XdPropElement> getModulePropByName(XdModulePropCause cause);
}
