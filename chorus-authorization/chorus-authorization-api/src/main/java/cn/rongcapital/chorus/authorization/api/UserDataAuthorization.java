package cn.rongcapital.chorus.authorization.api;

import cn.rongcapital.chorus.authorization.api.data.AuthorizationData;

/**
 * 数据授权
 * <p>
 * <ul>
 * <li>数据文件</li>
 * <li>数据库</li>
 * <li>数据表</li>
 * <li>数据行</li>
 * <li>数据列</li>
 * </ul>
 * Created by shicheng on 2017/3/6.
 */
public interface UserDataAuthorization {

    /**
     * 新增授权
     *
     * @param authorization 授权信息
     * @return 授权信息
     */
    AuthorizationData authorizationAdd(AuthorizationData authorization) throws Exception;

    /**
     * 更新授权
     *
     * @param authorization 授权信息
     * @return 授权信息
     */
    boolean authorizationUpdate(AuthorizationData authorization) throws Exception;

    /**
     * 清除授权
     *
     * @param authorization 授权信息
     * @return 授权信息
     */
    boolean authorizationRemove(AuthorizationData authorization) throws Exception;

    /**
     * 查询授权
     *
     * @param authorization 授权信息
     * @return 授权信息
     */
    AuthorizationData authorizationGet(AuthorizationData authorization) throws Exception;

    /**
     * 查询授权
     *
     * @param authorization 授权信息
     * @return 授权信息
     */
    AuthorizationData authorizationSearch(AuthorizationData authorization) throws Exception;
    
    /**
     * @param authId
     * @return
     * @throws Exception
     * @author yunzhong
     * @time 2017年9月8日上午11:02:54
     */
    boolean disablePolicy(String authId) throws Exception;
}
