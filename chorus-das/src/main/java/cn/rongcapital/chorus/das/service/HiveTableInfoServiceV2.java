package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.TableInfoV2;

import java.util.List;
import java.util.Map;

/**
 * Created by alan on 11/23/16.
 */
public interface HiveTableInfoServiceV2 {
    
    /**
     * 从Hive中读取样例数据
     *
     * @param tableEntityGuid 表Id
     * @param size            样例数据大小
     * @return 样例数据列表
     */
    List<Map<String, Object>> getSampleDataFromHive(String tableEntityGuid, Integer size);
    
    /**
     * 在Hive中创建数据表
     *
     * @param tableInfo      数据表信息
     * @param columnInfoList 数据表字段信息列表
     * @return 创建成功与否
     */
    Boolean createTable(TableInfoV2 tableInfo, List<ColumnInfoV2> columnInfoList);
    
    /**
     * 在Hive中创建数据表
     *
     * @param projectInfo 项目信息
     * @return 创建成功与否
     */
    Boolean createDb(ProjectInfo projectInfo, String userName);
    
    /**
     * 给Hive表增加列
     *
     * @param tableInfo
     * @param addedColumnList
     * @return
     * @author li.hzh
     */
    Boolean alterTable(TableInfoV2 tableInfo, List<ColumnInfoV2> addedColumnList);
    
    /**
     * 删除hive表，及hive表的column
     * 
     * @param tableName
     * @author yunzhong
     * @time 2017年9月6日上午10:13:40
     */
    Boolean delete(String tableName);
}
