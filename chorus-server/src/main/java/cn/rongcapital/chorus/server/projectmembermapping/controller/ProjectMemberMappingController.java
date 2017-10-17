package cn.rongcapital.chorus.server.projectmembermapping.controller;

import cn.rongcapital.chorus.authorization.api.UserDataAuthorization;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationData;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataDW;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataFS;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataType;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationPerm;
import cn.rongcapital.chorus.authorization.plugin.ranger.RangerUtils;
import cn.rongcapital.chorus.common.constant.ProjectRoleEnum;
import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.entity.*;
import cn.rongcapital.chorus.das.entity.web.ProjectCause;
import cn.rongcapital.chorus.das.service.AuthorizationDetailService;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.das.service.ProjectMemberMappingService;
import cn.rongcapital.chorus.das.service.TRoleService;
import cn.rongcapital.chorus.server.project.controller.HDFSAuthAssembler;
import cn.rongcapital.chorus.server.project.controller.ProjectInfoController;
import cn.rongcapital.chorus.server.projectmembermapping.controller.vo.ProjectMemberVo;
import cn.rongcapital.chorus.server.vo.ResultVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by shicheng on 2016/11/25.
 */
@RestController
@RequestMapping(value = { "/project/member" })
@Api(value = "项目成员 API")
@Slf4j
public class ProjectMemberMappingController {

    private ProjectInfoService projectInfoService;

    private ProjectMemberMappingService projectMemberMappingService;

    private AuthorizationDetailService authorizationDetailService;

    private TRoleService roleService = null;

    @Resource(name = "TRoleService")
    public void setService(TRoleService service) {
        this.roleService = service;
    }

    @Resource(name = "ProjectMemberMappingService")
    public void setService(ProjectMemberMappingService service) {
        this.projectMemberMappingService = service;
    }

    @Resource(name = "ProjectInfoService")
    public void setService(ProjectInfoService service) {
        this.projectInfoService = service;
    }

    @Resource(name = "AuthorizationDetailService")
    public void setService(AuthorizationDetailService service) {
        this.authorizationDetailService = service;
    }

    @Autowired
    @Qualifier(value = "userDataAuthorizationByRanger")
    private UserDataAuthorization authorization;

    @Autowired
    @Qualifier(value = "userDataAuthRangerHDFS")
    private UserDataAuthorization userDataAuthRangerHDFS;

