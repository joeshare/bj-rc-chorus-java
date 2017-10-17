package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.authorization.api.UserDataAuthorization;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationData;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataDW;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataFS;
import cn.rongcapital.chorus.das.entity.AuthorizationDetailCategory;
import cn.rongcapital.chorus.das.service.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by hhlfl on 2017-8-28.
 */
@Slf4j
@Service
public class AuthorizationServiceImpl implements AuthorizationService {
    @Autowired
    @Qualifier("userDataAuthRangerHDFS")
    private UserDataAuthorization userDataAuthRangerHDFS;
    @Autowired
    @Qualifier("userDataAuthorizationByRanger")
    private UserDataAuthorization userDataAuthorizationByRanger;

    @Override
    public boolean disable(String policyId, AuthorizationDetailCategory category) throws Exception {
        AuthorizationData authorization = new AuthorizationData();
        authorization.setAuthorizationId(policyId);
        AuthorizationData authorizationData = null;
        boolean f = false;
        switch (category) {
            case HIVE:
                authorizationData = userDataAuthorizationByRanger.authorizationGet(authorization);
                if(authorizationData != null) {
                    AuthorizationDataDW dataDW = (AuthorizationDataDW) authorizationData;
                    dataDW.setEnabled(false);
                    userDataAuthorizationByRanger.authorizationUpdate(dataDW);
                }
                f = true;
                break;
            case HDFS:
                authorizationData = userDataAuthRangerHDFS.authorizationGet(authorization);
                if(authorizationData != null) {
                    AuthorizationDataFS dataFS = (AuthorizationDataFS) authorizationData;
                    dataFS.setEnabled(false);
                    userDataAuthRangerHDFS.authorizationUpdate(dataFS);
                }
                f = true;
                break;
        }

        return f;
    }
}
