package cn.rongcapital.chorus.authorization.plugin.ranger;

import cn.rongcapital.chorus.authorization.api.data.*;
import cn.rongcapital.chorus.authorization.plugin.ranger.data.RangerDW;
import cn.rongcapital.chorus.authorization.plugin.ranger.data.RangerPerm;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hhlfl on 2017-9-21.
 */
public class RangerUtilsTest {
    @Test
    public void getRangerParamsToDW() throws Exception {

        RangerUtils rangerUtils = new RangerUtils();
        AuthorizationDataDW authorizationDataDW = new AuthorizationDataDW();
        List<String> databases = new ArrayList<>();
        databases.add("db1");
        databases.add("db2");
        List<String> tables = new ArrayList<String>();
        tables.add("tab1");
        tables.add("tab2");
        tables.add("tab3");
        List<String> columns = new ArrayList<String>();
        columns.add("col1");
        columns.add("col2");
        columns.add("col3");
        authorizationDataDW.setColumns(columns);
        authorizationDataDW.setAuthorizationName("test001");
        authorizationDataDW.setTables(tables);
        authorizationDataDW.setDatabases(databases);
        List<AuthorizationPerm> authorizationPerms = new ArrayList<>();
        AuthorizationPerm authorizationPerm = new AuthorizationPerm();
        List<String> users = new ArrayList<>();
        users.add("user1");
        users.add("user2");
        authorizationPerm.setPermUserList(users);
        List<String> groups = new ArrayList<>();
        groups.add("g1");
        groups.add("g2");
        groups.add("g3");
        authorizationPerm.setPermGroupList(groups);
        List<String> perms = new ArrayList<>();
        perms.add("select");
        perms.add("update");
        authorizationPerm.setPermPermList(perms);
        authorizationPerms.add(authorizationPerm);
        authorizationDataDW.setAuthorizationPermMapList(authorizationPerms);
        String json = rangerUtils.getRangerParamsToDW(authorizationDataDW);
        assertNotNull(json);
        RangerDW origin = JSON.parseObject(json,RangerDW.class);
        assertEquals(origin.getTables(),"tab1,tab2,tab3");
        assertEquals(origin.getDatabases(),"db1,db2");
        assertEquals(origin.getColumns(),"col1,col2,col3");
        assertEquals(origin.getPolicyName(),authorizationDataDW.getAuthorizationName());
        assertEquals(origin.getRepositoryType(), AuthorizationDataType.DW.getValue());
        assertEquals(origin.getColumnType(), DWTCType.INCLUDE.getValue());
        assertEquals(origin.getTableType(), DWTCType.INCLUDE.getValue());
        assertEquals(origin.getIsAuditEnabled(), true);
        assertEquals(origin.getIsEnabled(), authorizationDataDW.isEnabled());
        List<AuthorizationPerm> authorizationPermMapList = authorizationDataDW.getAuthorizationPermMapList();
        assertEquals(origin.getPermMapList().size(), authorizationPermMapList.size());
        AuthorizationPerm authorizationPerm1 = authorizationPermMapList.get(0);
        RangerPerm rangerPerm = origin.getPermMapList().get(0);
        assertEquals(rangerPerm.getGroupList().toString(), authorizationPerm1.getPermGroupList().toString());
        assertEquals(rangerPerm.getPermList().toString(), authorizationPerm1.getPermPermList().toString());
        assertEquals(rangerPerm.getUserList().toString(), authorizationPerm1.getPermUserList().toString());

    }

    @Test
    public void trim()throws Exception{
        RangerUtils rangerUtils = new RangerUtils();
        AuthorizationDataDW authorizationDataDW = new AuthorizationDataDW();
        List<String> databases = new ArrayList<>();
        databases.add("db1");
        databases.add("db2");
        List<String> tables = new ArrayList<String>();
        tables.add("tab1");
        tables.add("tabl2");
        tables.add("table3");
        List<String> columns = new ArrayList<String>();
        columns.add("col1");
        columns.add("col2");
        columns.add("col3");
        authorizationDataDW.setColumns(columns);
        authorizationDataDW.setAuthorizationName("test001");
        authorizationDataDW.setTables(tables);
        authorizationDataDW.setDatabases(databases);
        authorizationDataDW.setAuthorizationId("test");


        AuthorizationDataDW authorizationDataDW1 = new AuthorizationDataDW();
        databases = new ArrayList<>();
        databases.add(" db1 ");
        databases.add("db2");
        tables = new ArrayList<String>();
        tables.add(" tab1 ");
        tables.add("tabl2");
        tables.add("table3");
        columns = new ArrayList<String>();
        columns.add(" col1 ");
        columns.add("col2");
        columns.add("col3");
        authorizationDataDW1.setColumns(columns);
        authorizationDataDW1.setAuthorizationName("  test001  ");
        authorizationDataDW1.setTables(tables);
        authorizationDataDW1.setDatabases(databases);
        authorizationDataDW1.setAuthorizationId("  test  ");

        rangerUtils.trim(authorizationDataDW1);
        assertEquals(authorizationDataDW.getColumns().get(0),authorizationDataDW1.getColumns().get(0));
        assertEquals(authorizationDataDW.getTables().get(0), authorizationDataDW1.getTables().get(0));
        assertEquals(authorizationDataDW.getDatabases().get(0), authorizationDataDW1.getDatabases().get(0));
        assertEquals(authorizationDataDW.getAuthorizationName(), authorizationDataDW1.getAuthorizationName());
        assertEquals(authorizationDataDW.getAuthorizationId(), authorizationDataDW1.getAuthorizationId());


        AuthorizationDataFS dataFS = new AuthorizationDataFS();
        dataFS.setResourceName("  /a/b /c ");
        AuthorizationDataFS dataFS1 = new AuthorizationDataFS();
        dataFS1.setResourceName("/a/b /c");
        rangerUtils.trim(dataFS);
        assertEquals(dataFS.getResourceName(), dataFS1.getResourceName());
    }

}