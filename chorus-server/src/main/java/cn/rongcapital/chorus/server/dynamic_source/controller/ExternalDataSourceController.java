package cn.rongcapital.chorus.server.dynamic_source.controller;

import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.das.entity.*;
import cn.rongcapital.chorus.das.service.ExternalDataSourceService;
import cn.rongcapital.chorus.das.service.ResourceOutService;
import cn.rongcapital.chorus.server.dynamic_source.param.*;
import cn.rongcapital.chorus.server.dynamic_source.vo.*;
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
@RequestMapping("/external_datasource")
public class ExternalDataSourceController {
    @Autowired
    private ResourceOutService resourceOutService;

    @Autowired
    private ExternalDataSourceService externalDataSourceService;
    public List<ExternalDataSourceRDB> listRDBs(ProjectIdParam projectId) {
        List<ResourceOut> resourceOuts = resourceOutService.selectResourceOutByProjectId(projectId.getProjectId(), Integer.valueOf(ResourceOutEnum.MYSQL.getCode()));
        List<ExternalDataSourceRDB> result = resourceOuts.stream().map(o -> {
            ExternalDataSourceRDB rdb = new ExternalDataSourceRDB();
            rdb.setConnUrl(o.getConnUrl());
            rdb.setConnPass(o.getConnPass());
            rdb.setConnUser(o.getConnUser());
            rdb.setName(o.getResourceName());
            rdb.setId(o.getResourceOutId());
            return rdb;
        }).collect(Collectors.toList());
        return result;
    }

    @RequestMapping("/rdb")
    public ResultVO<List<ExternalDataSourceRDB>> listRDBsWithResultVO(@RequestBody ProjectIdParam projectId) {
        try {
            List<ExternalDataSourceRDB> rdbs = this.listRDBs(projectId);
            return ResultVO.success(rdbs);
        } catch (RuntimeException e) {
            log.error("get external rdb error", e);
            return ResultVO.error();
        }
    }

    @RequestMapping("/table")
    public ResultVO<List<ExternalDataSourceRDBTableVO>> listRDBTablesWithResultVO(@RequestBody ExternalDataSourceRDBParam param) {
        try {
            List<ExternalDataSourceRDBTable> externalDataSourceRDBTables = externalDataSourceService.listRDBTables(param.getUrl(), param.getUserName(), param.getPassword());
            List<ExternalDataSourceRDBTableVO> result = ExternalDataSourceRDBTableVOMapper.of(externalDataSourceRDBTables);
            return ResultVO.success(result);
        } catch (ServiceException e) {
            log.error("get externalData source rdb table error ", e);
            return ResultVO.error(e);
        }
    }

    @RequestMapping("/field")
    public ResultVO<List<ExternalDataSourceRDBFieldVO>> listRDBFieldsWithResultVO(@RequestBody ExternalDataSourceRDBFieldParam param) {
        try {
            List<ExternalDataSourceRDBField> externalDataSourceRDBFields = externalDataSourceService.listRDBFields(param.getUrl(), param.getUserName(), param.getPassword(), param.getTable());
            List<ExternalDataSourceRDBFieldVO> result = ExternalDataSourceRDBFieldVOMapper.of(externalDataSourceRDBFields);
            return ResultVO.success(result);
        } catch (ServiceException e) {
            log.error("get externalData source rdb fields error", e);
            return ResultVO.error(e);
        }
    }

    @RequestMapping("/csvfield")
    public ResultVO<List<ExternalDataSourceCSVFieldVO>> listCSVFieldsWithResultVO(@RequestBody ExternalDataSourceCSVFieldParam param) {
        try {
            List<ExternalDataSourceCSVField> externalDataSourceCSVFields = externalDataSourceService.listCSVFields(param.getCsvFilePath(), param.getDwUserName(), param.getHasTitle());
            List<ExternalDataSourceCSVFieldVO> result = ExternalDataSourceCSVFieldVOMapper.of(externalDataSourceCSVFields);
            return ResultVO.success(result);
        } catch (ServiceException e) {
            log.error("get externalData source csv fields error", e);
            return ResultVO.error(e);
        }
    }

    @RequestMapping("/csvsample")
    public ResultVO<String> getCSVSampleWithResultVO(@RequestBody ExternalDataSourceCSVFieldParam param) {
        try {
            String result = externalDataSourceService.getCSVSample(param.getCsvFilePath(), param.getDwUserName(), param.getHasTitle(), param.getDisplayLineCount());
            return ResultVO.success(result);
        } catch (ServiceException e) {
            log.error("get externalData source csv fields error", e);
            return ResultVO.error(e);
        }
    }
    
    @RequestMapping("/jsonfield")
    public ResultVO<List<ExternalDataSourceJsonFieldVO>> listJsonFieldsWithResultVO(@RequestBody ExternalDataSourceJsonFieldParam param) {
        try {
            List<ExternalDataSourceJsonField> externalDataSourceJsonFields = externalDataSourceService.listJsonFields(param.getFilePath(), param.getDwUserName());
            List<ExternalDataSourceJsonFieldVO> result = ExternalDataSourceJsonFieldVOMapper.of(externalDataSourceJsonFields);
            return ResultVO.success(result);
        } catch (ServiceException e) {
            log.error("get externalData source json fields error", e);
            return ResultVO.error(e);
        }
    }

    @RequestMapping("/jsonsample")
    public ResultVO<String> getJsonSampleWithResultVO(@RequestBody ExternalDataSourceJsonFieldParam param) {
        try {
            String result = externalDataSourceService.getJsonSample(param.getFilePath(), param.getDwUserName(), param.getDisplayLineCount());
            return ResultVO.success(result);
        } catch (ServiceException e) {
            log.error("get externalData source json fields error", e);
            return ResultVO.error(e);
        }
    }
}
