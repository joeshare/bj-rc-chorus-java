package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.XdModule;
import cn.rongcapital.chorus.das.entity.XdModuleDO;
import cn.rongcapital.chorus.das.entity.XdPropElement;
import cn.rongcapital.chorus.das.entity.web.XdModuleCause;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface XdModuleMapper {
	/**
	 * 列出所有Spring XD Module信息
	 *
	 * @return
	 */
	@Select({
		"select module_id as moduleId,module_type as moduleType,module_name as moduleName,module_alias_name as moduleAliasName,remark, sort_num as sortNum, create_user as createUser, create_time as createTime, update_user as updateUser, update_time as updateTime ",
		" from xd_module where use_yn = 'Y'"
	})
	List<XdModule> selectAll();

	/**
	 * 根据类型Spring XD Module信息
	 *
	 * @param moduleType
	 * @return
	 */
	@Select({
		"select module_id as moduleId,module_type as moduleType,module_name as moduleName,module_alias_name as moduleAliasName,remark, sort_num as sortNum, create_user as createUser, create_time as createTime, update_user as updateUser, update_time as updateTime ",
		" from xd_module ",
		" where use_yn = 'Y' and module_type = #{moduleType} order by sort_num "
	})
	List<XdModule> selectByType(int moduleType);

	/**
	 * 根据类型Spring XD Module信息（Stream）
	 *
	 * @return
	 */
	@Select({
			"select module_id as moduleId,module_type as moduleType,module_name as moduleName,module_alias_name as moduleAliasName,remark, sort_num as sortNum, create_user as createUser, create_time as createTime, update_user as updateUser, update_time as updateTime ",
			" from xd_module ",
			" where use_yn = 'Y' and module_type not in (1,2,11) order by moduleType, sort_num "
	})
    List<XdModule> selectStreamModules();

	/**
	 * 根据类型Spring XD Module信息（Stream）
	 *
	 * @return
	 */
	@Select({
			"select module_id as moduleId,module_type as moduleType,module_name as moduleName,module_alias_name as moduleAliasName,remark, sort_num as sortNum, create_user as createUser, create_time as createTime, update_user as updateUser, update_time as updateTime ",
			" from xd_module ",
			" where use_yn = 'Y' and module_type = 11 order by sort_num "
	})
    List<XdModule> selectJobDefinition();
	
	/**
	 *  通过xdmoduleid获取属性信息
	 *
	 * @param moduleId
	 * @return
	 */
	@Select({
		"select b.`name`,b.type,b.label,b.validate_config as validateConfig from xd_module a INNER JOIN xd_prop_element b on a.module_id = b.xd_module_id ",
		" where b.use_yn = 'Y' and b.xd_module_id = #{moduleId} order by b.sort_num "
	})
	List<XdPropElement> selectXdPropElementByModuleId(Integer moduleId);

	/**
	 *  通过xdmoduleid获取属性信息
	 *
	 * @param cause
	 * @return
	 */
	@SelectProvider(type = XdModuleSqlProvider.class, method = "getModuleList")
	List<XdModuleDO> getModuleList(XdModuleCause cause);

	/**
	 * 根据条件查询数据条数
	 *
	 * @param cause
	 */
	@SelectProvider(type = XdModuleSqlProvider.class, method = "getModuleCount")
	int getModuleCount(final XdModuleCause cause);

	/**
	 * 根据主键逻辑删除组件
	 *
	 * @param moduleId
	 */
	@Update({"update xd_module ",
			"set use_yn = 'N'",
			"where module_id = #{moduleId}"})
	void logicDelete(int moduleId);

	/**
	 *  保存组件信息
	 *
	 * @param xdModule
	 * @return
	 */
	@Insert({
		"insert into xd_module (module_id, module_type,module_name,module_alias_name,remark,sort_num, use_yn,module_level,project_id,module_category,file_name," +
				" module_view_name, create_user_name, create_user, create_time, update_user_name, update_user, update_time) values (",
			"#{moduleId,jdbcType=INTEGER}, ",
			"#{moduleType,jdbcType=INTEGER}, ",
			"#{moduleName,jdbcType=VARCHAR}, ",
			"#{moduleAliasName,jdbcType=VARCHAR}, ",
			"#{remark,jdbcType=VARCHAR}, ",
			"#{sortNum,jdbcType=INTEGER}, ",
			"#{useYn,jdbcType=VARCHAR}, ",
			"#{moduleLevel,jdbcType=INTEGER}, ",
			"#{projectId,jdbcType=BIGINT}, ",
			"#{moduleCategory,jdbcType=INTEGER}, ",
			"#{fileName,jdbcType=VARCHAR}, ",
			"#{moduleViewName,jdbcType=VARCHAR}, ",
			"#{createUserName,jdbcType=VARCHAR}, ",
			"#{createUser,jdbcType=VARCHAR}, ",
			"#{createTime,jdbcType=TIMESTAMP}, ",
			"#{updateUser,jdbcType=VARCHAR}, ",
			"#{updateUserName,jdbcType=VARCHAR}, ",
			"#{updateTime,jdbcType=TIMESTAMP}) "
	})
	int saveModule(XdModule xdModule);

	/**
	 *  保存组件信息
	 *
	 * @param xdModule
	 * @return
	 */
	@Update({
			"update xd_module set module_type=#{moduleType},",
			"module_name=#{moduleName},",
			"module_alias_name=#{moduleAliasName},",
			//"sort_num=#{sortNum},",
			"remark=#{remark},",
			"file_name=#{fileName},",
			"module_level=#{moduleLevel},",
			"module_category=#{moduleCategory},",
			"update_user_name=#{updateUserName},",
			"update_user=#{updateUser},",
			"update_time=#{updateTime}",
			"where module_id=#{moduleId} "
	})
	int updateModule(XdModule xdModule);

	/**
	 *  通过xdmoduleid获取组件信息
	 *
	 * @param moduleId
	 * @return
	 */
	@Select({
		"select module_id as moduleId,module_type as moduleType,module_name as moduleName,module_alias_name as moduleAliasName,remark, sort_num as sortNum, create_user as createUser, create_time as createTime, update_user as updateUser, update_time as updateTime ",
		" from xd_module where use_yn = 'Y' and module_id = #{moduleId}"
	})
	XdModule getModule(int moduleId);

	/**
	 *  根据组件名取得组件信息
	 *
	 * @param cause
	 * @return
	 */
	@Select({
		"select module_id as moduleId,module_type as moduleType,module_name as moduleName,module_alias_name as moduleAliasName,remark, sort_num as sortNum, create_user as createUser, create_time as createTime, update_user as updateUser, update_time as updateTime ",
		" from xd_module where use_yn = 'Y' and module_name = #{moduleName} and module_type = #{moduleType}"
	})
	List<XdModule> getModuleByName(XdModuleCause cause);
}
