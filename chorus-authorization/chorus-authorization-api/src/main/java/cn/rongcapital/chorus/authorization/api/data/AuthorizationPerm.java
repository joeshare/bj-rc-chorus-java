package cn.rongcapital.chorus.authorization.api.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by shicheng on 2017/3/21.
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationPerm {

    private List permUserList; // 用户列表
    private List permGroupList; // 用户组列表
    private List permPermList; // 权限列表

}

