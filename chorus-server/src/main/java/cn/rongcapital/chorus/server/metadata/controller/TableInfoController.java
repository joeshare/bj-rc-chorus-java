package cn.rongcapital.chorus.server.metadata.controller;

import cn.rongcapital.chorus.common.constant.HiveColumnTypeConstants;
import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.BeanUtils;
import cn.rongcapital.chorus.common.util.BinarySizeUnit;
import cn.rongcapital.chorus.common.util.ProjectUtil;
import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.ResourceInner;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.service.ColumnInfoServiceV2;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.das.service.ResourceInnerService;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import cn.rongcapital.chorus.hadoop.HadoopFSOperations;
import cn.rongcapital.chorus.server.metadata.param.TableInfoParam;
import cn.rongcapital.chorus.server.metadata.vo.TableInfoWithColumnInfoVoV2;
import cn.rongcapital.chorus.server.vo.ResultVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.ContentSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static cn.rongcapital.chorus.common.constant.StatusCode.EXCEPTION_RESOURCENOTENOUGH;

/**
 * Created by fuxiangli on 2016-11-18.
 */
@Slf4j
@RestController
@RequestMapping(value = "/table")
public class TableInfoController {
    private static final int _PAGE_NUM         = 1;
    private static final int _PAGE_SIZE        = 1000; //a bigger value
    private static final int MIN_STORAGE_LIMIT = 1;

    @Autowired
    private TableInfoServiceV2 tableInfoServiceV2;
    //    @Autowired
//    private TableListService  tableInfoServiceV2;
    @Autowired
    private ColumnInfoServiceV2 columnInfoServiceV2;

    @Autowired
    private HadoopFSOperations hadoopFSOperations;

    @Autowired
    private ResourceInnerService resourceInnerService;
    @Autowired
    private ProjectInfoService projectInfoService;
    
