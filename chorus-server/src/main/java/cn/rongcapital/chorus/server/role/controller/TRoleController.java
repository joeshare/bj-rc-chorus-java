package cn.rongcapital.chorus.server.role.controller;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.entity.TRole;
import cn.rongcapital.chorus.das.service.TRoleService;
import cn.rongcapital.chorus.server.vo.ResultVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by shicheng on 2017/4/26.
 */
@RestController
@RequestMapping(value = {"/role/project"})
@Slf4j
public class TRoleController {

    private TRoleService roleService = null;

    @Resource(name = "TRoleService")
    public void setService(TRoleService service) {
        this.roleService = service;
    }

    @ApiOperation(value = "添加授权信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleInfo", paramType = "body", required = true, dataType = "TRole", value = "授权路由信息, 描述如下: </br>" +
                    "<h7>createUser</h7>: (必填项) 创建权限的用户 id </ br>" +
                    "<p><h7>permission</h7>: (必填项) 权限信息, 多个按照, 分割</ br></p>" +
                    "<h7>roleCode</h7>: (必填项) 权限编码 *</ br>" +
                    "<h7>roleName</h7>: (必填项) 权限名称 *</ br>" +
                    "<h7>roleType</h7>: (必填项) 权限类型 *</ br>")
    })
    @RequestMapping(value = {"new"}, method = RequestMethod.POST)
    ResultVO addRole(@RequestBody TRole roleInfo) {
        try {
            if (roleInfo != null) {
                int count = roleService.insert(roleInfo);
                if (count > 0) {
                    ResultVO.success();
                }
            }
        } catch (Exception e) {
            log.error("add role error", e);
            return ResultVO.error();
        }
        return ResultVO.error(StatusCode.JOBMONITOR_DATA_EMPTY);
    }

    @ApiOperation(value = "获取所有的授权信息")
    @RequestMapping(value = {"get"}, method = RequestMethod.GET)
    ResultVO getRoles() {
        try {
            return ResultVO.success(roleService.selectAll());
        } catch (Exception e) {
            log.error("get role error", e);
            return ResultVO.error();
        }
    }

    @ApiOperation(value = "根据权限 code 获取授权信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleCode", paramType = "path", required = true, dataType = "string", value = "权限编码")
    })
    @RequestMapping(value = {"get/code/{roleCode}"}, method = RequestMethod.GET)
    ResultVO getRoleByCode(@PathVariable String roleCode) {
        try {
            if (StringUtils.isNotEmpty(roleCode)) {
                return ResultVO.success(roleService.selectByRoleCode(roleCode));
            }
            return ResultVO.error(StatusCode.JOBMONITOR_DATA_EMPTY);
        } catch (Exception e) {
            log.error("get role error", e);
            return ResultVO.error();
        }
    }

    @ApiOperation(value = "根据权限名称获取授权信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleName", paramType = "path", required = true, dataType = "string", value = "权限名称")
    })
    @RequestMapping(value = {"get/name/{roleName}"}, method = RequestMethod.GET)
    ResultVO getRoleByName(@PathVariable String roleName) {
        try {
            if (StringUtils.isNotEmpty(roleName)) {
                return ResultVO.success(roleService.selectByRoleName(roleName));
            }
            return ResultVO.error(StatusCode.JOBMONITOR_DATA_EMPTY);
        } catch (Exception e) {
            log.error("get role error", e);
            return ResultVO.error();
        }
    }

    @ApiOperation(value = "根据权限id取授权信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", paramType = "path", required = true, dataType = "string", value = "权限id")
    })
    @RequestMapping(value = {"get/id/{roleId}"}, method = RequestMethod.GET)
    ResultVO getRoleById(@PathVariable String roleId) {
        try {
            if (StringUtils.isNotEmpty(roleId)) {
                return ResultVO.success(roleService.selectByPrimaryKey(roleId));
            }
            return ResultVO.error(StatusCode.JOBMONITOR_DATA_EMPTY);
        } catch (Exception e) {
            log.error("get role error", e);
            return ResultVO.error();
        }
    }

}
