package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.AuthorizationDetail;

import java.util.List;

public interface AuthorizationDetailService {
    int deleteByPrimaryKey(Integer id);

    int insert(AuthorizationDetail record);

    int insertSelective(AuthorizationDetail record);

    AuthorizationDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AuthorizationDetail record);

    int updateByPrimaryKey(AuthorizationDetail record);

    List<AuthorizationDetail>
    selectByUserIdAndProjectId(String userId, String projectId);


    AuthorizationDetail selectByUserIdAndProjectIdAndName(String userId, String projectId, String policyName);

    int insertOrUpdate(AuthorizationDetail record);

    /**
     * 根据PolicyId 查询权限信息
     *
     * @param policyId
     * @return
     * @author li.hzh
     */
    List<AuthorizationDetail> selectByPolicyId(Long policyId);

    List<AuthorizationDetail> selectByProjectId(Long projectId);

    int isEnabled(int enabled, long id);
}