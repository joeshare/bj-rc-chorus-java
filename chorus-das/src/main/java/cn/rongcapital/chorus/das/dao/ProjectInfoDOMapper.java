package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProjectInfoDOMapper {

    /**
     * 根据用户 ID 查询项目列表
     *
     * @return 项目列表
     */
    List<ProjectInfoDO> selectAllProjectByUserId(String userId);

    /**
     * 根据项目名称查询项目
     *
     * @param projectName 项目名称
     * @return 该名称的项目信息
     */
    ProjectInfo selectByProjectName(String projectName);

    /***
     * 根据项目编码查询项目
     *
     * @param projectCode 项目编码
     * @return 该编码的项目信息
     */
    ProjectInfo selectByProjectCode(String projectCode);

    /**
     * 根据项目名称删除项目
     *
     * @param projectName 项目名称
     * @return 受影响的行数
     */
    int deleteByProjectName(String projectName);

    /**
     * 根据项目编码删除项目
     *
     * @param projectCode 项目编码
     * @return 受影响的行数
     */
    int deleteByProjectCode(String projectCode);

    /**
     * 根据条件查询项目信息列表
     *
     * @param projectName 项目名称
     * @param projectCode 项目编码
     * @return 项目列表
     */
    List<ProjectInfo> selectProjectInfoByCondition(@Param("projectName") String projectName,
                                                   @Param("projectCode") String projectCode,
                                                   @Param("userId") String userId,
                                                   @Param("isAccurate") boolean isAccurate);

    /**
     * 添加数据, 返回数据 id
     *
     * @param record 数据信息
     * @return
     */
    int insertAndGetId(ProjectInfo record);

    List<ProjectInfo> queryAll();
    
    /**
     * 查询所有未删除的project
     * 
     * @return
     * @author yunzhong
     * @time 2017年8月29日下午5:27:52
     */
    List<ProjectInfo> queryAllActive();
}