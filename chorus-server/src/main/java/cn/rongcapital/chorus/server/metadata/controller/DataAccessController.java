package cn.rongcapital.chorus.server.metadata.controller;

import cn.rongcapital.chorus.authorization.api.UserDataAuthorization;
import cn.rongcapital.chorus.authorization.api.data.*;
import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.BeanUtils;
import cn.rongcapital.chorus.das.entity.*;
import cn.rongcapital.chorus.das.service.*;
import cn.rongcapital.chorus.server.metadata.param.ApplyFormParamV2;
import cn.rongcapital.chorus.server.metadata.param.ApplyStatusParam;
import cn.rongcapital.chorus.server.metadata.vo.ColumnAuthorityVoV2;
import cn.rongcapital.chorus.server.metadata.vo.TableColumnAuthorityVoV2;
import cn.rongcapital.chorus.server.vo.ResultVO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by fuxiangli on 2016-11-22.
 */
@Slf4j
@RestController
@RequestMapping(value = "/data_access")
public class DataAccessController {
    @Autowired
    private ApplyFormServiceV2 applyFormService;
    @Autowired
    private ApplyDetailServiceV2 applyDetailService;

    @Autowired
    private TableAuthorityServiceV2    tableAuthorityServiceV2;
    @Autowired
    private TableInfoServiceV2         tableInfoServiceV2;
    @Autowired
    private ColumnInfoServiceV2        columnInfoServiceV2;
    @Autowired
    private ProjectInfoService         projectInfoService;
    @Autowired
    private AuthorizationDetailService authorizationDetailService;


    @Autowired
    @Qualifier(value = "userDataAuthorizationByRanger")
    private UserDataAuthorization authorization;

    /**
     * 提交申请单
     */
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO apply(@RequestBody ApplyFormParamV2 applyFormParam) {
        try {
            applyFormService.bulkInsert(applyFormParam.getTableInfoId(), applyFormParam.getColumnList(),
                    applyFormParam.getDuration(), applyFormParam.getReason(), applyFormParam.getUserId(), applyFormParam.getUserName());
            return ResultVO.success();
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.error("Caught exception in apply !!!", e);
            return ResultVO.error();
        }
    }

    /**
     * 申请单列表
     *
     * @param userId   审批者userId
     * @param approved 审批状态
     * @param pageNum  分页页码
     * @param pageSize 分页单页数量
     */
    @RequestMapping(value = "/select_application/{userId}/{approved}/{pageNum}/{pageSize}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<PageInfo> selectApplication(@PathVariable String userId,
                                                @PathVariable int approved,
                                                @PathVariable int pageNum,
                                                @PathVariable int pageSize) {
        try {
            List<ApplyFormDOV2> applyFormDOList = applyFormService.selectForm(
                    userId, BooleanUtils.toBoolean(approved), pageNum, pageSize);
            PageInfo page = new PageInfo<>(applyFormDOList);
            return ResultVO.success(page);
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.error("Caught exception in selectApplication !!!", e);
            return ResultVO.error();
        }
    }

    /**
     * 用户已提交申请.
     *
     * @param userId   查询用用户userId
     * @param pageNum  分页页码
     * @param pageSize 分页单页数量
     */
    @RequestMapping(value = "/select_application/{userId}/{pageNum}/{pageSize}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<PageInfo> selectApplication(@PathVariable String userId,
                                                @PathVariable int pageNum,
                                                @PathVariable int pageSize) {
        try {
            List<ApplyFormDOV2> applyFormDOList = applyFormService.selectAllForm(userId, pageNum, pageSize);
            PageInfo page = new PageInfo<>(applyFormDOList);
            return ResultVO.success(page);
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.error("Caught exception in selectApplication !!!", e);
            return ResultVO.error();
        }
    }

