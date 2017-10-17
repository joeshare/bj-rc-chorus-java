package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.caas.CaasUser;

import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;

public interface CaasClient {

    /**
     * 根据配置文件获得认证，并将认证信息缓存
     * 
     * @throws UnsupportedEncodingException
     * @throws Exception
     * @author yunzhong
     * @version
     * @since 2017年5月19日
     */
    void reAuth() throws UnsupportedEncodingException, Exception;

    /**
     * @param userId
     * @return CaasUser
     * @throws UnsupportedCharsetException
     * @throws Exception
     * @author yunzhong
     * @version
     * @since 2017年5月19日
     */
    CaasUser getUser(String userId) throws UnsupportedCharsetException, Exception;

    /**
     * 获得一个项目下，对应角色的所有人员信息
     * 
     * @param projectId
     * @param role
     * @return
     * @author yunzhong
     * @time 2017年8月21日下午2:32:56
     */
    List<CaasUser> getProjectUsers(String projectCode, String role) throws Exception;
}
