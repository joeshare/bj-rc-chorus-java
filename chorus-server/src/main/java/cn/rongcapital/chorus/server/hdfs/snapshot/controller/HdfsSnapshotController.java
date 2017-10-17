package cn.rongcapital.chorus.server.hdfs.snapshot.controller;

import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.rongcapital.chorus.common.hadoop.HadoopClient;
import cn.rongcapital.chorus.server.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lovett
 */
@RestController
@RequestMapping(value = {"/hdfssnapshot"})
@Api(value = "HDFS 快照 API")
@Slf4j
public class HdfsSnapshotController {

    @Autowired
    private HadoopClient hadoopClient;

    @ApiOperation(value = "开启指定Hdfs 目录下的所有子目录的快照功能")
    @ApiResponses({
            @ApiResponse(code = 401, message = "未经授权：访问由于凭据无效被拒绝"),
            @ApiResponse(code = 403, message = "服务器资源不可用"),
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径错误")
    })
    @RequestMapping(value = {"/allow"}, method = RequestMethod.POST)
    @ResponseBody
    ResultVO<String> getAllProjectByUserId(@RequestBody String hdfsParentPath) {
        try {
            RemoteIterator<LocatedFileStatus> listDirs = hadoopClient.listFiles(hdfsParentPath);
            while (listDirs.hasNext()) {
                LocatedFileStatus next = listDirs.next();
                Path path = next.getPath();
                hadoopClient.allowSnapshot(path.toString());
            }
            return ResultVO.success("开启Snapshot成功！");
        } catch (Exception e) {
            log.error("get data error", e);
            return ResultVO.error();
        }
    }

}
