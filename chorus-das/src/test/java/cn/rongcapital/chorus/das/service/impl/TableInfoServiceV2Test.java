package cn.rongcapital.chorus.das.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import cn.rongcapital.chorus.das.entity.TableInfoDOV2;
import cn.rongcapital.chorus.das.entity.TableInfoV2;
import cn.rongcapital.chorus.das.service.TableInfoServiceV2;
import cn.rongcapital.chorus.das.util.BasicSpringTest;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Athletics on 2017/7/28.
 */
@Slf4j
public class TableInfoServiceV2Test extends BasicSpringTest{

    @Autowired
    private TableInfoServiceV2 tableInfoServiceV2;

    @Test
    public void selectByIDTest(){
        String guid = "0a08b136-d446-4563-8836-3ecd68211a9d";
        TableInfoV2 tableInfo = tableInfoServiceV2.selectByID(guid);
        System.out.println(tableInfo.getTableInfoId());
        assertThat(tableInfo).isNotNull();
    }

    @Test
    public void listAllTableInfo01Test(){
        Long projectId = 222675L;
        int pageNum = 1;
        int pageSize=20;
        List<TableInfoV2> list = tableInfoServiceV2.listAllTableInfo(projectId, pageNum, pageSize);
        System.out.println("list.size = " + list.size());
        assertThat(list).isNotNull();
    }

    @Test
    public void createTableTest(){
        TableInfoV2 tableInfo = getTableInfo("111_column",222649L,"tableInfoServcieV2Test01");
        ColumnInfoV2 column1 = getColumnInfo("tableInfoServcieV2Test01", "cid_26","column26", "String", "20", (byte) 0);
        ColumnInfoV2 column2 = getColumnInfo("tableInfoServcieV2Test01", "cid_27","column27","String","20",(byte)0);
        ColumnInfoV2 column3 = getColumnInfo("tableInfoServcieV2Test01", "cid_28", "p_date", "String", "20", (byte) 1);
        List<ColumnInfoV2> columnInfoList = new ArrayList<>();
        columnInfoList.add(column1);
        columnInfoList.add(column2);
        columnInfoList.add(column3);

        tableInfoServiceV2.createTable(tableInfo, columnInfoList);
    }

    @Test
    public void listAllTablesTest(){
        int pageNum = 1;
        int pageSize = 20;
        List<TableInfoDOV2> list = tableInfoServiceV2.listAllTables(pageNum, pageSize);
        if(CollectionUtils.isNotEmpty(list)){
            System.out.println("tableInfoIds : " + list.get(0).getTableInfoId());
        }
        assertThat(list).isNotNull();
    }

    @Test
    public void listAllTableNameTest(){
        List<String> list = tableInfoServiceV2.listAllTableName(222675L);
        if(CollectionUtils.isNotEmpty(list)){
            System.out.println("list.size = " + list.size() + "// list.get 0  = " + list.get(0));
        }
        assertThat(list).isNotNull();
    }

    @Test
    public void countTablesTest(){
        long result = tableInfoServiceV2.countTables(222675L);
        System.out.println(result);
        assertThat(result).isNotNull();
    }

    private AtlasEntity getDbEntity(String typeName,String name,String unique){
        AtlasEntity atlasEntity = new AtlasEntity(typeName);
        atlasEntity.setAttribute("name", name);
        atlasEntity.setAttribute("description", "test database entity");
        atlasEntity.setAttribute("locationUrl", "");
        atlasEntity.setAttribute("owner", "user1");
        atlasEntity.setAttribute("qualifiedName",unique);
        atlasEntity.setAttribute("unique",unique);
        atlasEntity.setAttribute("createUser", "ymjtest");
        atlasEntity.setAttribute("createUserId", "111");
        atlasEntity.setAttribute("project", "aa");
        atlasEntity.setAttribute("projectId",111L);


        atlasEntity.setAttribute("clusterName", "");
        atlasEntity.setAttribute("createTime", new Date());
        return atlasEntity;
    }

    private TableInfoV2 getTableInfo(String tableInfoId,Long projectId,String tableName){
        TableInfoV2 tableInfo = new TableInfoV2();
        tableInfo.setTableInfoId(tableInfoId);
        tableInfo.setProjectId(projectId);
        tableInfo.setTableCode(tableName + "-01");
        tableInfo.setTableName(tableName);
        tableInfo.setDataField("用户域");
        tableInfo.setTableType("基础表");
        tableInfo.setIsSnapshot("0");
        tableInfo.setUpdateFrequence("实时");
        tableInfo.setSla("sla");
        tableInfo.setSecurityLevel("A");
        tableInfo.setIsOpen((byte) 0);
        tableInfo.setTableDes("标签实体关系表");
        tableInfo.setCreateTime(new Date());
        tableInfo.setUpdateTime(null);
        tableInfo.setStatusCode("1310");
        return tableInfo;
    }

    private ColumnInfoV2 getColumnInfo(String tableInfoId,String columnInfoId,String columnInfoName,String columnType,String columnLength,byte isPartitionKey){
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
        columnInfo.setIsKey((byte) 0);
        columnInfo.setIsRefKey((byte) 0);
        columnInfo.setIsIndex((byte) 0);
        columnInfo.setIsNull((byte) 0);
        columnInfo.setIsPartitionKey(isPartitionKey);
        columnInfo.setCreateTime(new Date());
        columnInfo.setUpdateTime(null);
        columnInfo.setStatusCode("1311");
        return columnInfo;
    }
    
    @Test
    public void testDelete(){
        String tableInfoId = "97a4e758-45ef-4dd9-b17c-17b96e579f4b";
        try {
            tableInfoServiceV2.delete(tableInfoId );
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
        log.info("success to delete.");
    }
    
    @Test
    public void testlistTable() throws AtlasServiceException {
        final List<TableInfoDOV2> exists = tableInfoServiceV2.searchByTableNameAndProjectNameAndProjectCode("undelete", 1, 200);
        assertNotNull(exists);
        assertEquals(1,exists.size());
        final List<TableInfoDOV2> deleted = tableInfoServiceV2.searchByTableNameAndProjectNameAndProjectCode("testdelete1", 1, 200);
        assertNotNull(deleted);
        assertEquals(0, deleted.size());
    }
    
    @Test
    public void testlistAllTableInfo(){
        Long projectId = 222675L;
        final List<TableInfoDOV2> tables = tableInfoServiceV2.listAllTableInfo(projectId );
        assertNotNull(tables);
        assertEquals(7,tables.size());
    }
    
    @Test
    public void testtablesOfProject(){
        List<Long> projectIds = new ArrayList<>();
        projectIds.add(222649L);
        projectIds.add(222675L);
        final Map<String, TableInfoV2> tables = tableInfoServiceV2.tablesOfProject(projectIds );
        assertNotNull(tables);
        assertEquals(23, tables.size());
    }
}
