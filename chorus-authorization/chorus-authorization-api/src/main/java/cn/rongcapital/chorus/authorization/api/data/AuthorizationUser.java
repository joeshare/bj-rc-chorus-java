package cn.rongcapital.chorus.authorization.api.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 用户授权信息
 * Created by shicheng on 2017/3/7.
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationUser {

    private String authorizationUserName; // 用户名
    private String authorizationPassword; // 用户密码

}
