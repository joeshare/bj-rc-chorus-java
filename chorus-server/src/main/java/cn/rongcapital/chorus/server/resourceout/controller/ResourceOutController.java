package cn.rongcapital.chorus.server.resourceout.controller;

import cn.rongcapital.chorus.common.util.FTPUtils;
import cn.rongcapital.chorus.das.entity.PagingEntity;
import cn.rongcapital.chorus.das.entity.ResourceOut;
import cn.rongcapital.chorus.das.entity.ResourceOutDO;
import cn.rongcapital.chorus.das.entity.ResourceOutEnum;
import cn.rongcapital.chorus.das.entity.web.ProjectCause;
import cn.rongcapital.chorus.das.service.ResourceOutService;
import cn.rongcapital.chorus.server.database.controller.util.MySQLUtils;
import cn.rongcapital.chorus.server.database.controller.vo.DBVo;
import cn.rongcapital.chorus.server.resourceout.vo.QueryVo;
import cn.rongcapital.chorus.server.resourceout.vo.ResourceOutVo;
import cn.rongcapital.chorus.server.vo.ResultVO;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 项目操作 API
 *
 * @author shicheng
 * @version 1.0
 * @since <pre>十一月 23, 2016</pre>
 */
@RestController
@RequestMapping(value = {"/resource/out"})
@Api(value = "外部资源 API")
public class ResourceOutController {

    private ResourceOutService resourceOutService = null;

    @Resource(name = "ResourceOutService")
    public void setService(ResourceOutService service) {
        this.resourceOutService = service;
    }

    @ApiOperation(value = "添加外部资源信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", paramType = "body", dataType = "ResourceOutVo", required = true, value = "外部资源信息")
    })
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"new"}, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    ResultVO insert(@RequestBody ResourceOutVo data) {
        try {
            int count = resourceOutService.insert(parse(data, true));
            if (count > 0) {
                return ResultVO.success();
            }
        } catch (Exception e) {
            return ResultVO.error();
        }
        return ResultVO.error();
    }

    @ApiOperation(value = "根据资源名称查找资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", paramType = "path", dataType = "integer", required = true, value = "需要查询的页数"),
            @ApiImplicitParam(name = "pageSize", paramType = "path", dataType = "integer", required = true, value = "每页显示数据总数")
    })
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"get/n/{pageNum}/{pageSize}"}, method = RequestMethod.POST)
    ResultVO<PageInfo> getDataByName(@RequestBody QueryVo queryVo,
                                     @PathVariable int pageNum,
                                     @PathVariable int pageSize) {
        try {
            List<ResourceOut> resourceOuts = resourceOutService.selectResourceOutByName(queryVo.getProjectId(),
                    queryVo.getData(), queryVo.getIsAccurate(), pageNum, pageSize);
            ResultVO<PageInfo> result = getPageInfoResultVO(resourceOuts);
            return result;
        } catch (Exception e) {
            return ResultVO.error();
        }
    }

    @ApiOperation(value = "根据资源类型查找资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", paramType = "path", dataType = "integer", required = true, value = "需要查询的页数"),
            @ApiImplicitParam(name = "pageSize", paramType = "path", dataType = "integer", required = true, value = "每页显示数据总数")
    })
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"get/t/{pageNum}/{pageSize}"}, method = RequestMethod.POST)
    ResultVO<PageInfo> getDataByType(@RequestBody QueryVo queryVo,
                                     @PathVariable int pageNum,
                                     @PathVariable int pageSize) {
        try {
            List<ResourceOut> resourceOuts = resourceOutService.selectResourceOutByType(queryVo.getProjectId(),
                    queryVo.getData(), queryVo.getIsAccurate(), pageNum, pageSize);
            ResultVO<PageInfo> result = getPageInfoResultVO(resourceOuts);
            return result;
        } catch (Exception e) {
            return ResultVO.error();
        }
    }

    @ApiOperation(value = "根据资源用途查找资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", paramType = "path", dataType = "integer", required = true, value = "需要查询的页数"),
            @ApiImplicitParam(name = "pageSize", paramType = "path", dataType = "integer", required = true, value = "每页显示数据总数")
    })
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"get/us/{pageNum}/{pageSize}"}, method = RequestMethod.POST)
    ResultVO<PageInfo> getDataByUsage(@RequestBody QueryVo queryVo,
                                      @PathVariable int pageNum,
                                      @PathVariable int pageSize) {
        try {
            List<ResourceOut> resourceOuts = resourceOutService.selectResourceOutByUsage(queryVo.getProjectId(),
                    queryVo.getData(), queryVo.getIsAccurate(), pageNum, pageSize);
            ResultVO<PageInfo> result = getPageInfoResultVO(resourceOuts);
            return result;
        } catch (Exception e) {
            return ResultVO.error();
        }
    }

    @ApiOperation(value = "根据用户查找资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", paramType = "path", dataType = "integer", required = true, value = "需要查询的页数"),
            @ApiImplicitParam(name = "pageSize", paramType = "path", dataType = "integer", required = true, value = "每页显示数据总数")
    })
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"get/ui/{pageNum}/{pageSize}"}, method = RequestMethod.POST)
    ResultVO<PageInfo> getDataByUserId(@RequestBody QueryVo queryVo,
                                       @PathVariable int pageNum,
                                       @PathVariable int pageSize) {
        try {
            List<ResourceOut> resourceOuts = resourceOutService.selectResourceOutByUserId(queryVo.getProjectId(),
                    queryVo.getData(), queryVo.getIsAccurate(), pageNum, pageSize);
            ResultVO<PageInfo> result = getPageInfoResultVO(resourceOuts);
            return result;
        } catch (Exception e) {
            return ResultVO.error();
        }
    }

    @ApiOperation(value = "根据项目查找资源")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "pageNum", paramType = "path", dataType = "integer", required = true, value = "需要查询的页数"),
