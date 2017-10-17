package cn.rongcapital.chorus.server.project.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import cn.rongcapital.chorus.authorization.api.data.AuthorizationData;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataFS;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataType;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationPerm;
import cn.rongcapital.chorus.authorization.api.data.HDFSPermissions;
import cn.rongcapital.chorus.authorization.plugin.ranger.RangerUtils;
import cn.rongcapital.chorus.common.constant.ProjectRoleEnum;
import cn.rongcapital.chorus.das.entity.AuthorizationDetail;
import cn.rongcapital.chorus.das.entity.AuthorizationDetailCategory;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import scala.actors.threadpool.Arrays;

/**
 * @author yunzhong
 *
 * @date 2017年8月24日上午9:21:04
 */
public class HDFSAuthAssembler {

    /**
     * @param project
     * @return
     * @author yunzhong
     * @time 2017年8月24日上午9:19:39
     */
    public static AuthorizationDataFS assembleHDFSPath(ProjectInfo project, ProjectRoleEnum roleType,
            List<String> userNames) {
        AuthorizationDataFS authData = new AuthorizationDataFS();
        authData.setAuthorizationRepositoryType(AuthorizationDataType.FS);
        authData.setResourceName(RangerUtils.generateHDFSResourceName(project.getProjectCode()));
        authData.setEnabled(true);
        authData.setRecursive(true);
        String policy = RangerUtils.generateHDFSPolicyName(project.getProjectCode());
        authData.setAuthorizationName(policy);
        if (roleType != null)
            assembleHDFSPath(authData, roleType, userNames);
        return authData;
    }

    /**
     * @param authData
     * @param roleType
     * @param userNames
     * @author yunzhong
     * @time 2017年8月24日下午1:47:09
     */
    public static void assembleHDFSPath(AuthorizationDataFS authData, ProjectRoleEnum roleType,
            List<String> userNames) {
        List<AuthorizationPerm> perms = authData.getAuthorizationPermMapList();
        if (perms == null) {
            perms = new ArrayList<>();
        }
        AuthorizationPerm perm = new AuthorizationPerm();
        perm.setPermUserList(userNames);
        switch (roleType) {
        case OWNER:
        case ADMIN:
        case DEV:
            List<Object> permissions = new ArrayList<>();
            permissions.add(HDFSPermissions.Read.getValue());
            permissions.add(HDFSPermissions.Write.getValue());
            permissions.add(HDFSPermissions.Execute.getValue());
            perm.setPermPermList(permissions);
            perms.add(perm);
            break;
        case QUERY:
            List<Object> queryPermission = new ArrayList<>();
            queryPermission.add(HDFSPermissions.Read.getValue());
            queryPermission.add(HDFSPermissions.Execute.getValue());
            perm.setPermPermList(queryPermission);
            perms.add(perm);
            break;
        default:
            break;
        }
        authData.setAuthorizationPermMapList(perms);

    }

    /**
     * @param project
     * @return
     * @author yunzhong
     * @time 2017年8月24日上午9:19:39
     */
    public static AuthorizationDataFS assembleProjectPath(ProjectInfo project) {
        AuthorizationDataFS authData = new AuthorizationDataFS();
        authData.setAuthorizationRepositoryType(AuthorizationDataType.FS);
        authData.setResourceName(RangerUtils.generateHDFSProjectpath(project.getProjectCode()));
        authData.setEnabled(true);
        authData.setRecursive(true);
        String policy = RangerUtils.generateHDFSProjectPolicyName(project.getProjectCode(), project.getCreateUserId());

        List<AuthorizationPerm> perms = new ArrayList<>();
        AuthorizationPerm perm = new AuthorizationPerm();
        authData.setAuthorizationName(policy);
        List<Object> users = new ArrayList<>();
        users.add(project.getUserName());
        perm.setPermUserList(users);
        List<Object> permissions = new ArrayList<>();
        permissions.add(HDFSPermissions.Read.getValue());
        permissions.add(HDFSPermissions.Write.getValue());
        permissions.add(HDFSPermissions.Execute.getValue());
        perm.setPermPermList(permissions);
        perms.add(perm);
        authData.setAuthorizationPermMapList(perms);
        return authData;
    }

