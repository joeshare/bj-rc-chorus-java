package cn.rongcapital.chorus.authorization.api.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * FS
 * Created by shicheng on 2017/4/5.
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationDataFS extends AuthorizationData {

    private String resourceName; // 资源路径, 多个以 , 分割
    private boolean isRecursive;//路径权限是否迭代传递
}
