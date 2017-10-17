package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.AuthorizationDetail;
import org.apache.ibatis.annotations.Param;
import scala.Int;

import java.util.List;

public interface AuthorizationDetailDOMapper {

    List<AuthorizationDetail> selectByUserIdAndProjectId(@Param("userId") String userId,
                                                         @Param("projectId") String projectId);

    AuthorizationDetail selectByUserIdAndProjectIdAndName(@Param("userId") String userId,
                                                          @Param("projectId") String projectId,
                                                          @Param("policyName") String policyName);

    List<AuthorizationDetail> selectByPolicyId(@Param("policyId") Long policyId);

    List<AuthorizationDetail> selectByProjectId(@Param("projectId") Long projectId);

    int isEnabled(@Param("enabled") int enabled, @Param("id") long id);
}