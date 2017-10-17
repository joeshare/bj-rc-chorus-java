package cn.rongcapital.chorus.das.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.rongcapital.chorus.das.entity.TUser;

public interface TUserMapper {

    /**
     * @param userId
     * @return
     * @author yunzhong
     * @time 2017年8月22日下午3:09:41
     */
    TUser getUserById(@Param("userId") String userId);

    /**
     * @param user
     * @return
     * @author yunzhong
     * @time 2017年8月22日下午3:53:28
     */
    int add(@Param("user")TUser user);

    /**
     * @param users
     * @author yunzhong
     * @time 2017年8月24日下午2:25:23
     */
    void replaceBatch(@Param("users")List<TUser> users);

}
