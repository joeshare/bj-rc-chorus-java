package cn.rongcapital.chorus.authorization.plugin.ranger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.rongcapital.chorus.authorization.api.data.AuthorizationData;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataDW;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationPerm;

/**
 * Created by shicheng on 2017/3/20.
 */
@Component
public class UserDataAuthorizationByRanger extends UserDataAuthorizationBase {

    protected AuthorizationData authorizationGetParse(String result) {
        AuthorizationDataDW dw = new AuthorizationDataDW();
        JSONObject object = JSON.parseObject(result);
        dw.setAuthorizationId(object.getString("id"));
        dw.setAuthorizationName(object.getString("policyName"));
        dw.setAuthorizationDescription(object.getString("description"));
        String columns = object.getString("columns");
        columns = (columns == null?"":columns);
        dw.setColumns(Arrays.asList(columns.split(",")));
        dw.setTables(Arrays.asList(object.getString("tables").split(",")));
        dw.setDatabases(Arrays.asList(object.getString("databases").split(",")));
        JSONObject perm = object.getJSONArray("permMapList").getJSONObject(0);
        List<AuthorizationPerm> permList = new ArrayList<>();
        AuthorizationPerm user = new AuthorizationPerm();

        user.setPermUserList(Arrays.asList(perm.getJSONArray("userList").toArray()));
        user.setPermGroupList(Arrays.asList(perm.getJSONArray("groupList").toArray()));
        user.setPermPermList(Arrays.asList(perm.getJSONArray("permList").toArray()));
        permList.add(user);
        dw.setAuthorizationPermMapList(permList);
        return dw;
    }

    @Override
    protected String generateRangerParams(AuthorizationData authorizationData) {
        return rangerUtils.getRangerParamsToDW(authorizationData);
    }

    @Override
    protected AuthorizationData authorizationSearchParse(String result) {
        return rangerUtils.getDataToDW(result);
    }

    @Override
    protected String getRepositoryName() {
        return rangerUtils.rangerRepositoryName;
    }
}