    @ApiOperation(value = "添加项目成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", paramType = "body", dataType = "ProjectMemberVo", required = true, value = "成员信息") })
    @RequestMapping(value = { "new" }, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    ResultVO insert(@RequestBody ProjectMemberVo data) {
        try {
            int count = projectMemberMappingService.insert(parse(data, true));
            if (count > 0) {
                // 获取项目信息
                ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(data.getProjectId());

                // 生成权限名称
                String policyName = policyNameFormat(projectInfo.getProjectCode(), projectInfo.getCreateUserId());

                // 创建权限实例对象
                AuthorizationDataDW authorizationDataDW = new AuthorizationDataDW();
                authorizationDataDW.setAuthorizationRepositoryType(AuthorizationDataType.DW);
                authorizationDataDW.setEnabled(true);
                List databases = new ArrayList();
                databases.add("chorus_" + projectInfo.getProjectCode());
                authorizationDataDW.setDatabases(databases);
                authorizationDataDW = ProjectInfoController.getDataDW(policyName, authorizationDataDW, data.getRoleId(),
                        data.getUserName());

                // 搜索原有授权 TODO: 防止原有授权包含当前用户, 增加 each 删除用户功能
                AuthorizationDataDW addw = (AuthorizationDataDW) authorization.authorizationSearch(authorizationDataDW);

                if (addw == null) {
                    // policy 在ranger 中不存在, 新添加授权信息
                    // 增加权限到Ranger
                    AuthorizationData authorizationData = addAuthorization(projectInfo, data, authorizationDataDW);
                    // 插入ranger policy 信息到chorus db
                    insertAuthorizationDetail(projectInfo, data, authorizationData);
                } else {
                    // policy 在ranger 中存在, 更新授权信息
                    AuthorizationData authorizationData = updateDWAuthorization(data, addw);
                    // 插入ranger policy 信息到chorus db
                    insertAuthorizationDetail(projectInfo, data, authorizationData);
                }

                updateHDFSAuth(projectInfo, data.getRoleId(), data.getUserName(), data.getUserId());
                return ResultVO.success();
            }
            return ResultVO.error();
        } catch (Exception e) {
            log.error("add error", e);
            return ResultVO.error();
        }
    }

    /**
     * @param projectInfo
     * @param roleId
     * @param userName
     * @author yunzhong
     * @throws Exception
     * @time 2017年8月24日下午5:45:11
     */
    private void updateHDFSAuth(ProjectInfo project, String roleId, String userName, String userId) throws Exception {
        ProjectRoleEnum role = ProjectRoleEnum.getByCode(roleId);
        List<String> userNames = new ArrayList<>();
        userNames.add(userName);
        AuthorizationDataFS authData = HDFSAuthAssembler.assembleHDFSPath(project, role, userNames);
        if (authData != null) {
            final AuthorizationData authorizationSearch = userDataAuthRangerHDFS.authorizationSearch(authData);
            final AuthorizationData authTemp;
            if (authorizationSearch == null) {
                authTemp = userDataAuthRangerHDFS.authorizationAdd(authData);
            } else {
                authTemp = HDFSAuthAssembler.appendAuthorization(authData, authorizationSearch);
                userDataAuthRangerHDFS.authorizationUpdate(authTemp);
            }
            AuthorizationDetail detail = HDFSAuthAssembler.assembleDetail(authTemp.getAuthorizationId(),
                    authTemp.getAuthorizationName(), project.getProjectId(), userId);
            authorizationDetailService.insertOrUpdate(detail);
        }
    }

    @ApiOperation(value = "更新项目成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", paramType = "body", dataType = "ProjectMemberVo", required = true, value = "成员信息") })
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "update" }, method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    ResultVO update(@RequestBody ProjectMemberVo data) {
        try {
            ProjectMemberMapping mapping = new ProjectMemberMapping();
            mapping.setProjectId(data.getProjectId());
            mapping.setRoleId(data.getRoleId());
            mapping.setUserId(data.getUserId());
            // 更新原有关系
            int count = projectMemberMappingService.updateByProjectAndUserId(mapping);
            if (count > 0) {// 更新权限信息
                ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(data.getProjectId());
                String policyName = policyNameFormat(projectInfo.getProjectCode(), projectInfo.getCreateUserId());

                // AuthorizationDataDW currentUserAuthorizationData =
                // ProjectInfoController.getDataDW(policyName, // 当前用户需要申请的权限归属
                // new AuthorizationDataDW(), data.getRoleId(),
                // data.getUserName());

                // 搜索原有授权
                AuthorizationDataDW currentUserAuthorizationData = new AuthorizationDataDW();
                currentUserAuthorizationData.setAuthorizationRepositoryType(AuthorizationDataType.DW);
                currentUserAuthorizationData.setEnabled(true);
                List databases = new ArrayList();
                databases.add("chorus_" + projectInfo.getProjectCode());
                currentUserAuthorizationData.setDatabases(databases);
                currentUserAuthorizationData = ProjectInfoController.getDataDW(policyName, currentUserAuthorizationData,
                        data.getRoleId(), data.getUserName());

                // 清除该用户之前在该项目下被授予的权限
                List<AuthorizationDetail> details = authorizationDetailService
                        .selectByUserIdAndProjectId(data.getUserId(), String.valueOf(data.getProjectId())); // 从数据库获取存储的权限信息
                final String hdfsPolicyName = RangerUtils.generateHDFSPolicyName(projectInfo.getProjectCode());
                for (AuthorizationDetail detail : details) {
                    if (isColumnPolicy(detail.getPolicyName()))
                        continue;
                    if (hdfsPolicyName.equals(detail.getPolicyName())) {
                        continue;
                    }
                    AuthorizationDataDW currentAuthorizationData = new AuthorizationDataDW(); // 查询介质
                    currentAuthorizationData.setAuthorizationId(detail.getPolicyId());
                    currentAuthorizationData.setAuthorizationName(detail.getPolicyName());
                    // query from ranger by policy id
                    AuthorizationDataDW oldAuthorizationData = (AuthorizationDataDW) authorization
                            .authorizationSearch(currentAuthorizationData); // 当前权限介质

                    if (oldAuthorizationData == null) {
                        log.error("policy id:{} not find in ranger.", currentAuthorizationData.getAuthorizationId());
                        authorizationDetailService.deleteByPrimaryKey(detail.getId());
                        log.info("remove record[{}] in table authorization_detail.", detail.getId());
                        continue;
                    }

                    AuthorizationDataDW updateADDW = new AuthorizationDataDW(); // 存储权限介质
                    updateADDW.setEnabled(true);
                    updateADDW.setAuthorizationId(oldAuthorizationData.getAuthorizationId());
                    updateADDW.setDatabases(oldAuthorizationData.getDatabases());
                    updateADDW.setAuthorizationName(oldAuthorizationData.getAuthorizationName());
                    updateADDW.setTables(oldAuthorizationData.getTables());
                    updateADDW.setColumns(oldAuthorizationData.getColumns());

                    List<AuthorizationPerm> currentPerms = oldAuthorizationData.getAuthorizationPermMapList(); // 原有权限介质
                    List<AuthorizationPerm> updatePerms = new ArrayList<>(); // 存储权限介质

                    AuthorizationPerm updatePerm = new AuthorizationPerm();
                    List currentUserList = currentPerms.get(0).getPermUserList();
                    List updateUserList = new ArrayList();
                    currentUserList.forEach(user -> updateUserList.add(user));
                    if (updateUserList.contains(data.getUserName())) { // 删除当前申请权限的用户
                        updateUserList.remove(data.getUserName());
                    }

                    updatePerm.setPermUserList(updateUserList);
                    updatePerm.setPermPermList(currentPerms.get(0).getPermPermList());
                    updatePerm.setPermGroupList(currentPerms.get(0).getPermGroupList());
                    updatePerms.add(updatePerm);
                    updateADDW.setAuthorizationPermMapList(updatePerms);
                    boolean success = true;
                    if (updateUserList.size() > 0) { // 权限中含有授权的用户
                        success = authorization.authorizationUpdate(updateADDW);
                    } else { // 权限中未含有授权的用户, 删除该条权限
                        success = authorization.authorizationRemove(updateADDW);
                    }

                    if (success)
                        authorizationDetailService.deleteByPrimaryKey(detail.getId());
                }

                // 插入或更新
                AuthorizationDataDW addw = (AuthorizationDataDW) authorization
                        .authorizationSearch(currentUserAuthorizationData);
                if (addw == null) {// 插入新的权限到Ranger 和 authorization detail.
                    AuthorizationData authorizationData = addAuthorization(projectInfo, data,
                            currentUserAuthorizationData);
                    insertAuthorizationDetail(projectInfo, data, authorizationData);
                } else {
                    AuthorizationDataDW updateAuthorization = new AuthorizationDataDW();
                    updateAuthorization.setEnabled(true);
                    updateAuthorization.setAuthorizationId(addw.getAuthorizationId());
                    updateAuthorization.setDatabases(addw.getDatabases());
                    updateAuthorization.setAuthorizationName(addw.getAuthorizationName());
                    updateAuthorization.setTables(addw.getTables());
                    updateAuthorization.setColumns(addw.getColumns());

                    List<AuthorizationPerm> authorizationPermMapList = addw.getAuthorizationPermMapList();
                    AuthorizationPerm oldPerm = authorizationPermMapList.get(0);
                    List<AuthorizationPerm> newPermMapList = new ArrayList<>();
                    List<String> userList = new ArrayList<>();
                    userList.addAll(oldPerm.getPermUserList());
                    userList.add(data.getUserName());
                    AuthorizationPerm perm = new AuthorizationPerm();
                    perm.setPermUserList(userList);
                    perm.setPermGroupList(oldPerm.getPermGroupList());
                    perm.setPermPermList(oldPerm.getPermPermList());
                    newPermMapList.add(perm);
                    updateAuthorization.setAuthorizationPermMapList(newPermMapList);
                    authorization.authorizationUpdate(updateAuthorization);
                    insertAuthorizationDetail(projectInfo, data, updateAuthorization);
                }
                updateHDFSAuth(projectInfo, data.getRoleId(), data.getUserName(), data.getUserId());
                return ResultVO.success();
            }
            return ResultVO.error();
        } catch (Exception e) {
            log.error("add error", e);
            return ResultVO.error();
        }
    }

    @ApiOperation(value = "根据记录 id 删除记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", paramType = "path", dataType = "string", required = true, value = "项目 id"),
            @ApiImplicitParam(name = "userId", paramType = "path", dataType = "string", required = true, value = "用户 id"),
            @ApiImplicitParam(name = "userName", paramType = "path", dataType = "string", required = true, value = "用户名"), })
    @RequestMapping(value = { "del/{projectId}/{userId}/{userName}" }, method = RequestMethod.DELETE)
    ResultVO delById(@PathVariable Long projectId, @PathVariable Long userId, @PathVariable String userName) {
        try {
            int count = projectMemberMappingService.deleteByProjectAndUserId(projectId, userId);
            final ProjectInfo project = projectInfoService.selectByPrimaryKey(projectId);
            if (project == null) {
                log.warn("Failed to get project {}", projectId);
                return ResultVO.error(StatusCode.PROJECT_NOT_EXISTS);
            }
            String hdfsPolicyName = RangerUtils.generateHDFSPolicyName(project.getProjectCode());
            {
                List<AuthorizationDetail> details = authorizationDetailService
                        .selectByUserIdAndProjectId(String.valueOf(userId), String.valueOf(projectId)); // 数据库获取存储的权限信息

                for (AuthorizationDetail detail : details) {
                    if (isColumnPolicy(detail.getPolicyName()))
                        continue;
                    if (detail.getPolicyName().equals(hdfsPolicyName)) {
                        deleteHDFSAuth(project, userName, String.valueOf(userId));
                    } else {
                        AuthorizationDataDW authorizationDataDW = new AuthorizationDataDW(); // 查询介质
                        authorizationDataDW.setAuthorizationId(detail.getPolicyId());

                        AuthorizationDataDW currentADDW = (AuthorizationDataDW) authorization
                                .authorizationGet(authorizationDataDW); // 当前权限介质
                        AuthorizationDataDW updateADDW = new AuthorizationDataDW(); // 存储权限介质

                        updateADDW.setEnabled(true);
                        updateADDW.setAuthorizationId(currentADDW.getAuthorizationId());
                        updateADDW.setDatabases(currentADDW.getDatabases());
                        updateADDW.setAuthorizationName(currentADDW.getAuthorizationName());
                        updateADDW.setTables(currentADDW.getTables());
                        updateADDW.setColumns(currentADDW.getColumns());

                        List<AuthorizationPerm> currentPerms = currentADDW.getAuthorizationPermMapList(); // 原有权限介质
                        List<AuthorizationPerm> updatePerms = new ArrayList<>(); // 存储权限介质

                        AuthorizationPerm updatePerm = new AuthorizationPerm();

                        List currentUserList = currentPerms.get(0).getPermUserList();
                        List updateUserList = new ArrayList();
                        currentUserList.forEach(user -> updateUserList.add(user));
                        if (updateUserList.contains(userName)) { // 删除当前申请权限的用户
                            updateUserList.remove(userName);
                        }

                        updatePerm.setPermUserList(updateUserList);
                        updatePerm.setPermPermList(currentPerms.get(0).getPermPermList());
                        updatePerm.setPermGroupList(currentPerms.get(0).getPermGroupList());
                        updatePerms.add(updatePerm);
                        updateADDW.setAuthorizationPermMapList(updatePerms);

                        if (updateUserList.size() > 0) { // 权限中含有授权的用户
                            authorization.authorizationUpdate(updateADDW);
                        } else { // 权限中未含有授权的用户, 删除该条权限
                            authorization.authorizationRemove(updateADDW);
                        }
                    }
                    authorizationDetailService.deleteByPrimaryKey(detail.getId());
                }
            }
            return ResultVO.success(count);
        } catch (Exception e) {
            log.error("delete error", e);
            return ResultVO.error();
        }
    }

    /**
     * @param project
     * @param userName
     * @param userId
     * @throws Exception
     * @author yunzhong
     * @time 2017年8月25日上午9:39:41
     */
    private void deleteHDFSAuth(ProjectInfo project, String userName, String userId) throws Exception {
        List<String> userNames = new ArrayList<>();
        userNames.add(userName);
        AuthorizationDataFS authData = HDFSAuthAssembler.assembleHDFSPath(project, null, userNames);
        final AuthorizationData authorizationSearch = userDataAuthRangerHDFS.authorizationSearch(authData);
        if (authorizationSearch != null) {
            HDFSAuthAssembler.deleteAuthorization(userName, authorizationSearch);
            if (CollectionUtils.isNotEmpty(authorizationSearch.getAuthorizationPermMapList())) {
                userDataAuthRangerHDFS.authorizationUpdate(authorizationSearch);
            } else {
                userDataAuthRangerHDFS.authorizationRemove(authorizationSearch);
            }
        }
    }

    @ApiOperation(value = "根据项目 ID 获取成员")
    @RequestMapping(value = { "get/member/{projectId}" }, method = RequestMethod.GET)
    ResultVO getMemberByProjectId(@PathVariable Long projectId) {
        List<ProjectInfoDO> data = projectMemberMappingService.selectMemberByProjectId(projectId);
        return ResultVO.success(data);
    }

    @ApiOperation(value = "根据项目 ID 和角色 id 获取成员")
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "get/member/pr" }, method = RequestMethod.GET)
    ResultVO getMemberByProjectIdAndRoleId(@RequestBody ProjectCause cause) {
        List<ProjectInfoDO> data = projectMemberMappingService.selectMemberByProjectIdAndRole(cause.getProjectId(),
                cause.getRoleId());
        return ResultVO.success(data);
    }

    private ProjectMemberMapping parse(ProjectMemberVo vo, Boolean flag) {
        ProjectMemberMapping mapping = new ProjectMemberMapping();
        mapping.setRoleId(vo.getRoleId());
        mapping.setProjectId(vo.getProjectId());
        mapping.setUserId(vo.getUserId());
        if (!flag) { // 更新
            mapping.setProjectMemberId(vo.getProjectMemberId());
        }
        return mapping;
    }

    private String policyNameFormat(String projectCode, String userId) {
        return String.format("chorus_%s_%s", projectCode, userId);
    }

    private AuthorizationData addAuthorization(ProjectInfo projectInfo, ProjectMemberVo data,
            AuthorizationDataDW authorizationDataDW) throws Exception {
        AuthorizationData ad = authorization.authorizationAdd(authorizationDataDW);
        return ad;
    }

    private AuthorizationData updateDWAuthorization(ProjectMemberVo data, AuthorizationDataDW authorizationDataDW)
            throws Exception {
        AuthorizationDataDW uaddw = new AuthorizationDataDW();
        uaddw.setEnabled(true);
        uaddw.setAuthorizationId(authorizationDataDW.getAuthorizationId());
        uaddw.setDatabases(authorizationDataDW.getDatabases());
        uaddw.setAuthorizationName(authorizationDataDW.getAuthorizationName());
        uaddw.setTables(authorizationDataDW.getTables());
        uaddw.setColumns(authorizationDataDW.getColumns());
        List<AuthorizationPerm> perms = authorizationDataDW.getAuthorizationPermMapList();
        List<AuthorizationPerm> addPerms = new ArrayList<>();
        AuthorizationPerm perm = new AuthorizationPerm();
        List users = new ArrayList();
        List userDatas = perms.get(0).getPermUserList();
        userDatas.forEach(value -> users.add(value));
        if (!userDatas.contains(data.getUserName())) {
            users.add(data.getUserName());
        }
        perm.setPermUserList(users);
        perm.setPermGroupList(perms.get(0).getPermGroupList());
        perm.setPermPermList(perms.get(0).getPermPermList());
        addPerms.add(perm);
        uaddw.setAuthorizationPermMapList(addPerms);
        authorization.authorizationUpdate(uaddw);

        return uaddw;
    }

    private int insertAuthorizationDetail(ProjectInfo projectInfo, ProjectMemberVo data,
            AuthorizationData authorizationData) {
        if (authorizationData == null)
            return 0;

        AuthorizationDetail detail = new AuthorizationDetail();
        detail.setCreateTime(new Date());
        detail.setPolicyId(authorizationData.getAuthorizationId());
        detail.setPolicyName(authorizationData.getAuthorizationName());
        detail.setProjectId(projectInfo.getProjectId());
        detail.setUserId(data.getUserId());
        detail.setCategory(AuthorizationDetailCategory.HIVE.name());
        detail.setEnabled(1);
        return authorizationDetailService.insertOrUpdate(detail);
    }

    /**
     * @param projectId
     * @return
     * @author yunzhong
     * @time 2017年6月16日下午4:41:25
     */
    @ApiOperation(value = "根据项目 ID 获得各个角色的人数统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", paramType = "path", dataType = "Long", required = true, value = "项目id") })
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "statistics/{projectId}" }, method = RequestMethod.GET)
    public ResultVO<ProjectMemberCount> getMemberByProjectIdAndRoleId(@PathVariable Long projectId) {
        ProjectMemberCount data = projectMemberMappingService.statProjectMember(projectId);
        return ResultVO.success(data);
    }

    private boolean isColumnPolicy(String policyName) {
        if (StringUtils.isEmpty(policyName))
            return false;

        String[] item = policyName.split("_");
        String flag = item[item.length - 1];
        return !("query".equalsIgnoreCase(flag) || "develop".equalsIgnoreCase(flag) || "admin".equalsIgnoreCase(flag)
                || "HDFS".equalsIgnoreCase(flag));
    }
}
