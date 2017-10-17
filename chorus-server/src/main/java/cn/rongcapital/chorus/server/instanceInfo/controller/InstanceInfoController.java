package cn.rongcapital.chorus.server.instanceInfo.controller;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.entity.InstanceInfoDO;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.ResourceInner;
import cn.rongcapital.chorus.das.entity.ResourceTemplate;
import cn.rongcapital.chorus.das.service.InstanceInfoService;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.das.service.ResourceInnerService;
import cn.rongcapital.chorus.das.service.ResourceTemplateService;
import cn.rongcapital.chorus.resourcemanager.ExecutionUnitGroup;
import cn.rongcapital.chorus.resourcemanager.service.ExecutionUnitService;
import cn.rongcapital.chorus.resourcemanager.service.YarnService;
import cn.rongcapital.chorus.server.instanceInfo.param.InstanceInfoCreateParam;
import cn.rongcapital.chorus.server.instanceInfo.param.InstanceInfoModifyParam;
import cn.rongcapital.chorus.server.instanceInfo.param.InstanceInfoOperationParam;
import cn.rongcapital.chorus.server.vo.ResultVO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.yarn.support.console.ContainerClusterReport;

import java.util.List;

/**
 * Created by alan on 12/2/16.
 */
@Slf4j
@RestController
@RequestMapping("/instance_info")
public class InstanceInfoController {

    @Autowired
    private InstanceInfoService instanceInfoService;
    @Autowired
    private ExecutionUnitService executionUnitService;
    @Autowired
    private ResourceTemplateService resourceTemplateService;
    @Autowired
    private ResourceInnerService resourceInnerService;
    @Autowired
    private ProjectInfoService projectInfoService;

    @Autowired
    private YarnService yarnService;

    @RequestMapping("/list/{projectId}")
    public ResultVO<List<InstanceInfoDO>> listByProjectId(@PathVariable Long projectId) {
        List<InstanceInfoDO> instanceInfoList = instanceInfoService.listDOByProject(projectId);
        return ResultVO.success(instanceInfoList);
    }

    @RequestMapping("/list/{projectId}/{pageNum}/{pageSize}")
    public ResultVO<PageInfo> listByProjectIdByPage(@PathVariable Long projectId,
                                                    @PathVariable int pageNum, @PathVariable int pageSize) {
        List<InstanceInfoDO> instanceInfoList = instanceInfoService.listDOByProject(projectId, pageNum, pageSize);
        PageInfo pageInfo = new PageInfo(instanceInfoList);
        return ResultVO.success(pageInfo);
    }

    @RequestMapping("/get/{instanceId}")
    public ResultVO<InstanceInfoDO> get(@PathVariable Long instanceId) {
        InstanceInfoDO instanceInfoList = instanceInfoService.getDOById(instanceId);
        return ResultVO.success(instanceInfoList);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultVO<Long> create(@RequestBody InstanceInfoCreateParam param) {
        try {
            ProjectInfo project = projectInfoService.selectByPrimaryKey(param.getProjectId());
            if (project == null) {
                return ResultVO.error(StatusCode.PROJECT_NOT_EXISTS);
            }
            if (StringUtils.isEmpty(project.getCreateUserId())
                    || StringUtils.isEmpty(project.getUserName())) {
                return ResultVO.error(StatusCode.PROJECT_OWNER_NOT_EXISTS);
            }
            ResourceInner resourceInner = resourceInnerService.getByProjectId(param.getProjectId());
            if (resourceInner == null) {
                return ResultVO.error(StatusCode.RESOURCE_INNER_NOT_EXISTS);
            }
            ResourceTemplate resourceTemplate = resourceTemplateService.getById(param.getResourceTemplateId());
            if (resourceTemplate == null) {
                return ResultVO.error(StatusCode.RESOURCE_TEMPLATE_NOT_EXISTS);
            }
            ExecutionUnitGroup executionUnitGroup = new ExecutionUnitGroup();
            BeanUtils.copyProperties(param, executionUnitGroup);
            executionUnitGroup.setResourceInnerId(resourceInner.getResourceInnerId());
            executionUnitGroup.setResourceTemplate(resourceTemplate);
            Long instanceId = executionUnitService.executionUnitGroupCreate(executionUnitGroup, param.getUserId(), project.getUserName());
            return ResultVO.success(instanceId);
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (DuplicateKeyException e) {
            return ResultVO.error(StatusCode.EXECUTION_UNIT_GROUPNAME_DUPLICATE);
        } catch (Exception e) {
            log.warn("Caught exception in create, param: ", param);
            log.warn("Caught exception in create: ", e);
            return ResultVO.error();
        }
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public ResultVO<Object> modify(@RequestBody InstanceInfoModifyParam param) {
        try {
            executionUnitService.executionUnitGroupModify(param.getInstanceId(), param.getInstanceSize(),
                    param.getInstanceDesc(), param.getUserId(), param.getUserName());
            return ResultVO.success();
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.warn("Caught exception in modify, param: ", param);
            log.warn("Caught exception in modify: ", e);
            return ResultVO.error();
        }
    }

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ResultVO<Object> start(@RequestBody InstanceInfoOperationParam param) {
        try {
            executionUnitService.executionUnitGroupStart(param.getInstanceId(), param.getUserId(), param.getUserName());
            return ResultVO.success();
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.warn("Caught exception in start, param: ", param);
            log.warn("Caught exception in start: ", e);
            return ResultVO.error();
        }
    }

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public ResultVO<Object> stop(@RequestBody InstanceInfoOperationParam param) {
        try {
            executionUnitService.executionUnitGroupStop(param.getInstanceId(), param.getUserId(), param.getUserName());
            return ResultVO.success();
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.warn("Caught exception in stop, param: ", param);
            log.warn("Caught exception in stop: ", e);
            return ResultVO.error();
        }
    }

    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public ResultVO<Object> destroy(@RequestBody InstanceInfoOperationParam param) {
        try {
            executionUnitService.executionUnitGroupDestroy(param.getInstanceId(), param.getUserId(), param.getUserName());
            return ResultVO.success();
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.warn("Caught exception in destroy, param: ", param);
            log.warn("Caught exception in destroy: ", e);
            return ResultVO.error();
        }
    }

    @RequestMapping(value = "/getinfo", method = RequestMethod.POST)
    public ResultVO<String> getExecutionUnitStatus(@RequestBody InstanceInfoCreateParam param){

        try {
            Long projectId = param.getProjectId();
            String groupName = param.getGroupName();

            ContainerClusterReport.ClustersInfoReportData reportData = yarnService.getClusterInfo(yarnService.getXdApplicationId(),
              executionUnitService.assembleClusterId(projectId,groupName));

            return ResultVO.success(reportData.getState());

        } catch (Exception e) {
            log.error("get execution container status error ",e);
            return ResultVO.error();
        }


    }
}
