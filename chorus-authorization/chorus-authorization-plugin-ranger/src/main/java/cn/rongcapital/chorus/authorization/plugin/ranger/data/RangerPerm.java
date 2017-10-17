package cn.rongcapital.chorus.authorization.plugin.ranger.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Created by shicheng on 2017/3/22.
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RangerPerm {

    private List userList; // 用户列表
    private List groupList; // 用户组列表
    private List permList; // 权限列表

}
