package cn.rongcapital.chorus.authorization.plugin.ranger.data;

import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Created by shicheng on 2017/3/22.
 */
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RangerBase {

    private String policyName; // 授权策略名称
    private String description; // 授权策略描述
    private String repositoryName; // 授权策略仓库名称
    private String repositoryType; // 授权类型
    private List<RangerPerm> permMapList; // 授权权限信息
    private boolean isAuditEnabled=true;//默认开启审计

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getRepositoryType() {
        return repositoryType;
    }

    public void setRepositoryType(String repositoryType) {
        this.repositoryType = repositoryType;
    }

    public List<RangerPerm> getPermMapList() {
        return permMapList;
    }

    public void setPermMapList(List<RangerPerm> permMapList) {
        this.permMapList = permMapList;
    }

    public boolean getIsAuditEnabled() {
        return isAuditEnabled;
    }

    public void setIsAuditEnabled(boolean isAuditEnabled) {
        isAuditEnabled = isAuditEnabled;
    }
}
