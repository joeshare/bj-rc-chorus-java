package cn.rongcapital.chorus.das.service;

import java.util.List;

import cn.rongcapital.chorus.das.entity.TUser;

public interface TUserService {

    TUser getUserById(String userId);
    
    int add(TUser user);

    /**
     * @param users
     * @author yunzhong
     * @time 2017年8月24日下午2:24:18
     */
    void replaceBatch(List<TUser> users);
}
