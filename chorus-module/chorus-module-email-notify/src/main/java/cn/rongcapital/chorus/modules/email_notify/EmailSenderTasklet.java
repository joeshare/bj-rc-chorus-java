package cn.rongcapital.chorus.modules.email_notify;

import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import cn.rongcapital.chorus.modules.utils.mail.MailSender;
import cn.rongcapital.chorus.modules.utils.retry.SimpleTasklet;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
public class EmailSenderTasklet extends SimpleTasklet {

    private String title;
    private String content;
    private String formatedContent;
    private String from;

    private static final String QUERY_ALL_MANAGERS = "SELECT u .* FROM t_user AS u "
            + "INNER JOIN project_member_mapping map ON u.user_id = map.user_id AND map.role_id IN (4, 5) "
            + "INNER JOIN project_info p ON map.project_id = p.project_id AND p.status_code = 1205 "
            + "GROUP BY u.user_id";
    
    private static final String TITLE_PRE = "[TEST]";

    @Getter
    @Setter
    public static class ProjectManager {
        private String id;
        private String name;
        private String email;
    }

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MailSender sender;

    @Autowired
    private Converter converter;

    @Override
    public RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) throws Exception {
        log.info("notify all managers start=================================>>");
        try {
            if (this.formatedContent == null)
                this.formatedContent = this.converter.convert(URLDecoder.decode(this.content,"UTF-8"));
            if(StringUtils.isEmpty(this.title)){
                log.warn("title is empty to send email.");
                return RepeatStatus.FINISHED;
            }
            List<ProjectManager> managers = null;
            if (this.title.startsWith(TITLE_PRE)) {
                managers = new ArrayList<>();
                ProjectManager manager = new ProjectManager();
                manager.setEmail("dp@rongcapital.cn");
                manager.setId("dp_id");
                manager.setName("dp_group");
                managers.add(manager);
            } else {
                managers = getManagers();
            }
            if (CollectionUtils.isEmpty(managers)) {
                log.warn("There are no managers or owners of project.");
                return RepeatStatus.FINISHED;
            }
            log.info("get all users count {}", managers.size());
            sendEmail(managers);
        } catch (Exception e) {
            log.error("Failed to get all managers info.", e);
            throw e;
        }
        log.info("notify all managers end <<=================================");
        return RepeatStatus.FINISHED;
    }

    /**
     * @param managers
     * @throws MessagingException
     * @author yunzhong
     * @time 2017年9月19日下午3:59:16
     */
    public void sendEmail(List<ProjectManager> managers) throws MessagingException {
        for (ProjectManager manager : managers) {
            try {
                log.info("send email to {}", manager.getEmail());
                sender.send(this.title, this.formatedContent, from, manager.getEmail());
            } catch (Exception e) {
                log.error("Failed to send email {} user {}", manager.getEmail(), manager.getName());
                log.error(e.getLocalizedMessage(), e);
            }
        }

    }

    /**
     * @return
     * @throws SQLException
     * @author yunzhong
     * @time 2017年9月19日下午2:44:29
     */
    public List<ProjectManager> getManagers() throws SQLException {
        final List<ProjectManager> managers = SQLUtil.query(dataSource, new ResultSolver<List<ProjectManager>>() {

            @Override
            public List<ProjectManager> result(ResultSet rs) throws SQLException {
                List<ProjectManager> result = new ArrayList<>();
                while (rs.next()) {
                    ProjectManager manager = new ProjectManager();
                    manager.setEmail(rs.getString("email"));
                    manager.setId(rs.getString("user_id"));
                    manager.setName(rs.getString("user_name"));
                    result.add(manager);
                }
                return result;
            }
        }, QUERY_ALL_MANAGERS);
        return managers;
    }
}
