package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.util.JsonUtils;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.entity.caas.*;
import cn.rongcapital.chorus.das.service.CaasClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CaasDefaultClient implements CaasClient, InitializingBean {

    @Value("${caas.server.url}")
    private String serverURL;

    @Value("${caas.username}")
    private String userName;

    @Value("${caas.password}")
    private String plainPassword;

    @Value("${caas.app.code}")
    private String appCode;

    private String password;

    private HttpClient client;

    private static final String ADMIN_AUTH_URL = "/api/v1/admin/login";

    /**
     * 37为chorus在caas注册的app-code
     */
    private static String ADMIN_SEARCH_USER_URL = "/api/v1/admin/app/%s/user";

    private static String ADMIN_SEARCH_ROLES = "/api/v1/admin/app/%s/role";
    private static String ADMIN_SEARCH_ROLE_MEMEBERS = "/api/v1/admin/role/%s/user";

    /**
     * 认证
     *
     * @throws UnsupportedEncodingException
     * @throws Exception
     * @author yunzhong
     * @version
     * @since 2017年5月19日
     */
    public void reAuth() throws UnsupportedEncodingException, Exception {
        HttpPost post = new HttpPost(this.serverURL + ADMIN_AUTH_URL);
        post.addHeader("Content-Type", "application/json");

        CaasAuthInfo info = new CaasAuthInfo();
        info.setEmail(this.userName);
        info.setPassword(this.password);
        HttpEntity entity = new StringEntity(JsonUtils.convet2Json(info), ContentType.APPLICATION_JSON);
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        assertHttpResponse(response);
        EntityUtils.consumeQuietly(response.getEntity());
    }

    /**
     * @param response
     * @return
     * @author yunzhong
     * @version
     * @since 2017年5月19日
     */
    public int assertHttpResponse(HttpResponse response) {
        if (response == null) {
            return -1;
        }
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode >= 400) {
            log.info("Failed to invoke http:" + statusLine.getReasonPhrase());
        }
        return statusCode;
    }

    /**
     * @param userId
     * @return
     * @throws Exception
     * @throws UnsupportedCharsetException
     * @author yunzhong
     * @version
     * @since 2017年5月19日
     */
    public CaasUser getUser(String userId) throws UnsupportedCharsetException, Exception {
        HttpPost post = new HttpPost(this.serverURL + ADMIN_SEARCH_USER_URL);
        post.addHeader("Content-Type", "application/json");

        CaasSearchUserCondition condition = new CaasSearchUserCondition();
        condition.setPageNo("1");
        condition.setPageSize("1");
        condition.setName(userId);

        HttpEntity entity = new StringEntity(JsonUtils.convet2Json(condition), ContentType.APPLICATION_JSON);
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        int returnCode = assertHttpResponse(response);
        if (assertUnauth(returnCode)) {
            reAuth();
            response = client.execute(post);
        } else if (!assertSuccess(returnCode)) {
            log.error("Failed to get user uri [" + ADMIN_SEARCH_USER_URL + "].return code [" + returnCode + "]");
            return null;
        }
        String content = EntityUtils.toString(response.getEntity());
        log.trace(content);
        if (StringUtils.isEmpty(content)) {
            log.error("Failed to get user [" + userId + "] info.");
            return null;
        }
        CaasUserWrapper wrapper = JsonUtils.Json2Object(content, CaasUserWrapper.class);
        return wrapper.getOne();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.password = DigestUtils.md5Hex(this.plainPassword.getBytes("UTF-8")).toLowerCase();
        this.plainPassword = null;
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(60000).setConnectionRequestTimeout(60000)
                .setSocketTimeout(60000).build();
        client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        reAuth();

        ADMIN_SEARCH_USER_URL = String.format(ADMIN_SEARCH_USER_URL, appCode);
        ADMIN_SEARCH_ROLES = String.format(ADMIN_SEARCH_ROLES, appCode);
    }

    /**
     * @param roleCode
     * @return
     * @author yunzhong
     * @time 2017年8月21日下午2:24:26
     */
    public String getRoleMemebersURI(String roleCode) {
        return String.format(ADMIN_SEARCH_ROLE_MEMEBERS, roleCode);
    }

    public String getRoleName(String projectCode, String role) {
        return String.format("%s_%s", projectCode, role);
    }

    boolean assertSuccess(int returnCode) {
        if (returnCode >= 200 && returnCode < 300) {
            return true;
        }
        return false;
    }

    boolean assertUnauth(int returnCode) {
        return returnCode == 401;
    }

    @Override
    public List<CaasUser> getProjectUsers(String projectCode, String role) throws Exception {
        HttpGet roleGet = new HttpGet(this.serverURL + ADMIN_SEARCH_ROLES);
        roleGet.addHeader("Content-Type", "application/json");
        HttpResponse response = client.execute(roleGet);
        int returnCode = assertHttpResponse(response);
        if (assertUnauth(returnCode)) {
            reAuth();
            response = client.execute(roleGet);
        } else if (!assertSuccess(returnCode)) {
            log.error("Failed to get roles list uri [" + ADMIN_SEARCH_ROLES + "].return code [" + returnCode + "]");
            return new ArrayList<>();
        }
        String content = EntityUtils.toString(response.getEntity());
        log.trace(content);
        if (StringUtils.isEmpty(content)) {
            log.error("Failed to get all roles info.");
            return null;
        }
        final List<CaasRole> roleList = JsonUtils.array2List(content, CaasRole.class);
        if (CollectionUtils.isEmpty(roleList)) {
            log.warn("Failed to get project role info.");
            return new ArrayList<>();
        }
        if (log.isDebugEnabled()) {
            for (CaasRole caasRole : roleList) {
                log.debug(caasRole.getName() + caasRole.getCode());
            }
        }
        String roleCode = null;
        String roleName = getRoleName(projectCode, role);
        for (CaasRole caasRole : roleList) {
            if (roleName.equals(caasRole.getName())) {
                roleCode = caasRole.getCode();
                break;
            }
        }
        if (StringUtils.isEmpty(roleCode)) {
            log.warn("Failed to get rolecode [" + roleName + "]");
            return new ArrayList<>();
        }
        String uri = getRoleMemebersURI(roleCode);
        HttpGet userGet = new HttpGet(this.serverURL + uri);
        userGet.setHeader("Content-Type", "application/json");
        HttpResponse userResponse = client.execute(userGet);
        returnCode = assertHttpResponse(userResponse);
        if (assertUnauth(returnCode)) {
            reAuth();
            userResponse = client.execute(userGet);
        } else if (!assertSuccess(returnCode)) {
            log.error("Failed to get roles list uri [" + ADMIN_SEARCH_ROLES + "].return code [" + returnCode + "]");
            return new ArrayList<>();
        }
        content = EntityUtils.toString(userResponse.getEntity());
        log.trace(content);
        if (StringUtils.isEmpty(content)) {
            log.error("Failed to get all roles info.");
            return null;
        }
        return JsonUtils.array2List(content, CaasUser.class);
    }

}