    /**
     * 申请单明细数据
     *
     * @param applicationId 申请单Id
     */
    @RequestMapping(value = "/application_detail/{applicationId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<List<ApplyDetailDOV2>> applicationDetail(@PathVariable Long applicationId) {
        try {
            List<ApplyDetailDOV2> applyDetailList = applyDetailService.selectApplyFormDetail(applicationId);
            return ResultVO.success(applyDetailList);
        } catch (Exception e) {
            log.error("Caught exception in applicationDetail !!!", e);
            return ResultVO.error();
        }
    }

    /**
     * 审批申请单
     */
    @RequestMapping(value = "/approve", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<Object> approve(@RequestBody ApplyStatusParam applyStatusParam) {
        try {
            // 提交的审核表单信息
            ApplyFormV2 applyForm = applyFormService.selectByPrimaryKey(applyStatusParam.getApplyFormId());
            // 获取表信息
            TableInfoV2 tableInfo = tableInfoServiceV2.selectByID(applyForm.getTableInfoId());
            // 所有的申请列信息
            List<ApplyDetailDOV2> applyDetailDOList = applyDetailService.selectApplyFormDetail(applyStatusParam.getApplyFormId());
            // 项目信息
            ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(tableInfo.getProjectId());
            // 列数据不为空
            if (applyDetailDOList != null) {

                ApplyFormV2 af = new ApplyFormV2();
                af.setApplyFormId(applyStatusParam.getApplyFormId());
                af.setApplyUserId(applyForm.getApplyUserId());
                af.setTableInfoId(applyForm.getTableInfoId());
                af.setStatusCode(applyStatusParam.getStatusCode());
                af.setDealTime(new Date());
                af.setDealUserId(applyStatusParam.getDealUserId());
                af.setDealInstruction(applyStatusParam.getDealInstruction());

                int count = applyFormService.approve(af);
                //如果申请被拒绝 不需要往ranger 发送权限信息
                if(!af.getStatusCode().equals(StatusCode.APPLY_UNTREATED.getCode()))
                    return ResultVO.success();


                List columns = new ArrayList();

                // 遍历授权所有表
                applyDetailDOList.forEach(applyDetailDO -> {
                    columns.add(applyDetailDO.getColumnName());
                });

                columns.sort(new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });

                StringBuffer columnsStr = new StringBuffer();
                columns.forEach(column -> columnsStr.append(column));
                String columnsHash = DigestUtils.md5Hex(columnsStr.toString());
                String dbName = String.format("chorus_%s", projectInfo.getProjectCode());
                // 权限名称,projectCode_tableName_MD5(columns)
                String policyName = String.format("%s_%s_%s", dbName, tableInfo.getTableName(), columnsHash);

                // 创建权限实例对象
                List tables = new ArrayList();
                tables.add(tableInfo.getTableName());
                List databases = new ArrayList();
                databases.add(dbName);
                List permissions = new ArrayList();
                permissions.add(DWPermissions.SELECT.getValue());
                List users = new ArrayList();
                users.add(applyForm.getApplyUserName());
                List<AuthorizationPerm> perms = new ArrayList<>();
                AuthorizationPerm perm = new AuthorizationPerm();
                perm.setPermPermList(permissions);
                perm.setPermUserList(users);
                perms.add(perm);

                AuthorizationDataDW authorizationDataDW = new AuthorizationDataDW();
                authorizationDataDW.setAuthorizationRepositoryType(AuthorizationDataType.DW);
                authorizationDataDW.setEnabled(true);
                authorizationDataDW.setDatabases(databases);
                authorizationDataDW.setTables(tables);
                authorizationDataDW.setColumns(columns);
                authorizationDataDW.setAuthorizationName(policyName);
                authorizationDataDW.setAuthorizationPermMapList(perms);
                //查询ranger 该 policy name 是否已经存在
                AuthorizationData authorizationData = authorization.authorizationSearch(authorizationDataDW);
                if(authorizationData == null) {
                    authorizationData = authorization.authorizationAdd(authorizationDataDW);
                }else{
                    AuthorizationPerm perm1 = authorizationData.getAuthorizationPermMapList().get(0);
                    if(authorizationData.isEnabled()) {
                        perm1.getPermUserList().forEach(e -> {
                            if (!users.contains(e)) users.add(e);
                        });
                    }
                    perm1.setPermUserList(users);
                    ((AuthorizationDataDW)authorizationData).setEnabled(true);
                    ((AuthorizationDataDW)authorizationData).setAuthorizationRepositoryType(AuthorizationDataType.DW);
                    if(!authorization.authorizationUpdate(authorizationData)){
                        log.error("update authorization in Ranger failed. policyId:{}",authorizationData.getAuthorizationId());
                        return ResultVO.error();
                    }
                }

                AuthorizationDetail detail = new AuthorizationDetail();
                detail.setCreateTime(new Date());
                detail.setPolicyId(authorizationData.getAuthorizationId());
                detail.setPolicyName(authorizationData.getAuthorizationName());
                detail.setProjectId(projectInfo.getProjectId());
                detail.setUserId(applyForm.getApplyUserId());
                detail.setCategory(AuthorizationDetailCategory.HIVE.name());
                detail.setEnabled(1);
                authorizationDetailService.insertOrUpdate(detail);
                return ResultVO.success();
            }
            return ResultVO.error();
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.error("Caught exception in approve !!!", e);
            return ResultVO.error();
        }
    }

