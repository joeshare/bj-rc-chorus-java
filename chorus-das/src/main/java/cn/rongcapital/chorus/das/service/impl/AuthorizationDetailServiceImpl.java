package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.AuthorizationDetailDOMapper;
import cn.rongcapital.chorus.das.dao.AuthorizationDetailMapper;
import cn.rongcapital.chorus.das.entity.AuthorizationDetail;
import cn.rongcapital.chorus.das.service.AuthorizationDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by shicheng on 2017/4/24.
 */
@Service(value = "AuthorizationDetailService")
public class AuthorizationDetailServiceImpl implements AuthorizationDetailService {

    @Autowired
    private AuthorizationDetailMapper authorizationDetailMapper;

    @Autowired
    private AuthorizationDetailDOMapper authorizationDetailDOMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return authorizationDetailMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(AuthorizationDetail record) {
        return authorizationDetailMapper.insert(record);
    }

    @Override
    public int insertSelective(AuthorizationDetail record) {
        return authorizationDetailMapper.insertSelective(record);
    }


    @Override
    public AuthorizationDetail selectByPrimaryKey(Integer id) {
        return authorizationDetailMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(AuthorizationDetail record) {
        return authorizationDetailMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(AuthorizationDetail record) {
        return authorizationDetailMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<AuthorizationDetail> selectByUserIdAndProjectId(String userId, String projectId) {
        return authorizationDetailDOMapper.selectByUserIdAndProjectId(userId, projectId);
    }

    @Override
    public AuthorizationDetail selectByUserIdAndProjectIdAndName(String userId, String projectId, String policyName) {
        return authorizationDetailDOMapper.selectByUserIdAndProjectIdAndName(userId, projectId, policyName);
    }

    @Override
    public int insertOrUpdate(AuthorizationDetail record) {
        if (record == null)
            return 0;

        AuthorizationDetail authorizationDetail = authorizationDetailDOMapper.selectByUserIdAndProjectIdAndName(record.getUserId(), record.getProjectId().toString(), record.getPolicyName());
        if (authorizationDetail == null) {
            authorizationDetailMapper.insert(record);
            return 1;
        } else {
            record.setId(authorizationDetail.getId());
            authorizationDetailMapper.updateByPrimaryKey(record);
            return 2;
        }
    }

    @Override
    public List<AuthorizationDetail> selectByPolicyId(Long policyId) {
        return authorizationDetailDOMapper.selectByPolicyId(policyId);
    }

    @Override
    public List<AuthorizationDetail> selectByProjectId(Long projectId) {
        return authorizationDetailDOMapper.selectByProjectId(projectId);
    }

    @Override
    public int isEnabled(int enabled, long id) {
        return authorizationDetailDOMapper.isEnabled(enabled,id);
    }
}
