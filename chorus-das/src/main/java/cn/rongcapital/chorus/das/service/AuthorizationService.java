package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.AuthorizationDetailCategory;

/**
 * Created by hhlfl on 2017-8-28.
 */
public interface AuthorizationService {
    boolean disable(String policyId, AuthorizationDetailCategory category)throws Exception;
}
