package cn.rongcapital.chorus.server.dynamic_source.controller;

import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.service.ColumnInfoServiceV2;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import cn.rongcapital.chorus.server.dynamic_source.param.ProjectIdParam;
import cn.rongcapital.chorus.server.dynamic_source.param.TableIdParamV2;
import cn.rongcapital.chorus.server.dynamic_source.vo.InternalDataSourceField;
import cn.rongcapital.chorus.server.dynamic_source.vo.InternalDataSourceTableV2;
import cn.rongcapital.chorus.server.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by abiton on 18/07/2017.
 */
@RestController
@Slf4j
@RequestMapping("/internal_datasource")
public class InternalDataSourceController {
    @Autowired
    private TableInfoServiceV2 tableInfoService;
    @Autowired
    private ColumnInfoServiceV2 columnInfoService;

    private List<InternalDataSourceTableV2> listTables(ProjectIdParam param) {
        List<TableInfoV2> tableInfos = tableInfoService.listAllTableInfo(param.getProjectId(), 1, 1000);
        List<InternalDataSourceTableV2> result = tableInfos.stream().map(t -> {
            InternalDataSourceTableV2 table = new InternalDataSourceTableV2();
            table.setId(t.getTableInfoId());
            table.setName(t.getTableName());
            return table;
        }).collect(Collectors.toList());
        return result;
    }

    @RequestMapping("/table")
    public ResultVO<List<InternalDataSourceTableV2>> listTablesWithResultVO(@RequestBody ProjectIdParam param) {
        try {
            List<InternalDataSourceTableV2> tables = this.listTables(param);
            return ResultVO.success(tables);
        } catch (ServiceException e) {
            log.error("get internal tables error", e);
            return ResultVO.error(e);
        }
    }

    private List<InternalDataSourceField> listFields(TableIdParamV2 param) {
        List<ColumnInfoV2> columnInfos = columnInfoService.selectColumnInfo(param.getTableId());
        List<InternalDataSourceField> result = columnInfos.stream()
                .filter(c -> !c.getColumnName().equals("p_date"))
                .sorted((c1, c2) -> {
                    if (c1.getColumnOrder() != null && c2.getColumnOrder() != null) {
                        return c1.getColumnOrder().compareTo(c2.getColumnOrder());
                    } else {
                        return 0;
                    }
                })
                .map(c -> {
                    InternalDataSourceField field = new InternalDataSourceField();
                    field.setName(c.getColumnName());
                    return field;
                }).collect(Collectors.toList());
        return result;
    }

    @RequestMapping("/field")
    public ResultVO<List<InternalDataSourceField>> listFieldsWithResultVO(@RequestBody TableIdParamV2 param) {

        try {
            List<InternalDataSourceField> fields = this.listFields(param);
            return ResultVO.success(fields);
        } catch (ServiceException e) {
            log.error("get internal fields error", e);
            return ResultVO.error(e);
        }
    }

    private List<InternalDataSourceField> listPartitions(TableIdParamV2 param) {
        List<ColumnInfoV2> columnInfos = columnInfoService.selectColumnInfo(param.getTableId());
        List<InternalDataSourceField> result = columnInfos.stream()
                .filter(c ->
                                c.getIsPartitionKey() == 1
                )
                .sorted((c1, c2) -> {
                    if (c1.getColumnOrder() != null && c2.getColumnOrder() != null) {
                        return c1.getColumnOrder().compareTo(c2.getColumnOrder());
                    } else {
                        return 0;
                    }
                })
                .map(c -> {
                    InternalDataSourceField field = new InternalDataSourceField();
                    field.setName(c.getColumnName());
                    return field;
                }).collect(Collectors.toList());
        return result;
    }

    @RequestMapping("/partition")
    public ResultVO<List<InternalDataSourceField>> listPartitionsWithResultVO(@RequestBody TableIdParamV2 param) {
        try {
            List<InternalDataSourceField> fields = this.listPartitions(param);
            return ResultVO.success(fields);
        } catch (ServiceException e) {
            log.error("get internal table dynamic partitions error ", e);
            return ResultVO.error(e);
        }
    }
}
