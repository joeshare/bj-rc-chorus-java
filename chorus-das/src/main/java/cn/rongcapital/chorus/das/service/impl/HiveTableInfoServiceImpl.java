package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.ProjectUtil;
import cn.rongcapital.chorus.das.entity.ColumnInfo;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.TableInfo;
import cn.rongcapital.chorus.das.service.ColumnInfoService;
import cn.rongcapital.chorus.das.service.HiveTableInfoService;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.das.service.ProjectMemberMappingService;
import cn.rongcapital.chorus.das.service.TableInfoService;
import cn.rongcapital.chorus.hive.jdbc.HiveClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by alan on 11/23/16.
 */
@Slf4j
@Service(value = "HiveTableInfoService")
public class HiveTableInfoServiceImpl implements HiveTableInfoService {

    @Autowired
    private TableInfoService tableInfoService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private ColumnInfoService columnInfoService;
    @Autowired
    private ProjectMemberMappingService projectMemberMappingService;

    public static final String DEFAULT_FILE_FORMAT                               = "TEXTFILE";
    public static final String DEFAULT_HIVE_UESR                                 = "hive";

    @Override
    public List<Map<String, Object>> getSampleDataFromHive(Long tableId, Integer size) {
        TableInfo tableInfo = tableInfoService.selectByID(tableId);
        if (tableInfo == null) {
            throw new ServiceException(StatusCode.TABLE_NOT_EXISTS);
        }
        ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(tableInfo.getProjectId());
        if (projectInfo == null) {
            throw new ServiceException(StatusCode.PROJECT_NOT_EXISTS);
        }

        return HiveClient.getInstance().execute(projectInfo.getUserName(), projectInfo.getProjectCode(),
                connection -> {
                    String sql = String.format("select * from %s limit %d",
                            assembleDbAndTablePath(projectInfo, tableInfo), size);
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ResultSet resultSet = ps.executeQuery();
                    List<String> columnList = columnInfoService.selectColumnInfo(tableId).stream()
                            .map(ColumnInfo::getColumnName)
                            .collect(Collectors.toList());
                    List<Map<String, Object>> innerRes = new ArrayList<>();
                    while (resultSet.next()) {
                        Map<String, Object> tempMap = new LinkedHashMap<>();
                        for (String s : columnList) {
                            tempMap.put(s, resultSet.getObject(s));
                        }
                        innerRes.add(tempMap);
                    }
                    return innerRes;
                }, null);
    }

    @Transactional
    @Override
    public Boolean createTable(TableInfo tableInfo, List<ColumnInfo> columnInfoList) {
        ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(tableInfo.getProjectId());
        if (projectInfo == null) {
            throw new ServiceException(StatusCode.PROJECT_NOT_EXISTS);
        }
        Predicate<ColumnInfo> isPartitionKey = columnInfo ->
                columnInfo.getIsPartitionKey() != null && columnInfo.getIsPartitionKey() == 1;
        Stream<ColumnInfo> partitionColumnInfoList = columnInfoList.stream().filter(isPartitionKey);
        Stream<ColumnInfo> normalColumnInfoList = columnInfoList.stream().filter(isPartitionKey.negate());
        String sql = " CREATE TABLE %s ( %s ) PARTITIONED BY (%s) " +
                     " STORED AS %s LOCATION '" + ProjectUtil.hiveTableLocation(projectInfo.getProjectCode(), tableInfo.getTableName()) + "' ";
        String partitionColumnSql = partitionColumnInfoList
                .map(c -> "`" + c.getColumnName() + "` " + c.getColumnType() + " COMMENT '" + c.getColumnDesc() + "'")
                .collect(Collectors.joining(","));
        String normalColumnSql = normalColumnInfoList
                .map(c -> "`" + c.getColumnName() + "` " + c.getColumnType() + " COMMENT '" + c.getColumnDesc() + "'")
                .collect(Collectors.joining(","));
        final String finalSql = String.format(sql,
                assembleDbAndTablePath(projectInfo, tableInfo),
                normalColumnSql, partitionColumnSql,
                DEFAULT_FILE_FORMAT);
        log.info("creating table in hive,\n sql: {}", finalSql);
        return execute(finalSql, DEFAULT_HIVE_UESR, projectInfo.getProjectCode());
    }

    @Transactional
    @Override
    public Boolean createDb(ProjectInfo projectInfo, String userName) {
        String sql = " CREATE DATABASE %s ";
        final String dbName = assembleDbName(projectInfo.getProjectCode());
        final String finalSql = String.format(sql, dbName);
        log.info("creating db in hive,\n sql: {}", finalSql);
        return execute(finalSql, DEFAULT_HIVE_UESR, projectInfo.getProjectCode());
    }

    @Transactional
    Boolean execute(String finalSql, String userName, String queueName) {
        HiveClient.getInstance().executeHiveSqlNoRes(userName, queueName, finalSql, null);
        return true;
    }

    private String assembleDbAndTablePath(ProjectInfo projectInfo, TableInfo tableInfo) {
        return String.format("%s.%s", assembleDbName(projectInfo), tableInfo.getTableName());
    }

    private String assembleDbName(ProjectInfo projectInfo) {
        return assembleDbName(projectInfo.getProjectCode());
    }

    private String assembleDbName(String projectCode) {
        return String.format("chorus_%s", projectCode);
    }

}
