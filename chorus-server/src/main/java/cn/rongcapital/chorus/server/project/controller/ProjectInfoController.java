package cn.rongcapital.chorus.server.project.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

import cn.rongcapital.chorus.datalab.service.LabService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;

import cn.rongcapital.chorus.authorization.api.UserDataAuthorization;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationData;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataDW;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataFS;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataType;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationPerm;
import cn.rongcapital.chorus.authorization.api.data.DWPermissions;
import cn.rongcapital.chorus.common.constant.JobStatus;
import cn.rongcapital.chorus.common.constant.ProjectRoleEnum;
import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.hadoop.HadoopClient;
import cn.rongcapital.chorus.common.hadoop.HadoopUtil;
import cn.rongcapital.chorus.das.entity.AuthorizationDetail;
import cn.rongcapital.chorus.das.entity.AuthorizationDetailCategory;
import cn.rongcapital.chorus.das.entity.InstanceInfoWithHostsDO;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.TRole;
import cn.rongcapital.chorus.das.entity.TUser;
import cn.rongcapital.chorus.das.entity.caas.CaasUser;
import cn.rongcapital.chorus.das.entity.*;
import cn.rongcapital.chorus.das.entity.web.JobCause;
import cn.rongcapital.chorus.das.entity.web.ProjectCause;
import cn.rongcapital.chorus.das.service.AuthorizationDetailService;
import cn.rongcapital.chorus.das.service.CaasClient;
import cn.rongcapital.chorus.das.service.InstanceInfoService;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.das.service.ProjectMemberMappingService;
import cn.rongcapital.chorus.das.service.QueueService;
import cn.rongcapital.chorus.das.service.TRoleService;
import cn.rongcapital.chorus.das.service.TUserService;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import cn.rongcapital.chorus.das.service.*;
import cn.rongcapital.chorus.datalab.service.LabStatusService;
import cn.rongcapital.chorus.resourcemanager.service.ExecutionUnitService;
import cn.rongcapital.chorus.server.project.controller.vo.ProjectInfoVo;
import cn.rongcapital.chorus.server.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * 项目操作 API
 *
 * @author shicheng
 * @version 1.0
 * @since
 *
 *        <pre>
 * 十一月 23, 2016
 *        </pre>
 */
@RestController
@RequestMapping(value = { "/projectinfo" })
@Api(value = "项目操作 API")
@Slf4j
public class ProjectInfoController {

    private ProjectInfoService projectInfoService = null;

    private AuthorizationDetailService authorizationDetailService = null;
    @Autowired
    private ExecutionUnitService executionUnitService;
    @Autowired
    private InstanceInfoService instanceInfoService;
    @Autowired
    private QueueService queueService;

    @Autowired
    private HadoopClient hadoopClient;

    @Autowired
    private ProjectMemberMappingService memberService;

    @Autowired
    private CaasClient caasClient;

    @Autowired
    private TUserService userService;
    @Autowired
    private TRoleService roleService;

    @Resource(name = "ProjectInfoService")
    public void setService(ProjectInfoService service) {
        this.projectInfoService = service;
    }

    @Resource(name = "AuthorizationDetailService")
    public void setService(AuthorizationDetailService service) {
        this.authorizationDetailService = service;
    }

    @Autowired
    @Qualifier(value = "userDataAuthRangerHDFS")
    private UserDataAuthorization userDataAuthRangerHDFS;

    @Autowired
    @Qualifier(value = "userDataAuthorizationByRanger")
    private UserDataAuthorization authorization;
    @Autowired
    private TableInfoServiceV2 tableInfoServiceV2;
    @Autowired
    private LabStatusService labStatusService;
    @Autowired
    private LabService labService;
    @Autowired
    private DatalabInfoService datalabInfoService;
    @Autowired
    private JobService jobService;

