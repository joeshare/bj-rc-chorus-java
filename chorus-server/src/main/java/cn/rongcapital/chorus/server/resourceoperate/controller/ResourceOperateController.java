package cn.rongcapital.chorus.server.resourceoperate.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.NoSuchStatusCodeException;
import cn.rongcapital.chorus.common.exception.ResourceNotEnoughException;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.OrikaBeanMapper;
import cn.rongcapital.chorus.das.entity.ResourceApproveDO;
import cn.rongcapital.chorus.das.entity.ResourceInner;
import cn.rongcapital.chorus.das.entity.ResourceOperation;
import cn.rongcapital.chorus.das.entity.ResourceOperationDO;
import cn.rongcapital.chorus.das.entity.TotalResource;
import cn.rongcapital.chorus.das.service.QueueService;
import cn.rongcapital.chorus.das.service.ResourceOperationService;
import cn.rongcapital.chorus.resourcemanager.service.TotalResourceService;
import cn.rongcapital.chorus.server.resourceoperate.param.ResourceApplyQuery;
import cn.rongcapital.chorus.server.resourceoperate.param.ResourceApproveParam;
import cn.rongcapital.chorus.server.resourceoperate.param.ResourceOperateParam;
import cn.rongcapital.chorus.server.resourceoperate.vo.ResourceOperationVO;
import cn.rongcapital.chorus.server.resourceoperate.vo.ResourceVO;
import cn.rongcapital.chorus.server.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by abiton on 15/11/2016.
 */
@RestController
@Slf4j
public class ResourceOperateController {

    @Autowired
    ResourceOperationService resourceOperateService;
    @Autowired
    private TotalResourceService totalResourceService;
    @Autowired
    private QueueService queueService;

    @RequestMapping(value = "/resource/apply/{pageNum}/{pageSize}", method = RequestMethod.POST)
    public ResultVO<PageInfo> applyForProject(@RequestBody ResourceApplyQuery query,
                                                         @PathVariable int pageNum, @PathVariable int pageSize) {
       return applyForProjectByStatus(query.getStatus(),pageNum,pageSize);
    }

    private ResultVO<PageInfo> applyForProjectByStatus(String statusCode,int pageNum,int pageSize){

        List<ResourceOperationDO> operationList =
                resourceOperateService.getResourceOperationByStatus(statusCode, pageNum, pageSize);
        PageInfo page = new PageInfo(operationList);

        List<ResourceOperationVO> data = new ArrayList<>();
        page.getList().forEach(operation -> {
            data.add(OrikaBeanMapper.INSTANCE.map(operation, ResourceOperationVO.class));
        });
        ResultVO<PageInfo> result = ResultVO.success(page);
        return result;

    }

    @RequestMapping(value = "/resource/apply/pending/{pageNum}/{pageSize}",method = RequestMethod.GET)
    public ResultVO<PageInfo> pendingApplyForProject(@PathVariable int pageNum, @PathVariable int pageSize){
        return applyForProjectByStatus(StatusCode.RESOURCE_OPERATE_APPLY.getCode(),pageNum,pageSize);
    }

    @RequestMapping(value = "/resource/apply/approved/{pageNum}/{pageSize}",method = RequestMethod.GET)
    public ResultVO<PageInfo> approvedApplyForProject(@PathVariable int pageNum, @PathVariable int pageSize){
        return applyForProjectByStatus(StatusCode.RESOURCE_OPERATE_APPROVE.getCode(),pageNum,pageSize);
    }

    @RequestMapping(value = "/resource/apply/denied/{pageNum}/{pageSize}",method = RequestMethod.GET)
    public ResultVO<PageInfo> deniedApplyForProject(@PathVariable int pageNum, @PathVariable int pageSize){
        return applyForProjectByStatus(StatusCode.RESOURCE_OPERATE_DENY.getCode(),pageNum,pageSize);
    }

    @RequestMapping(value = "/resource/apply" ,method = RequestMethod.POST)
    public ResultVO approveResourceApply(@RequestBody ResourceOperateParam param) {

        ResourceOperation operation = OrikaBeanMapper.INSTANCE.map(param,ResourceOperation.class);
        operation.setCreateUserId(param.getUserId());
        operation.setCreateUserName(param.getUserName());

        ResourceInner resourceInner = new ResourceInner();
        resourceInner.setCreateUserId(param.getUserId());
        resourceInner.setProjectId(param.getProjectId());
        resourceInner.setResourceCpu(param.getCpu());
        resourceInner.setResourceMemory(param.getMemory());
        resourceInner.setResourceStorage(param.getStorage());

        ResultVO resultVO = ResultVO.success();
        try {
            resourceOperateService.applyResourceForProject(operation, resourceInner);
        } catch (RuntimeException e){
            log.error("approve resource error ",e);
            resultVO = ResultVO.error();
        }
        return resultVO;
    }

    @RequestMapping(value = "/resource/apply/project", method = RequestMethod.POST)
    public ResultVO<List<ResourceOperationVO>> projectApplyList(@RequestBody ResourceApplyQuery query) {
        if (query.getProjectId() == null || query.getProjectId() < 0) {
            throw new RuntimeException("projectId do not correct");
        }
        List<ResourceOperationDO> operationList =
                resourceOperateService.getResourceOperationByProjectId(query.getProjectId());
        List<ResourceOperationVO> data = new ArrayList<>();
        operationList.forEach(operation -> {
            data.add(OrikaBeanMapper.INSTANCE.map(operation, ResourceOperationVO.class));
        });
        ResultVO<List<ResourceOperationVO>> result = ResultVO.success(data);
        return result;

    }

    @RequestMapping(value = "/resource/left", method = RequestMethod.GET)
    public ResultVO<ResourceVO> resourceLeft() {
        TotalResource total = totalResourceService.getLeftResource();
        ResourceVO left = OrikaBeanMapper.INSTANCE.map(total, ResourceVO.class);
        ResultVO<ResourceVO> resultVO = ResultVO.success(left);
        return resultVO;
    }

    @RequestMapping(value = "/resource/approve", method = RequestMethod.POST)
    public ResultVO resourceApprove(@RequestBody ResourceApproveParam param) {
        ResultVO resultVO;
        try {
            ResourceApproveDO approveDO = OrikaBeanMapper.INSTANCE.map(param,ResourceApproveDO.class);
            TotalResource leftResource = totalResourceService.getLeftResourceWithQueueCapacity();
            TotalResource totalResource = totalResourceService.getTotalResourceWithQueueCapacity();
            approveDO.setLeftResource(leftResource);
            approveDO.setTotalResource(totalResource);
            resourceOperateService.approveResource(approveDO);

            resultVO = ResultVO.success();
        } catch (NoSuchStatusCodeException e) {
            resultVO = ResultVO.error(StatusCode.EXCEPTION_NOSUCHSTATUSCODE);
        } catch (ResourceNotEnoughException e) {
            resultVO = ResultVO.error(StatusCode.EXCEPTION_RESOURCENOTENOUGH);
        } catch (ServiceException e){
            resultVO = ResultVO.error(e);
        }
        return resultVO;
    }

}
