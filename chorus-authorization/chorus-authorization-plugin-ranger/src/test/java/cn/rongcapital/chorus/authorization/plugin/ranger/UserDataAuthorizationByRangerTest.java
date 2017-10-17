package cn.rongcapital.chorus.authorization.plugin.ranger;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cn.rongcapital.chorus.authorization.api.UserDataAuthorization;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataDW;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataType;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationPerm;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationUser;
import cn.rongcapital.chorus.authorization.plugin.ranger.common.BasicSpringTest;

/**
 * UserDataAuthorizationByRanger Tester.
 *
 * @author shicheng
 * @version 1.0
 * @since <pre>三月 24, 2017</pre>
 */
public class UserDataAuthorizationByRangerTest extends BasicSpringTest{
    @Autowired
    @Qualifier("userDataAuthorizationByRanger")
    UserDataAuthorization authorization;
    /**
     * Method: authorizationAdd(AuthorizationUser user, AuthorizationData authorizationData)
     */
    @Test
    public void testAuthorizationAdd() throws Exception {
        AuthorizationDataDW authorizationDataHive = new AuthorizationDataDW();
        authorizationDataHive.setAuthorizationName("hive-hhl-test-12");
        List<String> dbs = new ArrayList<>();
        dbs.add("chorus_hhltest01");
        authorizationDataHive.setDatabases(dbs);
        List<String> tables = new ArrayList<>();
        tables.add("t_hhl_test_02");
        authorizationDataHive.setTables(tables);
        List<String> clumns = new ArrayList<>();
        clumns.add("*");
        authorizationDataHive.setColumns(clumns);
        authorizationDataHive.setAuthorizationDescription("Hive hhl Test Policy");
        authorizationDataHive.setAuthorizationRepositoryType(AuthorizationDataType.DW);
        authorizationDataHive.setEnabled(true);

        List<AuthorizationPerm> perms = new ArrayList<>();
        AuthorizationPerm perm = new AuthorizationPerm();
        List<String> pperms = new ArrayList<>();
        pperms.add("select");
        perm.setPermPermList(pperms);
        List<String> users = new ArrayList<>();
        users.add("shicheng");
        perm.setPermUserList(users);
        perms.add(perm);
        authorizationDataHive.setAuthorizationPermMapList(perms);

        AuthorizationUser user = new AuthorizationUser();
        user.setAuthorizationUserName("admin");
        user.setAuthorizationPassword("admin");
        authorizationDataHive.setAuthorizationUser(user);

        System.out.println("\n" + authorization.authorizationAdd(authorizationDataHive));
    }

    /**
     * Method: authorizationUpdate(AuthorizationUser user, AuthorizationData authorization)
     */
    @Test
    public void testAuthorizationUpdate() throws Exception {

        AuthorizationDataDW authorizationDataHive = new AuthorizationDataDW();
        authorizationDataHive.setAuthorizationName("hive-shicheng-test-1111");
        authorizationDataHive.setAuthorizationDescription("Hive ShiCheng Test Policy");
        authorizationDataHive.setAuthorizationRepositoryType(AuthorizationDataType.DW);
        authorizationDataHive.setAuthorizationId("38");

        List<AuthorizationPerm> perms = new ArrayList<>();
        AuthorizationPerm perm = new AuthorizationPerm();
        perms.add(perm);
        authorizationDataHive.setAuthorizationPermMapList(perms);

        AuthorizationUser user = new AuthorizationUser();
        user.setAuthorizationUserName("admin");
        user.setAuthorizationPassword("admin");

        System.out.println(authorization.authorizationUpdate(authorizationDataHive));
    }

    /**
     * Method: authorizationRemove(AuthorizationUser user, AuthorizationData authorization)
     */
    @Test
    public void testAuthorizationRemove() throws Exception {

        AuthorizationUser user = new AuthorizationUser();
        user.setAuthorizationUserName("admin");
        user.setAuthorizationPassword("admin");

        AuthorizationDataDW authorizationDataHive = new AuthorizationDataDW();
        authorizationDataHive.setAuthorizationId("37");

        authorization.authorizationRemove(authorizationDataHive);
    }

    /**
     * Method: authorizationGet(AuthorizationUser user, AuthorizationData authorization)
     */
    @Test
    public void testAuthorizationSearch() throws Exception {
        AuthorizationDataDW authorizationDataHive = new AuthorizationDataDW();
        authorizationDataHive.setAuthorizationName("hive-shicheng-dsaf-111111");
        authorization.authorizationGet(authorizationDataHive);

    }

    /**
     * Method: authorizationGet(AuthorizationUser user, AuthorizationData authorization)
     */
    @Test
    public void testAuthorizationGet() throws Exception {
        AuthorizationDataDW authorizationDataHive = new AuthorizationDataDW();
        authorizationDataHive.setAuthorizationName("hive-shicheng-dsaf-111111");
        authorization.authorizationGet(authorizationDataHive);

    }
    
    /**
     * 
     * @author yunzhong
     * @time 2017年9月11日下午2:25:44
     */
    @Test
    public void testDisable() {
        boolean disabled = false;
        try {
            disabled = authorization.disablePolicy("602");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
        assertTrue(disabled);
    }
} 