//            @ApiImplicitParam(name = "pageSize", paramType = "path", dataType = "integer", required = true, value = "每页显示数据总数")
//    })
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"get/p"}, method = RequestMethod.POST)
//    ResultVO<PageInfo> getDataByProjectId(@RequestBody QueryVo queryVo) {
//        try {
//            List<ResourceOut> resourceOuts = resourceOutService.selectResourceOutByProjectId(queryVo.getProjectId());
//            ResultVO<PageInfo> result = getPageInfoResultVO(resourceOuts);
//            return result;
//        } catch (Exception e) {
//            return ResultVO.error();
//        }
//    }
    PagingEntity getDataByProjectId(@RequestBody ProjectCause cause) {
        List<ResourceOutDO> resourceOuts = resourceOutService.selectResourceOutDOByProjectId(cause.getProjectId());
        PagingEntity pagingEntity = new PagingEntity();
        pagingEntity.setiTotalDisplayRecords(resourceOuts.size());
        pagingEntity.setAaData(resourceOuts);
        return pagingEntity;
    }

    @ApiOperation(value = "根据资源 id查找资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "resourceOutId", paramType = "path", dataType = "string", required = true, value = "资源 id"),
    })
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"get/{resourceOutId}"}, method = RequestMethod.GET)
    ResultVO<ResourceOut> getDataById(@PathVariable Long resourceOutId) {
        try {
            if (resourceOutId != null) {
                ResourceOut resourceOut = resourceOutService.selectByPrimaryKey(resourceOutId);
                return ResultVO.success(resourceOut);
            }
        } catch (Exception e) {
            return ResultVO.error();
        }
        return ResultVO.error();
    }

    @ApiOperation(value = "根据资源 id 删除资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "resourceOutId", paramType = "path", dataType = "string", required = true, value = "资源 id"),
    })
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"del/{resourceOutId}"}, method = RequestMethod.GET)
    ResultVO<ResourceOut> delDataById(@PathVariable Long resourceOutId) {
        try {
            Integer count = resourceOutService.deleteByPrimaryKey(resourceOutId);
            if (count > 0) {
                return ResultVO.success();
            }
        } catch (Exception e) {
            return ResultVO.error();
        }
        return ResultVO.error();
    }

    @ApiOperation(value = "根据项目 id 删除资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", paramType = "path", dataType = "string", required = true, value = "项目 id"),
    })
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"del/p/{projectId}"}, method = RequestMethod.GET)
    ResultVO<ResourceOut> delDataByProjectId(@PathVariable String projectId) {
        try {
            if (NumberUtils.isNumber(projectId)) {
                Integer count = resourceOutService.deleteByProjectId(Long.parseLong(projectId));
                if (count > 0) {
                    return ResultVO.success();
                }
            }
        } catch (Exception e) {
            return ResultVO.error();
        }
        return ResultVO.error();
    }

    @ApiOperation(value = "更新外部资源信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", paramType = "body", dataType = "ResourceOutVo", required = true, value = "外部资源信息")
    })
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"update"}, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    ResultVO update(@RequestBody ResourceOutVo data) {
        try {
            int count = resourceOutService.updateByPrimaryKeySelective(parse(data, false));
            if (count > 0) {
                return ResultVO.success();
            }
        } catch (Exception e) {
            return ResultVO.error();
        }
        return ResultVO.error();
    }

    /**
     * 获取结果信息
     *
     * @param resourceOuts 源数据
     * @return 数据集
     */
    private ResultVO<PageInfo> getPageInfoResultVO(List<ResourceOut> resourceOuts) {
        PageInfo pageInfo = new PageInfo(resourceOuts);
        return ResultVO.success(pageInfo);
    }

    /**
     * 封装资源信息
     *
     * @param vo 前端页面信息
     * @param flag true 添加, false 更新
     * @return 资源信息
     */
    private ResourceOut parse(ResourceOutVo vo, boolean flag) {
        ResourceOut resourceOut = new ResourceOut();
        // TODO: 此处配置从 MySQL 中读取 jdbc:mysql://%s:%s
        DBVo dbVo = new DBVo();
        dbVo.setDatabaseName(vo.getDatabaseName());
        dbVo.setHost(vo.getHost());
        dbVo.setPort(vo.getPort());
        dbVo.setUserName(vo.getUserName());
        dbVo.setPassword(vo.getUserPassword());
        dbVo.setType(vo.getType());
        String url = "";
        if(ResourceOutEnum.MYSQL.getCode().equals(vo.getType())){
            url = MySQLUtils.getMysqlConnectionUrlByDB(dbVo);
        }
        if(ResourceOutEnum.FTP.getCode().equals(vo.getType())){
            dbVo.setPath(vo.getPath());
            url = FTPUtils.getFtpConnectionUrl(vo.getHost(), vo.getPort(), vo.getUserName(), vo.getUserPassword(), vo.getPath());
        }
        resourceOut.setConnPort(vo.getPort());
        resourceOut.setConnPass(vo.getUserPassword());
        resourceOut.setConnUser(vo.getUserName());
        resourceOut.setCreateUserId(vo.getUserId());
        resourceOut.setConnPass(vo.getUserPassword());
        resourceOut.setResourceDesc(vo.getDescription());
        resourceOut.setProjectId(vo.getProjectId());
        resourceOut.setStorageType(vo.getType());
        resourceOut.setResourceName(vo.getResourceName());
        resourceOut.setResourceUsage(vo.getUsage());
        resourceOut.setDatabaseName(vo.getDatabaseName());
        resourceOut.setConnHost(vo.getHost());
        resourceOut.setConnUrl(url);
        resourceOut.setPath(vo.getPath());
        resourceOut.setConnectMode(vo.getConnectMode());
        if (flag) {
            resourceOut.setCreateTime(new Date());
            resourceOut.setCreateUserName(vo.getCreateUserName());
        } else {
            resourceOut.setResourceOutId(vo.getResourceOutId());
            resourceOut.setUpdateTime(new Date());
            resourceOut.setUpdateUserId(vo.getUpdateUserId());
        }
        return resourceOut;
    }

}