    /**
     * 根据项目ID，列出所有表信息(分页)
     */
    @RequestMapping(value = {"/list/{projectId}/{pageNum}/{pageSize}"}, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<PageInfo> list(@PathVariable Long projectId,
                                   @PathVariable int pageNum, @PathVariable int pageSize) {
        try {
            List<TableInfoV2> tableInfos = tableInfoServiceV2.listAllTableInfo(projectId, _PAGE_NUM, _PAGE_SIZE);//TODO tricky & inefficient method to support pageable
            Comparator<TableInfoV2> comparator = (c1, c2) -> c1.getCreateTime().compareTo(c2.getCreateTime());
            tableInfos.sort(comparator.reversed());
            
            Page<TableInfoV2> objects = PageHelper.startPage(pageNum, pageSize);
            int fromIndex = (pageNum - 1) * pageSize;
            int toIndex = fromIndex + pageSize;
            int total = tableInfos.size();
            if (total > fromIndex) {
                if (total < toIndex) toIndex = total;
                objects.addAll(tableInfos.subList(fromIndex, toIndex));
            }
            objects.setTotal(total);
            PageInfo<TableInfoV2> page = new PageInfo<>(objects);
            return ResultVO.success(page);
        } catch (Exception e) {
            log.error("Caught exception in list: ", e);
            return ResultVO.error();
        } finally {
            SqlUtil.clearLocalPage();
        }
    }
    
    /**
     * 创建表和字段
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<Object> create(@RequestBody TableInfoParam tableInfoParam) {
        try {
            Long projectId = tableInfoParam.getProjectId();
            final ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(projectId);
            assert projectInfo != null;

            String tableName = tableInfoParam.getTableName();
            // 校验列是否合法
            if (!validateColumns(tableInfoParam.getColumnInfoList())) {
                return ResultVO.error(StatusCode.COLUMN_TYPE_NOT_SUPPORTED);
            }

            TableInfoV2 exist = tableInfoServiceV2.getAtlasByUnique(tableName, projectId);
            if (exist != null) {
                return ResultVO.error(StatusCode.TABLE_NAME_DUPLICATE);
            }

            if (!remainingSpaceMustMoreThen1M(projectInfo)) return ResultVO.error(new ServiceException(EXCEPTION_RESOURCENOTENOUGH.getCode(), "剩余存储资源少于1M或者还未申请任何资源，不能建表;请申请资源;"));

            List<ColumnInfoV2> columnInfoList;
            TableInfoV2 tableInfo = new TableInfoV2();
            BeanUtils.copyProperties(tableInfo, tableInfoParam);
            columnInfoList = tableInfoParam.getColumnInfoList();
            //数据列需要有顺序
            for (int i = 0; i < columnInfoList.size(); i++) {
                columnInfoList.get(i).setColumnOrder(i);
            }
            //建表元数据入库
            tableInfoServiceV2.createTable(tableInfo, columnInfoList);
            return ResultVO.success();
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.error("Caught exception in create: ", e);
            return ResultVO.error();
        }
    }

    private boolean remainingSpaceMustMoreThen1M(ProjectInfo projectInfo) throws IOException {
        final ResourceInner totalInnerResource = resourceInnerService.getByProjectId(projectInfo.getProjectId());
        if (totalInnerResource == null) {
            log.info("there was not any resource. pls apply for some first.");
            return false;
        }
        final ContentSummary contentSummary = hadoopFSOperations.contentSummary(ProjectUtil.projectLocation(projectInfo.getProjectCode()));

        final long used = contentSummary.getLength(); //TOTO check the length unit
        final long quota = BinarySizeUnit.GIGA.bytes(totalInnerResource.getResourceStorage().longValue());
        final long remaining = quota - used;
        if (BinarySizeUnit.BYTES.mega(remaining) < MIN_STORAGE_LIMIT) {
            log.warn("{}-{}-{},left storage less thaSn {}M", quota, used, MIN_STORAGE_LIMIT);
            return false;
        }
        return true;
    }

    /**
     * 创建表和字段
     */
    @RequestMapping(value = "/alter", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<Object> alter(@RequestBody TableInfoParam tableInfoParam) {
        try {
            List<ColumnInfoV2> columnInfoList = tableInfoParam.getColumnInfoList();
            // 校验列是否合法
            if (!validateColumns(columnInfoList)) {
                return ResultVO.error(StatusCode.COLUMN_TYPE_NOT_SUPPORTED);
            }
            TableInfoV2 tableInfo = new TableInfoV2();
            BeanUtils.copyProperties(tableInfo, tableInfoParam);
            List<ColumnInfoV2> columnInfoV2s = columnInfoServiceV2.selectColumnInfo(tableInfo.getTableInfoId());
            int startOrderIndex = columnInfoV2s == null ? 0 : columnInfoV2s.size();
            //数据列需要有顺序
            for (int i = 0; i < columnInfoList.size(); i++, startOrderIndex++) {
                columnInfoList.get(i).setColumnOrder(startOrderIndex);
            }
            //建表元数据入库
            tableInfoServiceV2.alterTable(tableInfo, columnInfoList);
            return ResultVO.success();
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.error("Caught exception in alter table: ", e);
            return ResultVO.error();
        }
    }
    
    private boolean validateColumns(List<ColumnInfoV2> columnInfoV2List) {
        try {
            columnInfoV2List.forEach(ci -> {
                HiveColumnTypeConstants.valueOf(ci.getColumnType());
            });
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
    
    /**
     * 根据表Id, 获取表信息和列信息
     */
    @RequestMapping(value = "/get_with_column/{tableInfoId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<TableInfoWithColumnInfoVoV2> get(@PathVariable("tableInfoId") String tableInfoId) {
        try {
            TableInfoV2 tableInfo = tableInfoServiceV2.selectByID(tableInfoId);
            if (tableInfo == null)
                return ResultVO.error(StatusCode.TABLE_NOT_EXISTS);
            List<ColumnInfoV2> columnInfoList = columnInfoServiceV2.selectColumnInfo(tableInfoId);
            TableInfoWithColumnInfoVoV2 res = new TableInfoWithColumnInfoVoV2();
            BeanUtils.copyProperties(res, tableInfo);
            res.setColumnInfoList(columnInfoList);
            return ResultVO.success(res);
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.error("Caught exception in get: ", e);
            return ResultVO.error();
        }
    }
    
    @RequestMapping(value = "/get_hive_types", method = RequestMethod.GET)
    @ResponseBody
    public ResultVO<List<String>> getHiveTypes() {
        return ResultVO.success(Arrays.stream(HiveColumnTypeConstants.values())
                                        .map(Enum::toString)
                                        .collect(Collectors.toList()));
    }
    
    /**
     * @param tableInfoId
     * @return
     * @author yunzhong
     * @time 2017年9月6日上午8:59:58
     */
    @RequestMapping(value = "/delete/{tableInfoId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<Object> delete(@PathVariable("tableInfoId") String tableInfoId) {
        try {
            tableInfoServiceV2.delete(tableInfoId);
            return ResultVO.success();
        } catch (ServiceException se) {
            return ResultVO.error(se);
        } catch (Exception e) {
            log.error("Caught exception in create: ", e);
            return ResultVO.error();
        }
    }
}
