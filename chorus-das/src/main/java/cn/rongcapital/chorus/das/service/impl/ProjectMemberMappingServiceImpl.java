package cn.rongcapital.chorus.das.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.ImmutableList;

import cn.rongcapital.chorus.das.dao.ProjectMemberMappingDOMapper;
import cn.rongcapital.chorus.das.dao.ProjectMemberMappingMapper;
import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.ProjectMemberCount;
import cn.rongcapital.chorus.das.entity.ProjectMemberMapping;
import cn.rongcapital.chorus.das.entity.ProjectMemberRoleCount;
import cn.rongcapital.chorus.das.service.ProjectMemberMappingService;

/**
 * Created by shicheng on 2016/11/25.
 */
@Service("ProjectMemberMappingService")
public class ProjectMemberMappingServiceImpl implements ProjectMemberMappingService {

    @Autowired
    private ProjectMemberMappingMapper projectMemberMappingMapper;

    @Autowired
    private ProjectMemberMappingDOMapper projectMemberMappingDOMapper;

    @Override
    public int deleteByPrimaryKey(Long projectMemberId) {
        if (projectMemberId != null) {
            return projectMemberMappingMapper.deleteByPrimaryKey(projectMemberId);
        }
        return 0;
    }

    @Override
    public int insert(ProjectMemberMapping record) {
        if (record != null) {
            // 由于caass系统的原因，导致chorus 跟cass两边数据不一致性，特加上这段逻辑，如果项目成员已经存在，则进行更新操作。
            ProjectInfoDO projectInfoDO = projectMemberMappingDOMapper
                    .selectMemberByProjectIdAndUserId(record.getProjectId(), record.getUserId());
            Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
            if (projectInfoDO == null) {
                record.setCreateTime(timeStamp);
                projectMemberMappingMapper.insert(record);
                return 1;
            } else {
                record.setProjectMemberId(projectInfoDO.getProjectMemberId());
                record.setUpdateTime(timeStamp);
                projectMemberMappingMapper.updateByPrimaryKey(record);
                return 2;
            }
        }
        return 0;
    }

    @Override
    public int updateByPrimaryKey(ProjectMemberMapping record) {
        if (record != null) {
            return projectMemberMappingMapper.updateByPrimaryKey(record);
        }
        return 0;
    }

    @Override
    public List<ProjectInfoDO> selectMemberByProjectId(Long projectId) {
        if (projectId != null) {
            return projectMemberMappingDOMapper.selectMemberByProjectId(projectId);
        }
        return null;
    }

    @Override
    public int selectCountMemberByProjectId(Long projectId) {
        return projectMemberMappingDOMapper.selectCountMemberByProjectId(projectId);
    }

    @Override
    public List<ProjectInfoDO> selectMemberByProjectIdAndRole(Long projectId, String roleId) {
        return projectMemberMappingDOMapper.selectMemberByProjectIdAndRole(projectId, roleId);
    }

    @Override
    public int deleteByProjectAndUserId(Long projectId, Long userId) {
        return projectMemberMappingDOMapper.deleteByProjectAndUserId(projectId, userId);
    }

    @Override
    public int updateByProjectAndUserId(ProjectMemberMapping vo) {
        if (vo == null)
            return 0;

        Timestamp updateTime = new Timestamp(System.currentTimeMillis());
        vo.setUpdateTime(updateTime);
        return projectMemberMappingDOMapper.updateByProjectAndUserId(vo);
    }

    @Override
    public ProjectMemberCount statProjectMember(Long projectId) {
        ProjectMemberCount result = new ProjectMemberCount();
        List<ProjectMemberRoleCount> counts = projectMemberMappingDOMapper.statProjectMember(projectId);
        if (CollectionUtils.isEmpty(counts)) {
            result.setDatas(new ArrayList<>());
            return result;
        }
        result.setDatas(counts);
        counts.forEach(count -> result.add(count.getCount()));
        return result;
    }

    @Override
    public List<ProjectInfoDO> getProjectByUser(String userId) {
        if (StringUtils.isBlank(userId))
            return ImmutableList.of();
        return projectMemberMappingMapper.selectProjectByUserId(userId);
    }

    @Override
    public ProjectInfoDO getProject(String userId, Long projectId) {
        return projectMemberMappingMapper.getProject(userId, projectId);
    }

    @Override
    public List<ProjectInfoDO> selectMembers(Long projectId, String roleId) {
        return projectMemberMappingDOMapper.selectMembers(projectId, roleId);
    }

    @Override
    public ProjectInfoDO selectMemberByProjectIdAndUserId(Long projectId, String userId){
        return projectMemberMappingDOMapper.selectMemberByProjectIdAndUserId(projectId, userId);
    }

}
