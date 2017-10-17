package cn.rongcapital.chorus.authorization.plugin.ranger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataType;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.rongcapital.chorus.authorization.api.data.AuthorizationData;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataFS;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationPerm;

/**
 * @author yunzhong
 *
 * @date 2017年8月23日下午5:09:25
 */
@Component
public class UserDataAuthRangerHDFS extends UserDataAuthorizationBase {

    @Override
    protected AuthorizationData authorizationGetParse(String result) {
        AuthorizationDataFS data = new AuthorizationDataFS();
        JSONObject object = JSON.parseObject(result);
        data.setAuthorizationId(object.getString("id"));
        data.setAuthorizationName(object.getString("policyName"));
        data.setAuthorizationDescription(object.getString("description"));
        data.setResourceName(object.getString("resourceName"));
        data.setAuthorizationRepositoryType(AuthorizationDataType.FS);
        data.setEnabled(object.getBoolean("isEnabled"));
        JSONObject perm = object.getJSONArray("permMapList").getJSONObject(0);
        List<AuthorizationPerm> permList = new ArrayList<>();
        AuthorizationPerm user = new AuthorizationPerm();

        user.setPermUserList(Arrays.asList(perm.getJSONArray("userList").toArray()));
        user.setPermGroupList(Arrays.asList(perm.getJSONArray("groupList").toArray()));
        user.setPermPermList(Arrays.asList(perm.getJSONArray("permList").toArray()));
        permList.add(user);
        data.setAuthorizationPermMapList(permList);
        return data;
    }

    @Override
    public String generateRangerParams(AuthorizationData authorizationData) {
        return rangerUtils.getRangerParamsToHDFS(authorizationData);
    }

    @Override
    protected AuthorizationData authorizationSearchParse(String result) {
        return rangerUtils.getDataToHDFS(result);
    }

    @Override
    protected String getRepositoryName() {
        return rangerUtils.getRangerHdfsRepositoryName();
    }
}
