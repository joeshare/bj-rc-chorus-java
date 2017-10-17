package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.common.constant.StatusCode;

/**
 * Created by alan on 11/28/16.
 */
public interface InstanceOperationService {

    void insert(Long instanceId, String userId,String userName, StatusCode statusCode);

}
