package cn.rongcapital.chorus.authorization.plugin.ranger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.rongcapital.chorus.authorization.api.data.AuthorizationData;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataDW;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataFS;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataType;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationPerm;
import cn.rongcapital.chorus.authorization.api.data.DWTCType;
import cn.rongcapital.chorus.authorization.plugin.ranger.data.RangerDW;
import cn.rongcapital.chorus.authorization.plugin.ranger.data.RangerHDFS;
import cn.rongcapital.chorus.authorization.plugin.ranger.data.RangerPerm;
import cn.rongcapital.chorus.common.hadoop.HadoopUtil;
import cn.rongcapital.chorus.common.util.CollectionUtils;
import cn.rongcapital.chorus.common.util.StringUtils;
import lombok.Getter;

import static java.util.stream.Collectors.toList;

/**
 * Ranger 插件配置 Created by shicheng on 2017/3/20.
 * 
 */
@Component
public class RangerUtils {

    @Value("${ranger.service.url}")
    public String serverUrl;

    @Value("${ranger.repository.name}")
    public String rangerRepositoryName;

    @Value("${ranger.repository.hdfs.name}")
    @Getter
    private String rangerHdfsRepositoryName;

    @Value("${ranger.service.policy}")
    public String rangerServicePolicy;

    @Value("${ranger.login}")
    public String rangerLogin;

    public static String RANGER_USERNAME = "j_username"; // ranger login
    public static String RANGER_PASSWORD = "j_password"; // ranger login

    @Value("${ranger.username}")
    public String username;

    @Value("${ranger.password}")
    public String password;

    // "chorus_" + record.getProjectCode() + "_HDFS"
    public static String RANGER_HDFS_POLICY_NAME = "chorus_%s_HDFS";
    // "chorus_" + record.getProjectCode() + "_" + record.getCreateUserId() +
    // "_PROJECT_HDFS";
    public static String RANGER_HDFS_PROJECT_POLICY_NAME = "chorus_%s_%s_PROJECT_HDFS";

    public static String generateHDFSPolicyName(String projectCode) {
        return String.format(RANGER_HDFS_POLICY_NAME, projectCode);
    }

    public static String generateHDFSProjectPolicyName(String projectCode, String creator) {
        return String.format(RANGER_HDFS_PROJECT_POLICY_NAME, projectCode, creator);
    }

    /**
     * @param projectCode
     * @return
     * @author yunzhong
     * @time 2017年8月23日下午8:52:28
     */
    public static String generateHDFSResourceName(String projectCode) {
        final String parentPath = HadoopUtil.appendPath(HadoopUtil.formatPath(projectCode), "hdfs");
        return parentPath;
    }

    /**
     * @param projectCode
     * @return
     * @author yunzhong
     * @time 2017年8月24日上午9:42:23
     */
    public static String generateHDFSProjectpath(String projectCode) {
        final String parentPath = HadoopUtil.formatPath(projectCode);
        return parentPath;
    }

    /**
     * Ranger API 路径
     *
     * @return Ranger API 路径
     */
    public String getRangerAPIPath() {
        return String.format("%s%s", serverUrl, rangerServicePolicy);
    }

    /**
     * Ranger 登录路径
     *
     * @return Ranger API 路径
     */
    public String getRangerLoginPath() {
        return String.format("%s%s", serverUrl, rangerLogin);
    }

    /**
     * Ragner 登录授权
     *
     * @return 授权信息
     */
    public boolean rangerLogin() throws Exception {
        return login(username, password);
    }

    public boolean login(String username, String password) throws Exception {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new RuntimeException("authorization username or password not null");
        }
        List<NameValuePair> userParams = new ArrayList<>();
        userParams.add(new BasicNameValuePair(RANGER_USERNAME, username));
        userParams.add(new BasicNameValuePair(RANGER_PASSWORD, password));
        String result = Request.Post(getRangerLoginPath()).body(new UrlEncodedFormEntity(userParams)).execute()
                .returnContent().asString();