    @RequestMapping(value = "/{projectId}")
    public ResultVO<List<String>> tablesName(@PathVariable @Nonnull Long projectId) {
        try {
            return ResultVO.success(tableInfoServiceV2.listAllTableName(projectId));
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            return ResultVO.error(new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @ApiOperation(value = "根据用户获取该用户所有的项目信息列表")
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "get/u/{userId}/{pageNum}/{pageSize}" }, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    ResultVO<PageInfo> getAllProjectByUserId(@PathVariable String userId, @PathVariable int pageNum,
            @PathVariable int pageSize) {
        try {
            PageInfo page = new PageInfo(projectInfoService.selectAllProjectByUserId(userId, pageNum, pageSize));
            return ResultVO.success(page);
        } catch (Exception e) {
            log.error("get data error", e);
            return ResultVO.error();
        }
    }

    @ApiOperation(value = "根据项目名称获得匹配的项目信息列表")
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "get/u/{pageNum}/{pageSize}" }, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    ResultVO<PageInfo> getAllProjectsWithName(@PathVariable int pageNum, @PathVariable int pageSize,
            @RequestBody ProjectCause cause) {
        try {
            PageInfo page = new PageInfo(projectInfoService.selectProjectInfoByCondition(cause.getProjectName(), null,
                    null, true, pageNum, pageSize));
            return ResultVO.success(page);
        } catch (Exception e) {
            log.error("get data error", e);
            return ResultVO.error();
        }
    }

    @RequestMapping(value = { "get/u" }, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    ResultVO getAllProjectByUserId(@RequestBody ProjectCause cause) {
        List<ProjectInfoDO> list = projectInfoService.selectAllProjectByUserId(cause.getUserId());
        return ResultVO.success(list);
    }

    @ApiOperation(value = "添加项目信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", paramType = "body", dataType = "ProjectInfoVo", required = true, value = "项目信息") })
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "new" }, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    ResultVO insert(@RequestBody ProjectInfoVo data) {
        try {
            if (projectInfoService.validateProjectExists(data.getProjectCode())) {
                return ResultVO.error(StatusCode.PROJECT_EXISTS);
            }
            ProjectInfo record = projectInfoService.insertAndGetKey(parse(data, true));
            if (record != null) {
                addHiveAuth(record);
                updateHDFSProjectAuth(record);
                return ResultVO.success(record);
            }
        } catch (Exception e) {
            log.error("add project error", e);
            return ResultVO.error();
        }
        return ResultVO.success(Boolean.FALSE);
    }

    /**
     * @param record
     * @throws Exception
     * @author yunzhong
     * @time 2017年8月23日下午8:49:39
     */
    private void addHiveAuth(ProjectInfo record) throws Exception {
        AuthorizationDataDW authorizationDataDW = new AuthorizationDataDW();
        List databases = new ArrayList();
        databases.add("chorus_" + record.getProjectCode());
        authorizationDataDW.setDatabases(databases);
        List tables = new ArrayList();
        tables.add("*");
        authorizationDataDW.setTables(tables);
        List columns = new ArrayList();
        columns.add("*");
        authorizationDataDW.setColumns(columns);
        authorizationDataDW.setAuthorizationRepositoryType(AuthorizationDataType.DW);
        authorizationDataDW.setEnabled(true);
        String policy = "chorus_" + record.getProjectCode() + "_" + record.getCreateUserId();
        AuthorizationData authorizationData = getDataDW(policy, authorizationDataDW, ProjectRoleEnum.OWNER.code, record.getUserName());
        AuthorizationData oldAuthorizationData = authorization.authorizationSearch(authorizationData);
        if(oldAuthorizationData == null) {
            authorizationData = authorization.authorizationAdd(authorizationData);
        }else {
            authorizationData.setAuthorizationId(oldAuthorizationData.getAuthorizationId());
            authorization.authorizationUpdate(authorizationData);
        }
        AuthorizationDetail detail = new AuthorizationDetail();
        detail.setCreateTime(new Date());
        detail.setPolicyId(authorizationData.getAuthorizationId());
        detail.setPolicyName(authorizationData.getAuthorizationName());
        detail.setProjectId(record.getProjectId());
        detail.setUserId(record.getCreateUserId());
        detail.setCategory(AuthorizationDetailCategory.HIVE.name());
        detail.setEnabled(1);
        authorizationDetailService.insert(detail);
    }

    /**
     * @return
     * @author yunzhong
     * @time 2017年8月24日上午10:26:21
     */
    @ApiOperation(value = "添加项目信息")
    @RequestMapping(value = "refresh/auth", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResultVO<Object> refreshHDFSAuth(@RequestParam(value = "projectId", required = false) Long projectId) {
        List<ProjectInfo> projects = null;
        if (projectId != null) {
            final ProjectInfo project = projectInfoService.selectByPrimaryKey(projectId);
            projects = new ArrayList<>();
            if (project != null) {
                projects.add(project);
            } else {
                return ResultVO.error(StatusCode.PROJECT_NOT_EXISTS);
            }
        } else {
            projects = projectInfoService.queryAllActive();
        }
        for (ProjectInfo project : projects) {
            try {
                cacheProjectMembers(project);
                String path = HadoopUtil.appendPath(HadoopUtil.formatPath(project.getProjectCode()), "hdfs");
                if (hadoopClient.mkdir(path, project.getUserName())) {
                    updateHDFSProjectAuth(project);
                    updateHDFSPathAuth(project);
                }
            } catch (Exception e) {
                log.error("Failed to reauth [" + project.getProjectCode() + "]", e);
            }
        }
        return ResultVO.success();
    }

    /**
     * @param project
     * @author yunzhong
     * @time 2017年8月24日下午2:55:12
     */
    private void cacheProjectMembers(ProjectInfo project) {
        try {
            final List<TRole> roles = roleService.selectAll();
            List<CaasUser> projectUsers = new ArrayList<>();
            roles.forEach(role -> {
                final List<ProjectInfoDO> members = memberService.selectMembers(project.getProjectId(),
                        role.getRoleId());
                boolean needCache = false;
                if (members != null) {
                    for (ProjectInfoDO member : members) {
                        if (StringUtils.isEmpty(member.getUserName()) || StringUtils.isEmpty(member.getEmail())) {
                            needCache = true;
                            break;
                        }
                    }
                }
                if (needCache) {
                    List<CaasUser> tempUsers = null;
                    try {
                        tempUsers = caasClient.getProjectUsers(project.getProjectCode(), role.getRoleName());
                    } catch (Exception e) {
                        log.warn("Failed to get project [" + project.getProjectCode() + "] role [" + role.getRoleName()
                                + "] user list.", e);
                    }
                    if (CollectionUtils.isNotEmpty(tempUsers)) {
                        projectUsers.addAll(tempUsers);
                    }
                }
            });
            if (CollectionUtils.isNotEmpty(projectUsers)) {
                List<TUser> users = new ArrayList<>();
                projectUsers.forEach(projectUser -> {
                    TUser user = new TUser();
                    user.setEmail(projectUser.getEmail());
                    user.setUserId(projectUser.getCode());
                    user.setUserName(projectUser.getName());
                    users.add(user);
                });
                userService.replaceBatch(users);
            }
        } catch (Exception e) {
            log.error("Failed to cache project [" + project.getProjectId() + "] user list.", e);
        }
    }

    private void updateHDFSPathAuth(ProjectInfo project) throws Exception {
        final List<ProjectInfoDO> managers = memberService.selectMembers(project.getProjectId(),
                ProjectRoleEnum.ADMIN.code);
        final List<ProjectInfoDO> developers = memberService.selectMembers(project.getProjectId(),
                ProjectRoleEnum.DEV.code);
        final List<ProjectInfoDO> queryers = memberService.selectMembers(project.getProjectId(),
                ProjectRoleEnum.QUERY.code);
        List<String> alluserId = new ArrayList<>();
        List<String> wrxAuthUsers = new ArrayList<>();
        List<String> rxAuthUsers = new ArrayList<>();
        if (!CollectionUtils.isEmpty(managers))
            managers.forEach(manager -> {
                if (StringUtils.hasLength(manager.getUserName())) {
                    alluserId.add(manager.getUserId());
                    wrxAuthUsers.add(manager.getUserName());
                }
            });
        if (!CollectionUtils.isEmpty(developers))
            developers.forEach(develop -> {
                if (StringUtils.hasLength(develop.getUserName())) {
                    alluserId.add(develop.getUserId());
                    wrxAuthUsers.add(develop.getUserName());
                }
            });
        if (!CollectionUtils.isEmpty(queryers))
            queryers.forEach(queryer -> {
                if (StringUtils.hasLength(queryer.getUserName())) {
                    alluserId.add(queryer.getUserId());
                    rxAuthUsers.add(queryer.getUserName());
                }
            });
        AuthorizationDataFS authData = null;
        if (CollectionUtils.isNotEmpty(wrxAuthUsers)) {
            authData = HDFSAuthAssembler.assembleHDFSPath(project, ProjectRoleEnum.ADMIN, wrxAuthUsers);
            if (CollectionUtils.isNotEmpty(rxAuthUsers)) {
                HDFSAuthAssembler.assembleHDFSPath(authData, ProjectRoleEnum.QUERY, rxAuthUsers);
            }
        } else {
            if (CollectionUtils.isNotEmpty(rxAuthUsers)) {
                authData = HDFSAuthAssembler.assembleHDFSPath(project, ProjectRoleEnum.QUERY, rxAuthUsers);
            }
        }
        if (authData != null) {
            final AuthorizationData authorizationSearch = userDataAuthRangerHDFS.authorizationSearch(authData);
            final AuthorizationData authTemp;
            if (authorizationSearch == null) {
                authTemp = userDataAuthRangerHDFS.authorizationAdd(authData);
            } else {
                authTemp = HDFSAuthAssembler.updateAuthorization(authData, authorizationSearch);
                userDataAuthRangerHDFS.authorizationUpdate(authTemp);
            }
            alluserId.forEach(userId -> {
                AuthorizationDetail detail = HDFSAuthAssembler.assembleDetail(authTemp.getAuthorizationId(),
                        authTemp.getAuthorizationName(), project.getProjectId(), userId);
                authorizationDetailService.insertOrUpdate(detail);
            });
        }

    }

    /**
     * 项目owner，项目根目录的所有权限
     *
     * @param project
     * @throws Exception
     * @author yunzhong
     * @time 2017年8月23日下午4:14:21
     */
    private void updateHDFSProjectAuth(ProjectInfo project) throws Exception {
        AuthorizationDataFS authData = HDFSAuthAssembler.assembleProjectPath(project);
        final AuthorizationData authorizationSearch = userDataAuthRangerHDFS.authorizationSearch(authData);
        AuthorizationData authorizationData = null;
        if (authorizationSearch == null) {
            authorizationData = userDataAuthRangerHDFS.authorizationAdd(authData);
        } else {
            authorizationData = HDFSAuthAssembler.updateAuthorization(authData, authorizationSearch);
            userDataAuthRangerHDFS.authorizationUpdate(authorizationData);
        }
        AuthorizationDetail detail = HDFSAuthAssembler.assembleDetail(authorizationData, project);
        authorizationDetailService.insertOrUpdate(detail);
    }

    @ApiOperation(value = "更新项目信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", paramType = "body", dataType = "ProjectInfoVo", required = true, value = "项目信息") })
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "update" }, method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    ResultVO updateProject(@RequestBody ProjectInfoVo data) {
        try {
            Integer count = projectInfoService.updateByPrimaryKey(parse(data, false));
            if (count > 0) {
                return ResultVO.success(count);
            }
        } catch (Exception e) {
            log.error("update project error", e);
            return ResultVO.error();
        }
        return ResultVO.error();
    }

    @ApiOperation(value = "根据项目 id 删除项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", paramType = "path", dataType = "string", required = true, value = "项目 id"), })
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "del/p/{projectId}" }, method = RequestMethod.DELETE)
    ResultVO delByProjectId(@PathVariable Long projectId) {
        try {
            int count = projectInfoService.deleteByPrimaryKey(projectId);
            if (count > 0) {
                return ResultVO.success(Boolean.TRUE);
            }
        } catch (Exception e) {
            log.error("delete project error", e);
            return ResultVO.error();
        }
        return ResultVO.success(Boolean.FALSE);
    }

    @ApiOperation(value = "根据项目 id 查找项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", paramType = "path", dataType = "string", required = true, value = "项目 id"), })
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "get/{projectId}" }, method = RequestMethod.GET)
    ResultVO<ProjectInfo> getProjectById(@PathVariable Long projectId) {
        try {
            if (projectId != null) {
                ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(projectId);
                return ResultVO.success(projectInfo);
            }
        } catch (Exception e) {
            log.error("get data error", e);
            return ResultVO.error();
        }
        return ResultVO.error(StatusCode.PARAM_ERROR);
    }

    @ApiOperation(value = "验证项目存在性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", paramType = "path", dataType = "string", required = true, value = "项目信息: ID 或 项目名称 或 项目编码"), })
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "validata/{data}" }, method = RequestMethod.GET)
    ResultVO validateProject(@PathVariable String data) {
        try {
            if (data != null) {
                if (projectInfoService.validateProjectExists(data)) {
                    return ResultVO.success(Boolean.TRUE);
                }
            }
        } catch (Exception e) {
            log.error("validate project error", e);
            return ResultVO.error();
        }
        return ResultVO.success(Boolean.FALSE);
    }

    @ApiOperation(value = "注销项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", paramType = "body", dataType = "ProjectInfoVo", required = true, value = "项目信息") })
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "canceled" }, method = RequestMethod.POST)
    ResultVO canceledProject(@RequestBody ProjectInfoVo data) {
        try {
            if (null != data.getProjectId()) {
                ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(data.getProjectId());
                StatusCode statusCode = checkUserAuth(data);
                if(!statusCode.equals(StatusCode.SUCCESS)) return ResultVO.error(statusCode);

                statusCode = checkDatalabAndJobStatus(projectInfo);
                if(!statusCode.equals(StatusCode.SUCCESS)) return ResultVO.error(statusCode);

                if(projectInfo.getStatusCode().equals(StatusCode.PROJECT_IS_DELETED.getCode()) || projectInfo.getStatusCode().equals(StatusCode.PROJECT_IS_DISABLED.getCode()))
                    return ResultVO.success();

                projectInfo.setStatusCode(StatusCode.PROJECT_IS_DISABLED.getCode());
                projectInfo.setUpdateTime(new Date());
                projectInfo.setUpdateUserId(data.getUpdateUserId());
                projectInfoService.updateByPrimaryKeySelective(projectInfo);
                return ResultVO.success();
            }
        } catch (Exception e) {
            log.error("canceled project error", e);
            return ResultVO.error(StatusCode.PROJECT_RESOURCE_RELEASE_ERROR);
        }
        return ResultVO.error(StatusCode.PROJECT_RESOURCE_RELEASE_ERROR);
    }


    @ApiOperation(value = "注销项目确认")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", paramType = "path", dataType = "int", required = true, value = "状态，1:确认，0:回滚"),
            @ApiImplicitParam(name = "data", paramType = "body", dataType = "ProjectInfoVo", required = true, value = "项目信息"),
    })
    @ApiResponses({ @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误") })
    @RequestMapping(value = { "/canceled/confirm/{code}" }, method = RequestMethod.POST)
    ResultVO canceledProjectConfirm(@PathVariable("code") int code,@RequestBody ProjectInfoVo data) {
        try {
            if (null != data.getProjectId()) {
                ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(data.getProjectId());
                StatusCode statusCode = checkUserAuth(data);
                if(!StatusCode.SUCCESS.equals(statusCode))return ResultVO.error(statusCode);

                if(code == 0){//rollback
                    ProjectInfo projectInfoNew = new ProjectInfo();
                    projectInfoNew.setProjectId(projectInfo.getProjectId());
                    projectInfoNew.setProjectCode(projectInfo.getProjectCode());
                    projectInfoNew.setStatusCode(StatusCode.PROJECT_IS_OPENED.getCode());
                    projectInfoNew.setUpdateTime(new Date());
                    projectInfoNew.setUpdateUserId(data.getUpdateUserId());
                    projectInfoService.updateByPrimaryKeySelective(projectInfoNew);
                    log.info("cancel project rollback success.");
                }else if(code == 1){//release resource
                    data.setProjectCode(projectInfo.getProjectCode());
                    boolean success = retryReleaseResource(data, 3);
                    if(success) {
                        log.info("release resource success.");
                    }else {
                        log.info("release resource failed. please check the failed reason.");
                        return ResultVO.error(StatusCode.PROJECT_RESOURCE_RELEASE_ERROR);
                    }
                }else{
                    return ResultVO.error(StatusCode.PROJECT_RESOURCE_RELEASE_CODE_ERROR);
                }

                return ResultVO.success();
            }
        } catch (Exception e) {
            log.error("canceled project error", e);
            return ResultVO.error(StatusCode.PROJECT_RESOURCE_RELEASE_ERROR);
        }
        return ResultVO.error(StatusCode.PROJECT_RESOURCE_RELEASE_ERROR);
    }

    private boolean retryReleaseResource(ProjectInfoVo data, int times) throws Exception{
        int i = 1;
        do {
            try{
                if(i != 1)log.info("retry release resource....");
                releaseResource(data);
                return true;
            }catch (Exception ex){
                log.error("release resource exception. reason:{}",ex.getMessage());
                Thread.sleep(500);
            }
        }while (++i<times);

        return false;
    }

    private void releaseResource(ProjectInfoVo data)throws  Exception{
        destroyDatalab(data);
        List<Long> availableInstancesId = instanceInfoService.listByProjectIdAndStatusCode(data.getProjectId(),
                StatusCode.EXECUTION_UNIT_CREATED,
                StatusCode.EXECUTION_UNIT_STOPPED).stream()
                .map(InstanceInfoWithHostsDO::getInstanceId)
                .collect(Collectors.toList());
        availableInstancesId.forEach(id -> {
            executionUnitService.executionUnitGroupDestroy(id, data.getUpdateUserId(), data.getUserName());
        });
        List<Long> availableInstancesId2 = instanceInfoService.listByProjectIdAndStatusCode(data.getProjectId(),
                StatusCode.EXECUTION_UNIT_STARTED).stream()
                .map(InstanceInfoWithHostsDO::getInstanceId)
                .collect(Collectors.toList());
        availableInstancesId2.forEach(id -> {
            executionUnitService.executionUnitGroupStop(id, data.getUpdateUserId(), data.getUserName());
            executionUnitService.executionUnitGroupDestroy(id, data.getUpdateUserId(), data.getUserName());
        });

        queueService.canceledQueue(data.getProjectId());
        projectInfoService.canceledProjectAndReleaseResource(data.getProjectId(), data.getUpdateUserId());
    }

    private StatusCode checkUserAuth(ProjectInfoVo projectInfo){
        ProjectInfoDO memberInfo = memberService.selectMemberByProjectIdAndUserId(projectInfo.getProjectId(), projectInfo.getUpdateUserId());
        if(memberInfo != null && (ProjectRoleEnum.OWNER.code.equals(memberInfo.getRoleId()) || ProjectRoleEnum.ADMIN.code.equals(memberInfo.getRoleId())))
            return StatusCode.SUCCESS;

        return StatusCode.PROJECT_MEMBER_AUTH_ERROR;
    }

    private StatusCode checkDatalabAndJobStatus(ProjectInfo projectInfo){
        boolean existsAliveDatalab = existsAliveDatalab(projectInfo);
        boolean existsOnlineJob = existsOnlineJob(projectInfo);
        if(existsAliveDatalab && existsOnlineJob){
            return StatusCode.PROJECT_DTATLAB_JOB_ONLINE_ERROR;
        }else if(existsAliveDatalab){
            return StatusCode.PROJECT_DTALAB_UNCOLSED_ERROR;
        }else if(existsOnlineJob){
            return StatusCode.PROJECT_JOB_ONLINE_ERROR;
        }

        return StatusCode.SUCCESS;
    }

    private boolean existsOnlineJob(ProjectInfo projectInfo){
        boolean existDeployJob = false;
        JobCause jobCause = new JobCause();
        jobCause.setProjectId(projectInfo.getProjectId());
        jobCause.setStatus(JobStatus.DEPLOY.getType());
        jobCause.setJobType(3);
        List<Job> jobList = jobService.getProjectJobList(jobCause);
        jobList.forEach(job -> {
            log.info("job status is deployed. job:{}, project:{}", job.getJobName(),projectInfo.getProjectCode());
        });

        return jobList.size()>0;
    }

    private boolean existsAliveDatalab(ProjectInfo projectInfo){
        boolean isLabAlive = false;
        List<DatalabInfo> datalabInfos = datalabInfoService.get(projectInfo.getProjectCode());
        for(DatalabInfo datalabInfo : datalabInfos){
            isLabAlive = labStatusService.isAlive(projectInfo.getProjectCode(), datalabInfo.getLabCode());
            if(isLabAlive) {
                log.info("datalab is alive. lab:{},project:{}",datalabInfo.getLabCode(),projectInfo.getProjectCode());
                break;
            }
        }

        return isLabAlive;
    }

    private void destroyDatalab(ProjectInfoVo projectInfo){
        List<DatalabInfo> datalabInfos = datalabInfoService.get(projectInfo.getProjectCode());
        datalabInfos.forEach(datalabInfo ->{
            labService.deleteLab(projectInfo.getProjectCode(),datalabInfo.getLabCode());
            log.info("datalab destroyed success . lab:{},project:{}", datalabInfo.getLabCode(), projectInfo.getProjectCode());
        });
    }

    /**
     * 转换项目信息
     *
     * @param vo 页面传输信息
     * @param flag 添加更新标示
     * @return
     */
    private ProjectInfo parse(ProjectInfoVo vo, Boolean flag) {
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setProjectCode(vo.getProjectCode());
        projectInfo.setProjectName(vo.getProjectName());
        projectInfo.setProjectDesc(vo.getProjectDesc());
        projectInfo.setProjectManagerId(vo.getCreateUserId());
        projectInfo.setManagerTelephone(vo.getManagerTelephone());
        projectInfo.setCreateUserId(vo.getCreateUserId());
        projectInfo.setCaasTopicId(vo.getCaasTopicId());
        projectInfo.setUserName(vo.getUserName());
        if (flag) {
            projectInfo.setCreateTime(new Date());
            projectInfo.setStatusCode(StatusCode.PROJECT_IS_OPENED.getCode());
        } else { // 更新
            projectInfo.setCreateTime(vo.getCreateTime());
            projectInfo.setProjectId(vo.getProjectId());
            projectInfo.setUpdateTime(new Date());
            projectInfo.setUpdateUserId(vo.getUpdateUserId());
            projectInfo.setStatusCode(vo.getStatusCode());
        }
        return projectInfo;
    }

    /**
     * 授权信息
     *
     * @param data
     * @param type
     * @param userName
     * @return
     */
    public static AuthorizationDataDW getDataDW(String policy, AuthorizationDataDW data, String type, String userName) {
        String common = "*";
        List tables, columns, permissions, users;
        users = new ArrayList();
        users.add(userName);

        tables = new ArrayList();
        tables.add(common);
        columns = new ArrayList();
        columns.add(common);
        permissions = new ArrayList();
        permissions.add(DWPermissions.SELECT.getValue());

        List<AuthorizationPerm> perms = new ArrayList<>();
        AuthorizationPerm perm = new AuthorizationPerm();
        switch (type) {
        case "4": // 项目 owner
        case "5": // 项目 admin
            data.setTables(tables);
            data.setColumns(columns);
            data.setAuthorizationName(policy + "_owner_admin");
            perm.setPermUserList(users);
            permissions.add(DWPermissions.ALL.getValue());
            permissions.add(DWPermissions.ALTER.getValue());
            permissions.add(DWPermissions.CREATE.getValue());
            permissions.add(DWPermissions.DROP.getValue());
            permissions.add(DWPermissions.INDEX.getValue());
            permissions.add(DWPermissions.UPDATE.getValue());
            permissions.add(DWPermissions.LOCK.getValue());
            perm.setPermPermList(permissions);
            perms.add(perm);
            data.setAuthorizationPermMapList(perms);
            break;
        case "1": // 项目开发者
            tables.add("1");
            data.setTables(tables);
            data.setColumns(columns);
            data.setAuthorizationName(policy + "_develop");
            perm.setPermUserList(users);
            permissions.add(DWPermissions.CREATE.getValue());
            perm.setPermPermList(permissions);
            perms.add(perm);
            data.setAuthorizationPermMapList(perms);
            break;
        default: // 项目查询用户
            tables.add("2");
            data.setTables(tables);
            data.setColumns(columns);
            data.setAuthorizationName(policy + "_query");
            perm.setPermUserList(users);
            perm.setPermPermList(permissions);
            perms.add(perm);
            data.setAuthorizationPermMapList(perms);
            break;
        }
        return data;
    }

}
