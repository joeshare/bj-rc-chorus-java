package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.service.HiveTableInfoServiceV2;
import cn.rongcapital.chorus.das.util.BasicSpringTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Date;

/**
 * @author li.hzh
 * @date 2017-08-08 14:27
 */
public class HiveTableInfoServiceV2Test extends BasicSpringTest {
    
    @Autowired
    private HiveTableInfoServiceV2 hiveTableInfoServiceV2;
    
    @Test
    public void testAlterHiveTable() {
//        String tableInfoId = "dd0d4746-28bd-4c62-8173-b40284ad284b";
        String tableInfoId = "43437876-83a6-4713-8978-cf2a66dfebb0";
        TableInfoV2 tableInfo = getTableInfo(tableInfoId, 222655L, "lhzbbbbbbb");
        ColumnInfoV2 columnInfo = getColumnInfo(tableInfoId, null, "lllhhhzzz", "VARCHAR", "200", (byte) 0, 5);
        Boolean addColumn = hiveTableInfoServiceV2.alterTable(tableInfo, Collections.singletonList(columnInfo));
        System.out.println(addColumn);
    }
    
    private TableInfoV2 getTableInfo(String tableInfoId, Long projectId, String tableName) {
        TableInfoV2 tableInfo = new TableInfoV2();
        tableInfo.setTableInfoId(tableInfoId);
        tableInfo.setProjectId(projectId);
        tableInfo.setTableCode(tableName + "-01");
        tableInfo.setTableName(tableName);
        tableInfo.setDataField("用户域01");
        tableInfo.setTableType("基础表01");
        tableInfo.setIsSnapshot("1");
        tableInfo.setUpdateFrequence("实时01");
        tableInfo.setSla("sla01");
        tableInfo.setSecurityLevel("");
        tableInfo.setIsOpen((byte) 1);
        tableInfo.setTableDes("测试添加列表01");
        tableInfo.setCreateTime(new Date());
        tableInfo.setUpdateTime(null);
        tableInfo.setStatusCode("1311");
        return tableInfo;
    }
    
    private ColumnInfoV2 getColumnInfo(String tableInfoId, String columnInfoId, String columnInfoName, String columnType, String columnLength, byte isPartitionKey, int order) {
//        String columnName = randomAlphabetic(6);
        ColumnInfoV2 columnInfo = new ColumnInfoV2();
        columnInfo.setTableInfoId(tableInfoId);
        columnInfo.setColumnInfoId(columnInfoId);
        columnInfo.setColumnName(columnInfoName);
        columnInfo.setColumnDesc(columnInfoName);
        columnInfo.setColumnType(columnType);
        columnInfo.setColumnLength(columnLength);
        columnInfo.setColumnPrecision(null);
        columnInfo.setSecurityLevel("A");
        columnInfo.setIsKey((byte) 1);
        columnInfo.setIsRefKey((byte) 1);
        columnInfo.setIsIndex((byte) 1);
        columnInfo.setIsNull((byte) 1);
        columnInfo.setIsPartitionKey(isPartitionKey);
        columnInfo.setCreateTime(new Date());
        columnInfo.setUpdateTime(null);
        columnInfo.setStatusCode("1311");
        columnInfo.setColumnOrder(order);
        return columnInfo;
    }
    
    /**
     * 
     * @author yunzhong
     * @time 2017年9月11日下午2:05:58
     */
    @Test
    public void testDelete(){
        String tableName = "chorus_yunzhongtestnew.newtable";
        final Boolean deleted = hiveTableInfoServiceV2.delete(tableName );
        assertNotNull(deleted);
        assertTrue(deleted);
    }
}