    /**
     * 返回用户拥有权限的表(字段)列表
     *
     * @param userId 查询用用户userId
     */
    @RequestMapping(value = "/authority/{userId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<List<TableAuthorityDOV2>> getAuthority(@PathVariable String userId) {
        try {
            List<TableAuthorityDOV2> tableAuthorityDOs = tableAuthorityServiceV2.selectByUserId(userId);
            return ResultVO.success(tableAuthorityDOs);
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.error("Caught exception in getAuthority !!!", e);
            return ResultVO.error();
        }
    }

    /**
     * 根据用户Id和表Id获取列的权限列表
     */
    @RequestMapping(value = {"column_authority/{userId}/{tableId}"}, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<TableColumnAuthorityVoV2> columnAuthority(@PathVariable String userId,
                                                              @PathVariable String tableId) {
        try {
            TableInfoV2 tableInfo = tableInfoServiceV2.selectByID(tableId);
//            List<ColumnInfoV2> columnInfoList = columnInfoServiceV2.selectColumnInfo(tableId);
            List<ColumnInfoV2> columnInfoList = columnInfoServiceV2.selectColumnInfoByTableEntity(tableInfo);
//            List<String> authorizedColumnSet = tableAuthorityServiceV2.selectByUserIdAndTableInfoId(userId, tableId);
            List<String> authorizedColumnSet = tableAuthorityServiceV2.selectByUserIdAndTableInfo(userId, tableInfo, columnInfoList);
            List<ColumnAuthorityVoV2> columnList = columnInfoList.stream()
                                                                 .map(c -> {
                        ColumnAuthorityVoV2 columnVo = new ColumnAuthorityVoV2();
                        try {
                            BeanUtils.copyProperties(columnVo, c);
                        } catch (Exception e) {
                            log.error("Caught exception in columnAuthority.columnList.BeanCopy !!!", e);
                        }
                        columnVo.setAuthorized(authorizedColumnSet.contains(c.getColumnInfoId()));
                        return columnVo;
                    })
                                                                 .collect(Collectors.toList());

            TableColumnAuthorityVoV2 res = new TableColumnAuthorityVoV2(tableInfo, columnList);
            return ResultVO.success(res);
        } catch (Exception e) {
            log.error("Caught exception in columnAuthority !!!", e);
            return ResultVO.error();
        }
    }

    /**
     * 返回用户拥有权限的表列表
     *
     * @param userId 查询用用户userId
     */
    @RequestMapping(value = "/table_authority/{userId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<List<TableAuthorityWithTableDOV2>> getTableAuthority(@PathVariable String userId) {
        try {
            return ResultVO.success(tableAuthorityServiceV2.selectTableByUserId(userId));
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.error("Caught exception in getTableAuthority !!!", e);
            return ResultVO.error();
        }
    }

}
