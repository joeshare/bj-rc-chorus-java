package cn.rongcapital.chorus.modules.email_notify;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class ,PropertyPlaceholderAutoConfiguration.class})
@Configuration
@TestPropertySource({ "classpath:application.properties" })
public class EmailSenderTaskletTest {

    @Autowired
    private EmailSenderTasklet tasklet;

    /**
     * 
     * @author yunzhong
     * @time 2017年9月19日下午3:02:24
     */
    @Test
    public void testgetManagers() {
        List<EmailSenderTasklet.ProjectManager> managers = null;
        try {
            managers = tasklet.getManagers();
        } catch (SQLException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
        assertNotNull(managers);
        assertTrue(managers.size() >0);
        log.info("managers size {}", managers.size());
        Set<String> emails = new HashSet<>();
        for (EmailSenderTasklet.ProjectManager manager : managers) {
            assertNotNull(manager.getEmail());
            if (emails.contains(manager.getEmail())) {
                fail("conflict email [" + manager.getEmail() + "]");
            }
            emails.add(manager.getEmail());
        }
    }
    
    /**
     * 
     * @author yunzhong
     * @time 2017年9月19日下午4:25:55
     */
    @Test
    public void testSendEmail(){
        List<EmailSenderTasklet.ProjectManager> managers = new ArrayList<>();
        EmailSenderTasklet.ProjectManager manager = new EmailSenderTasklet.ProjectManager();
        manager.setEmail("wangyunzhong@rongcapital.cn");
        manager.setId("20209");
        manager.setName("wangyunzhong");
        managers.add(manager);
        
        EmailSenderTasklet.ProjectManager managerUnkown = new EmailSenderTasklet.ProjectManager();
        managerUnkown.setEmail("unkown@rongcapital.cn");
        managerUnkown.setId("233245234");
        managerUnkown.setName("unkown");
        managers.add(managerUnkown);
        
        try {
            tasklet.sendEmail(managers );
        } catch (MessagingException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
    }
}