    /**
     * @param targetAuth
     * @param authorizationSearch
     * @return
     * @throws Exception
     * @author yunzhong
     * @time 2017年8月24日上午11:11:44
     */
    public static AuthorizationData updateAuthorization(AuthorizationDataFS targetAuth,
            AuthorizationData authorizationSearch) throws Exception {
        authorizationSearch.setAuthorizationPermMapList(targetAuth.getAuthorizationPermMapList());
        if(authorizationSearch instanceof AuthorizationDataFS){
            ((AuthorizationDataFS) authorizationSearch).setResourceName(targetAuth.getResourceName());
            ((AuthorizationDataFS) authorizationSearch).setRecursive(true);
        }
        return authorizationSearch;
    }

    /**
     * @param appendAuth
     * @param authorizationSearch
     * @return
     * @throws Exception
     * @author yunzhong
     * @time 2017年8月24日下午5:54:41
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static AuthorizationData appendAuthorization(AuthorizationDataFS appendAuth,
            AuthorizationData authorizationSearch) throws Exception {
        final List<AuthorizationPerm> authPerms = authorizationSearch.getAuthorizationPermMapList();
        final List<AuthorizationPerm> appendPerms = appendAuth.getAuthorizationPermMapList();
        if (CollectionUtils.isEmpty(appendPerms)) {
            return authorizationSearch;
        }
        if (CollectionUtils.isEmpty(authPerms)) {
            authorizationSearch.setAuthorizationPermMapList(appendAuth.getAuthorizationPermMapList());
            return authorizationSearch;
        }
        // 用需要append的perm去和之前的perm列表比对：如果找到相同的权限，则将perm的user附加到此列表中；如果没有找到相同权限，则添加此权限的设置。
        // 权限不同，则在此perm中删除此用户。
        for (AuthorizationPerm appendPerm : appendPerms) {
            boolean appended = false;
            final int appendCode = HDFSPermissions.encode(appendPerm.getPermPermList());
            for (AuthorizationPerm authPerm : authPerms) {
                final int authCode = HDFSPermissions.encode(authPerm.getPermPermList());
                if (appendCode == authCode) {
                    Set totalUsers = new HashSet<>();
                    totalUsers.addAll(appendPerm.getPermUserList());
                    totalUsers.addAll(authPerm.getPermUserList());
                    authPerm.setPermUserList(Arrays.asList(totalUsers.toArray(new Object[totalUsers.size()])));
                    appended = true;
                } else {
                    Set totalUsers = new HashSet<>();
                    totalUsers.addAll(authPerm.getPermUserList());
                    totalUsers.removeAll(appendPerm.getPermUserList());
                    authPerm.setPermUserList(Arrays.asList(totalUsers.toArray(new Object[totalUsers.size()])));
                }
            }

            if (!appended) {
                authPerms.add(appendPerm);
            }
        }
        // 清除user为空的perm。
        // 由于用户只会添加或者更新，所以perm list 不会为空，不需考虑。
        final Iterator<AuthorizationPerm> iterator = authPerms.iterator();
        while (iterator.hasNext()) {
            if (CollectionUtils.isEmpty(iterator.next().getPermUserList())) {
                iterator.remove();
            }
        }
        return authorizationSearch;
    }

    /**
     * @param authData
     * @param project
     * @return
     * @author yunzhong
     * @time 2017年8月24日上午11:33:53
     */
    public static AuthorizationDetail assembleDetail(AuthorizationData authData, ProjectInfo project) {
        return assembleDetail(authData.getAuthorizationId(), authData.getAuthorizationName(), project.getProjectId(),
                project.getCreateUserId());
    }

    public static AuthorizationDetail assembleDetail(String policyId, String policyName, Long projectId,
            String userId) {
        AuthorizationDetail detail = new AuthorizationDetail();
        detail.setCreateTime(new Date());
        detail.setPolicyId(policyId);
        detail.setPolicyName(policyName);
        detail.setProjectId(projectId);
        detail.setUserId(userId);
        detail.setCategory(AuthorizationDetailCategory.HDFS.name());
        detail.setEnabled(1);
        return detail;
    }

    /**
     * @param userName
     * @param authorizationSearch
     * @return
     * @author yunzhong
     * @time 2017年8月25日上午9:29:36
     */
    @SuppressWarnings("rawtypes")
    public static void deleteAuthorization(String userName, AuthorizationData authorizationSearch) {
        final List<AuthorizationPerm> permList = authorizationSearch.getAuthorizationPermMapList();
        if (CollectionUtils.isNotEmpty(permList)) {
            final Iterator<AuthorizationPerm> iterator = permList.iterator();
            while (iterator.hasNext()) {
                final List permUserList = iterator.next().getPermUserList();
                if (permUserList != null) {
                    permUserList.remove(userName);
                }
                if (CollectionUtils.isEmpty(permUserList)) {
                    iterator.remove();
                }
            }
        }
    }
}
