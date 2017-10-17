package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ColumnInfo;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.TableInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alan on 11/23/16.
 */
public interface HiveTableInfoService {

    /**
     * 从Hive中读取样例数据
     *
     * @param tableId 表Id
     * @param size    样例数据大小
     * @return 样例数据列表
     */
    List<Map<String, Object>> getSampleDataFromHive(Long tableId, Integer size);

    /**
     * 在Hive中创建数据表
     *
     * @param tableInfo      数据表信息
     * @param columnInfoList 数据表字段信息列表
     * @return 创建成功与否
     */
    Boolean createTable(TableInfo tableInfo, List<ColumnInfo> columnInfoList);

    /**
     * 在Hive中创建数据表
     *
     * @param projectInfo 项目信息
     * @return 创建成功与否
     */
    Boolean createDb(ProjectInfo projectInfo, String userName);

}
