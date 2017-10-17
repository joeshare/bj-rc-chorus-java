package cn.rongcapital.chorus.server.database.controller;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.util.FTPUtils;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.entity.ResourceOutEnum;
import cn.rongcapital.chorus.server.database.controller.util.MySQLUtils;
import cn.rongcapital.chorus.server.database.controller.vo.DBVo;
import cn.rongcapital.chorus.server.vo.ResultVO;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 数据库操作 API
 *
 * @author shicheng
 * @version 1.0
 * @since <pre>十一月 23, 2016</pre>
 */
@RestController
@RequestMapping(value = {"/database"})
@Api(value = "数据库 API")
public class DatabaseController {

    @ApiOperation(value = " 测试连接")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", paramType = "body", dataType = "DBVo", required = true, value = "外部资源信息")
    })
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"test/connection"}, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    ResultVO testConnection(@RequestBody DBVo data) {
        try {
            if(StringUtils.isEmpty(data.getType())){
                data.setType(ResourceOutEnum.MYSQL.getCode());
            }
            if(ResourceOutEnum.MYSQL.getCode().equals(data.getType()) && MySQLUtils.testConnection(data)){
                return ResultVO.success("数据库连接成功");
            }else if (ResourceOutEnum.FTP.getCode().equals(data.getType()) && FTPUtils.testFtpConnection(data.getHost(), data.getPort(), data.getUserName(), data.getPassword())){
                return ResultVO.success("服务器连接成功");
            }
        } catch (Exception e) {
            return ResultVO.error(StatusCode.DATABASE_CONN_FAIL);
        }
        return ResultVO.error(StatusCode.DATABASE_CONN_FAIL);
    }

    @ApiOperation(value = "获取所有的数据库")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", paramType = "body", dataType = "DBVo", required = true, value = "外部资源信息")
    })
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"get/database"}, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    ResultVO getAllDatabases(@RequestBody DBVo data) {
        try {
            return ResultVO.success(MySQLUtils.getAllDatabase(data));
        } catch (Exception e) {
            return ResultVO.error(StatusCode.DATABASE_CONN_FAIL);
        }
    }

    @ApiOperation(value = "获取所有的数据表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", paramType = "body", dataType = "DBVo", required = true, value = "外部资源信息")
    })
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"get/table"}, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    ResultVO getAllTables(@RequestBody DBVo data) {
        try {
            if (!MySQLUtils.checkDbExists(data)) {
                return ResultVO.error(StatusCode.DATABASE_NOT_EXISTS);
            }
            return ResultVO.success(MySQLUtils.getAllTable(data));
        } catch (Exception e) {
            return ResultVO.error(StatusCode.DATABASE_CONN_FAIL);
        }
    }

}
