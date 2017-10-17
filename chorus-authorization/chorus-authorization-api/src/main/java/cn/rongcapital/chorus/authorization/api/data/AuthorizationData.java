package cn.rongcapital.chorus.authorization.api.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * 数据授权信息
 * Created by shicheng on 2017/3/7.
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationData {

    private String authorizationId; // ID
    private String authorizationName; // 授权策略名称
    private String authorizationDescription; // 授权策略描述
    private AuthorizationDataType authorizationRepositoryType; // 授权类型
    private AuthorizationUser authorizationUser; // 授权用户
    private List<AuthorizationPerm> authorizationPermMapList; // 授权权限信息
    private Date authorizationStartTime; // 授权时间
    private Date authorizationEndTime; // 授权失效时间
    private boolean isEnabled; // 授权是否启用
}