package cn.rongcapital.chorus.server.metadata.controller;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.entity.*;
import cn.rongcapital.chorus.das.service.*;
import cn.rongcapital.chorus.server.database.controller.util.MySQLUtils;
import cn.rongcapital.chorus.server.metadata.vo.SampleDataVo;
import cn.rongcapital.chorus.server.metadata.vo.TableInfoWithProjectNameVoV2;
import cn.rongcapital.chorus.server.metadata.vo.TableVo;
import cn.rongcapital.chorus.server.vo.ResultVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by fuxiangli on 2016-11-18.
 */
@Slf4j
@RestController
@RequestMapping(value = "/metadata")
public class MetaDataController {
    private static  final int _PAGE_NUM = 1;
    private static final int _PAGE_SIZE = 1000; //a bigger value

    @Autowired
    private TableInfoServiceV2     tableInfoServiceV2;
    @Autowired
    private ColumnInfoServiceV2    columnInfoServiceV2;
    @Autowired
    private ResourceOutService     resourceOutService;
    @Autowired
    private HiveTableInfoServiceV2 hiveTableInfoServiceV2;
    @Autowired
    private ProjectInfoService     projectInfoService;


    @RequestMapping(value = "/all/tables-name")
    public ResultVO<List<String>> tablesName(){
        final List<TableInfoDOV2> allTables = tableInfoServiceV2.listAllTables(_PAGE_NUM, Integer.MAX_VALUE);
        return ResultVO.success(allTables.parallelStream().map(TableInfoDOV2::getTableName).distinct().collect(Collectors.toList()));
    }

    // ut
    // 查询所有表信息
    @RequestMapping(value = {"/list_table/{pageNum}/{pageSize}"}, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<PageInfo> listTable(@PathVariable int pageNum, @PathVariable int pageSize,
                                          @RequestParam(required = false) String q) {
        try {
            List<TableInfoDOV2> tableInfoDO;

            //TODO tricky & inefficient method to support pageable
            if (StringUtils.isBlank(q)) {
                tableInfoDO = tableInfoServiceV2.listAllTables(_PAGE_NUM, 200);//TODO issue: just for fix too many guids in get request url
            } else {
                tableInfoDO = tableInfoServiceV2.searchByTableNameAndProjectNameAndProjectCode(q, _PAGE_NUM, _PAGE_SIZE);
            }
            Page<TableInfoDOV2> objects = PageHelper.startPage(pageNum, pageSize);
            int fromIndex = (pageNum - 1) * pageSize;
            int toIndex = fromIndex + pageSize;
            int total = tableInfoDO.size();
            if (total > fromIndex) {
                if (total < toIndex) toIndex = total;
                objects.addAll(tableInfoDO.subList(fromIndex, toIndex));
            }
            objects.setTotal(total);

            PageInfo page = new PageInfo<>(objects);
            return ResultVO.success(page);
        } catch (Exception e) {
            log.warn("Caught exception in getAllTable: ", e);
            return ResultVO.error(StatusCode.SYSTEM_ERR);
        }finally {
            SqlUtil.clearLocalPage();
        }
    }

    // ut
    // 查询表信息
    @RequestMapping(value = {"/table/{tableInfoId}"}, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<TableInfoWithProjectNameVoV2> getTable(@PathVariable String tableInfoId) {
        try {
            TableInfoV2 tableInfo = tableInfoServiceV2.selectByID(tableInfoId);
            if (tableInfo == null)
                return ResultVO.error(StatusCode.TABLE_NOT_EXISTS);
            ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(tableInfo.getProjectId());
            if (projectInfo == null)
                return ResultVO.error(StatusCode.PROJECT_NOT_EXISTS);
            TableInfoWithProjectNameVoV2 res = new TableInfoWithProjectNameVoV2();
            BeanUtils.copyProperties(tableInfo, res);
            res.setProjectName(projectInfo.getProjectName());
            return ResultVO.success(res);
        } catch (Exception e) {
            log.warn("Caught exception in getTable: ", e);
            return ResultVO.error(StatusCode.SYSTEM_ERR);
        }
    }

    // 根据表ID,查询列信息
    @RequestMapping(value = {"/list_column/{tableId}"}, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<List<ColumnInfoV2>> listColumn(@PathVariable String  tableId) {
        List<ColumnInfoV2> columnInfo = columnInfoServiceV2.selectColumnInfo(tableId);
        ResultVO<List<ColumnInfoV2>> resultVO = ResultVO.success(columnInfo);
        return resultVO;
    }

    //5. 内外部表清单(job输入输出接口用)
    @RequestMapping(value = {"/tnamelist/{projectId}"}, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<List<TableVo>> getTableList(@PathVariable Long projectId) {
        List<TableVo> res = new ArrayList<>();
        try {
            String fmt = "%s.%s";
            List<TableVo> tableInnerList = tableInfoServiceV2.listAllTableInfo(projectId).stream()
                                                             .map(t -> new TableVo(t.getTableInfoId().toString(),
                            String.format(fmt, t.getProjectCode(), t.getTableName())))
                                                             .collect(Collectors.toList());
            List<TableVo> tableOutList = resourceOutService.selectResourceOutByProjectId(projectId, Integer.valueOf(ResourceOutEnum.MYSQL.getCode())).stream()
                    .flatMap(ro -> {
                        try {
                            return MySQLUtils.getAllTableToDT(Collections.singletonList(ro)).stream()
                                    .map(n -> new TableVo(n, n));
                        } catch (SQLException e) {
                            e.printStackTrace();
                            return Stream.empty();
                        }
                    })
                    .collect(Collectors.toList());
            res.addAll(tableInnerList);
            res.addAll(tableOutList);
            return ResultVO.success(res);
        } catch (Exception e) {
            log.error("Caught error in getTableList!!!", e);
            return ResultVO.error();
        }
    }


    // 查询样例数据
    @RequestMapping(value = "/sample_data/{tableId}/{size}", method = RequestMethod.GET)
    @ResponseBody
    public ResultVO<SampleDataVo> sampleData(@PathVariable String tableId, @PathVariable Integer size,
                                             @RequestParam(required = false) String userId) {
        try {
            List<Map<String, Object>> sampleDataFromHive = hiveTableInfoServiceV2.getSampleDataFromHive(tableId, size);
            if (sampleDataFromHive == null || sampleDataFromHive.size() <= 0) {
                List<String> columnList = columnInfoServiceV2.selectColumnInfo(tableId).stream()
                                                             .map(ColumnInfoV2::getColumnName)
                                                             .collect(Collectors.toList());
                return ResultVO.success(new SampleDataVo(columnList,
                        Collections.emptyList()));
            }
            return ResultVO.success(new SampleDataVo(sampleDataFromHive.get(0).keySet(),
                    sampleDataFromHive));
        } catch (ServiceException se) {
            log.info("Service exception in sampleData, tableId: {}, userId: {}, exception: {}",
                    tableId, userId, se);
            return ResultVO.error(se);
        } catch (Exception e) {
            log.error("Caught exception in sampleData, tableId: {}, userId: {}, exception: {}",
                    tableId, userId, e);
            log.error("Caught exception in sampleData.", e);
            return new ResultVO<>(StatusCode.SYSTEM_ERR);
        }
    }

}
