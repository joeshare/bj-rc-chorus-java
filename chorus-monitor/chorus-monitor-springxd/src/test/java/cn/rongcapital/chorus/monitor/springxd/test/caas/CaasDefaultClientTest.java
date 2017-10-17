package cn.rongcapital.chorus.monitor.springxd.test.caas;

import static org.junit.Assert.*;

import java.util.List;

import cn.rongcapital.chorus.das.entity.caas.CaasUser;
import cn.rongcapital.chorus.das.service.impl.CaasDefaultClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.rongcapital.chorus.monitor.springxd.test.util.BasicSpringTest;

public class CaasDefaultClientTest extends BasicSpringTest {

    @Autowired
    private CaasDefaultClient client;

    /**
     * 
     * @author yunzhong
     * @version
     * @since 2017年5月19日
     */
    @Test
    public void testAuth() {
        try {
            client.reAuth();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
    }

    /**
     * 
     * @author yunzhong
     * @version
     * @since 2017年5月19日
     */
    @Test
    public void testgetUserEmail() {
        try {
            CaasUser user = client.getUser("wangyunzhong");
            assertNotNull(user);
            assertNotNull(user.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
    }

    /**
     * 
     * @author yunzhong
     * @time 2017年8月21日下午3:22:08
     */
    @Test
    public void testgetProjectUsers() {
        String projectCode = "yunProject";
        String role = "项目管理员";
        List<CaasUser> projectUsers = null;
        try {
            projectUsers = client.getProjectUsers(projectCode, role);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
        assertNotNull(projectUsers);
        assertTrue(projectUsers.size() == 2);
    }
}
