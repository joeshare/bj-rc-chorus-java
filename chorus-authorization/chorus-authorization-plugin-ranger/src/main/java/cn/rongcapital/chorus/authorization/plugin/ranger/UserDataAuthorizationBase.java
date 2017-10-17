package cn.rongcapital.chorus.authorization.plugin.ranger;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

import cn.rongcapital.chorus.authorization.api.UserDataAuthorization;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class UserDataAuthorizationBase implements UserDataAuthorization {

    @Autowired
    RangerUtils rangerUtils;
    
    protected final String EQUALS_STRING = "=";
    protected final String PARAM_SPLIT = "&";

    /**
     * 新增授权
     *
     * @param authorizationData 授权信息
     * @return 授权信息
     */
    public AuthorizationData authorizationAdd(AuthorizationData authorizationData) throws Exception {
        assert (authorizationData != null);
        if (authorizationData != null) {
            boolean loginStatus = rangerUtils.rangerLogin();
            if (loginStatus) {
                Response response = Request.Post(rangerUtils.getRangerAPIPath())
                        .addHeader(HTTP.CONTENT_TYPE, "application/json")
                        .bodyString(generateRangerParams(authorizationData), ContentType.APPLICATION_JSON).execute();
                AuthorizationData responseData = response.handleResponse(httpResponse -> RangerUtils
                        .getResultToData(JSON.parseObject(httpResponse.getEntity().getContent(), String.class)));
                if ("-1".equals(responseData.getAuthorizationId())) {
                    log.error("Failed to auth." + responseData.getAuthorizationDescription());
                    throw new RuntimeException("authorization error");
                } else {
                    return responseData;
                }
            }
        }
        throw new RuntimeException("login error");
    }

    /**
     * 更新授权
     *
     * @param authorization 授权信息
     * @return 授权信息
     */
    public boolean authorizationUpdate(AuthorizationData authorization) throws Exception {
        assert (authorization != null);
        if (authorization != null) {
            boolean loginStatus = rangerUtils.rangerLogin();
            if (loginStatus) {
                Response response = Request
                        .Put(rangerUtils.getRangerAPIPath() + "/" + authorization.getAuthorizationId())
                        .addHeader(HTTP.CONTENT_TYPE, "application/json")
                        .bodyString(generateRangerParams(authorization), ContentType.APPLICATION_JSON).execute();
                String result = response.handleResponse(
                        httpResponse -> JSON.parseObject(httpResponse.getEntity().getContent(), String.class));
                if(log.isDebugEnabled()){
                    log.debug(result);
                }
                if (result != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 清除授权
     *
     * @param authorization 授权信息
     * @return 授权信息
     */
    public boolean authorizationRemove(AuthorizationData authorization) throws Exception {
        assert (authorization != null);
        if (authorization.getAuthorizationId() != null) {
            boolean loginStatus = rangerUtils.rangerLogin();
            if (loginStatus) {
                Request.Delete(rangerUtils.getRangerAPIPath() + "/" + authorization.getAuthorizationId()).execute();
                return true;
            }
        }
        return false;
    }

    /**
     * 查询授权
     *
     * @param authorization 授权信息
     * @return 授权信息
     */
    @Override
    public AuthorizationData authorizationGet(AuthorizationData authorization) throws Exception {
        assert (authorization != null);
        boolean loginStatus = rangerUtils.rangerLogin();
        String path = String.format(rangerUtils.getRangerAPIPath() + "/" + authorization.getAuthorizationId());
        if (loginStatus) {
            String result = Request.Get(path).execute().returnContent().asString();
            return authorizationGetParse(result);
        }
        throw new RuntimeException("login error");
    }

    @Override
    public AuthorizationData authorizationSearch(AuthorizationData authorization) throws Exception {
        assert (authorization != null);
        boolean loginStatus = rangerUtils.rangerLogin();
        String path = String.format(rangerUtils.getRangerAPIPath() + "?repositoryName=" + getRepositoryName()
                + "&policyName=" + authorization.getAuthorizationName());
        if (loginStatus) {
            String result = Request.Get(path).execute().returnContent().asString();
            return authorizationSearchParse(result);
        }
        throw new RuntimeException("login error");
    }

    protected abstract String generateRangerParams(AuthorizationData authorizationData);

    protected abstract AuthorizationData authorizationSearchParse(String result);

    protected abstract AuthorizationData authorizationGetParse(String result);

    protected abstract String getRepositoryName();
    
    private AuthorizationData getById(String id) throws Exception{
        boolean loginStatus = rangerUtils.rangerLogin();
        String path = String.format(rangerUtils.getRangerAPIPath() + "/" + id);
        if (loginStatus) {
            String result = Request.Get(path).execute().returnContent().asString();
            return authorizationGetParse(result);
        }
        throw new RuntimeException("login error");
    }
    
    public boolean disablePolicy(String authId) throws Exception {
        if (StringUtils.isEmpty(authId)) {
            log.warn("auth id is empty to disable.");
            return false;
        }
        final AuthorizationData data = getById(authId);
        if (data == null) {
            log.warn("Failed to get auth data by id {}", authId);
            return false;
        }
        data.setEnabled(false);
        return this.authorizationUpdate(data);
    }
}