        if (result != null) {
            JSONObject object = JSON.parseObject(result);
            if (object.get("statusCode").toString().trim().equals("200")) {
                return true;
            }
        }
        return false;
    }

    public boolean rangerLogin(String username, String password) throws Exception {
        return login(username, password);
    }

    public String getRangerParamsToDW(AuthorizationData authorizationData) {
        trim(authorizationData);
        AuthorizationDataDW authorization = (AuthorizationDataDW) authorizationData;
        RangerDW data = new RangerDW();
        data.setPolicyName(authorization.getAuthorizationName());
        data.setDatabases(StringUtils.join(authorization.getDatabases(),","));
        data.setTables(StringUtils.join(authorization.getTables(), ","));
        data.setColumns(StringUtils.join(authorization.getColumns(), ","));
        data.setDescription(authorization.getAuthorizationDescription());
        data.setRepositoryName(rangerRepositoryName);
        data.setRepositoryType(AuthorizationDataType.DW.getValue());
        data.setTableType(DWTCType.INCLUDE.getValue());
        data.setColumnType(DWTCType.INCLUDE.getValue());
        data.setIsEnabled(authorization.isEnabled());
        List<RangerPerm> perms = new ArrayList<>();
        List<AuthorizationPerm> authorizationPerms = authorization.getAuthorizationPermMapList();
        if(authorizationPerms != null && !authorizationPerms.isEmpty()) {
            for (AuthorizationPerm authorizationPerm : authorizationPerms) {
                RangerPerm rangerPerm = new RangerPerm();
                rangerPerm.setGroupList(authorizationPerm.getPermGroupList());
                rangerPerm.setPermList(authorizationPerm.getPermPermList());
                rangerPerm.setUserList(authorizationPerm.getPermUserList());
                perms.add(rangerPerm);
            }
        }
        data.setPermMapList(perms);
        return JSON.toJSON(data).toString();
    }

    /**
     * @param authorizationData
     * @return
     * @author yunzhong
     * @time 2017年8月23日下午7:51:21
     */
    public String getRangerParamsToHDFS(AuthorizationData authorizationData) {
        trim(authorizationData);
        AuthorizationDataFS fsAuth = (AuthorizationDataFS) authorizationData;
        RangerHDFS data = new RangerHDFS();
        data.setPolicyName(fsAuth.getAuthorizationName());
        data.setDescription(fsAuth.getAuthorizationDescription());
        data.setRepositoryName(rangerHdfsRepositoryName);
        data.setRepositoryType(AuthorizationDataType.FS.getValue());
        data.setResourceName(fsAuth.getResourceName());
        data.setIsEnabled(fsAuth.isEnabled());
        data.setIsRecursive(fsAuth.isRecursive());
        List<RangerPerm> perms = new ArrayList<>();
        for (AuthorizationPerm authorizationPerm : fsAuth.getAuthorizationPermMapList()) {
            RangerPerm rangerPerm = new RangerPerm();
            rangerPerm.setGroupList(authorizationPerm.getPermGroupList());
            rangerPerm.setPermList(authorizationPerm.getPermPermList());
            rangerPerm.setUserList(authorizationPerm.getPermUserList());
            perms.add(rangerPerm);
        }
        data.setPermMapList(perms);
        return JSON.toJSON(data).toString();
    }

    public static AuthorizationData getResultToData(String json) {
        AuthorizationData data = new AuthorizationData();
        JSONObject object = JSON.parseObject(json);
        String id = String.valueOf(object.get("id"));
        if (id != "null") {
            data.setAuthorizationId(id);
            data.setAuthorizationName(String.valueOf(object.get("policyName")));
        } else {
            data.setAuthorizationId("-1");
            data.setAuthorizationDescription(String.valueOf(object.get("msgDesc")));
        }
        return data;
    }

    public AuthorizationData getDataToDW(String json) {
        AuthorizationDataDW authorization = new AuthorizationDataDW();
        JSONArray jsonArray = JSON.parseObject(json).getJSONArray("vXPolicies");
        if (jsonArray.size() <= 0) {
            return null;
        }
        JSONObject object = jsonArray.getJSONObject(0);
        authorization.setAuthorizationId(object.getString("id"));
        authorization.setAuthorizationName(object.getString("policyName"));
        authorization.setAuthorizationDescription(object.getString("description"));
        authorization.setColumns(Arrays.asList(object.getString("columns").split(",")));
        authorization.setTables(Arrays.asList(object.getString("tables").split(",")));
        authorization.setDatabases(Arrays.asList(object.getString("databases").split(",")));
        JSONObject perm = object.getJSONArray("permMapList").getJSONObject(0);

        List<AuthorizationPerm> permList = new ArrayList<>();
        AuthorizationPerm user = new AuthorizationPerm();

        user.setPermUserList(Arrays.asList(perm.getJSONArray("userList").toArray()));
        user.setPermGroupList(Arrays.asList(perm.getJSONArray("groupList").toArray()));
        user.setPermPermList(Arrays.asList(perm.getJSONArray("permList").toArray()));

        permList.add(user);
        authorization.setAuthorizationPermMapList(permList);
        return authorization;
    }

    /**
     * @param json
     * @return
     * @author yunzhong
     * @time 2017年8月23日下午7:59:40
     */
    @SuppressWarnings("rawtypes")
    public AuthorizationData getDataToHDFS(String json) {
        AuthorizationDataFS authorization = new AuthorizationDataFS();
        JSONArray jsonArray = JSON.parseObject(json).getJSONArray("vXPolicies");
        if (jsonArray.size() <= 0) {
            return null;
        }
        JSONObject object = jsonArray.getJSONObject(0);
        authorization.setAuthorizationId(object.getString("id"));
        authorization.setAuthorizationName(object.getString("policyName"));
        authorization.setAuthorizationDescription(object.getString("description"));
        authorization.setResourceName(object.getString("resourceName"));
        authorization.setEnabled(object.getBooleanValue("isEnabled"));
        authorization.setRecursive(object.getBooleanValue("isRecursive"));
        final JSONArray perms = object.getJSONArray("permMapList");
        List<AuthorizationPerm> permList = new ArrayList<>();
        for (int i = 0; i < perms.size(); i++) {
            JSONObject perm = perms.getJSONObject(i);
            AuthorizationPerm authPerm = new AuthorizationPerm();

            List permUsers = new ArrayList<>();
            CollectionUtils.addAll(permUsers, perm.getJSONArray("userList").toArray());
            authPerm.setPermUserList(permUsers);
            List permGroup = new ArrayList<>();
            CollectionUtils.addAll(permGroup, perm.getJSONArray("groupList").toArray());
            authPerm.setPermGroupList(permGroup);
            List permPerm = new ArrayList<>();
            CollectionUtils.addAll(permPerm, perm.getJSONArray("permList").toArray());
            authPerm.setPermPermList(permPerm);

            permList.add(authPerm);
        }
        authorization.setAuthorizationPermMapList(permList);
        return authorization;
    }

    /**
     * @param result
     * @return
     * @author yunzhong
     * @time 2017年9月7日下午12:02:01
     */
    public List<AuthorizationData> getDatasToDWs(String result) {
        AuthorizationDataDW authorization = new AuthorizationDataDW();
        JSONArray jsonArray = JSON.parseObject(result).getJSONArray("vXPolicies");
        if (jsonArray.size() <= 0) {
            return new ArrayList<>();
        }
        List<AuthorizationData> datas = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            authorization.setAuthorizationId(object.getString("id"));
            authorization.setAuthorizationName(object.getString("policyName"));
            authorization.setAuthorizationDescription(object.getString("description"));
            final String columns = object.getString("columns");
            if (StringUtils.isNotEmpty(columns))
                authorization.setColumns(Arrays.asList(columns.split(",")));
            final String tables = object.getString("tables");
            if (StringUtils.isNotEmpty(tables))
                authorization.setTables(Arrays.asList(object.getString("tables").split(",")));
            final String databases = object.getString("databases");
            if (StringUtils.isNotEmpty(databases))
                authorization.setDatabases(Arrays.asList(databases.split(",")));
            final JSONArray permJsonArray = object.getJSONArray("permMapList");
            if (permJsonArray.size() > 0) {
                JSONObject perm = object.getJSONArray("permMapList").getJSONObject(0);
                
                List<AuthorizationPerm> permList = new ArrayList<>();
                AuthorizationPerm user = new AuthorizationPerm();
                
                user.setPermUserList(Arrays.asList(perm.getJSONArray("userList").toArray()));
                user.setPermGroupList(Arrays.asList(perm.getJSONArray("groupList").toArray()));
                user.setPermPermList(Arrays.asList(perm.getJSONArray("permList").toArray()));
                
                permList.add(user);
                authorization.setAuthorizationPermMapList(permList);
            }
            datas.add(authorization);
        }
        return datas;
    }

    public void trim(AuthorizationData data){
        if(data == null) return;
        data.setAuthorizationName(StringUtils.trim(data.getAuthorizationName()));
        data.setAuthorizationId(StringUtils.trim(data.getAuthorizationId()));
        if(data instanceof AuthorizationDataDW){
            AuthorizationDataDW dataDW = (AuthorizationDataDW) data;
            dataDW.setDatabases(trim(dataDW.getDatabases()));
            dataDW.setTables(trim(dataDW.getTables()));
            dataDW.setColumns(trim(dataDW.getColumns()));
        }else if(data instanceof AuthorizationDataFS){
            AuthorizationDataFS dataFS = (AuthorizationDataFS)data;
            dataFS.setResourceName(StringUtils.trim(dataFS.getResourceName()));
        }
    }

    private List<String> trim(List<String> list){
        if(list == null) return null;

        return list.stream().map(item->{
            return StringUtils.trim(item);
        }).collect(toList());

    }
}
