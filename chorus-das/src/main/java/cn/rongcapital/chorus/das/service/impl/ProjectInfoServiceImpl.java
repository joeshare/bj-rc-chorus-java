package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.HadoopPathException;
import cn.rongcapital.chorus.common.hadoop.DefaultHadoopClient;
import cn.rongcapital.chorus.common.hadoop.HadoopClient;
import cn.rongcapital.chorus.common.hadoop.HadoopUtil;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.dao.ProjectInfoDOMapper;
import cn.rongcapital.chorus.das.dao.ProjectInfoMapper;
import cn.rongcapital.chorus.das.dao.ProjectMemberMappingMapper;
import cn.rongcapital.chorus.das.entity.*;
import cn.rongcapital.chorus.das.service.*;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目操作服务实现类
 * <p>
 * <p>实现项目的各项操作</p>
 *
 * @author shicheng
 * @version 1.0
 * @since <pre>十一月 23, 2016</pre>
 */
@Slf4j
@Service(value = "ProjectInfoService")
@Transactional
public class ProjectInfoServiceImpl implements ProjectInfoService {

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Autowired
    private ProjectMemberMappingMapper projectMemberMappingMapper;

    @Autowired
    private ProjectInfoDOMapper projectInfoDOMapper;

    @Autowired
    private HiveTableInfoServiceV2 hiveTableInfoServiceV2;

    @Autowired
    private ResourceInnerService resourceInnerService;

    @Autowired
    private HadoopClient hadoopClient;

    @Autowired
    private HdfsSnapshotService hdfsSnapshotService;

    @Autowired
    private ResourceOutService resourceOutService;
    @Autowired
    private AuthorizationDetailService authorizationDetailService;
    @Autowired
    private AuthorizationService authorizationService;

    /**
     * 创建项目
     *
     * @param record 项目信息
     * @return 受影响的行数
     */
    @Override
    public int insertSelective(ProjectInfo record) {
        if (record != null) {
            return projectInfoMapper.insertSelective(record);
        }
        return 0;
    }

    /**
     * 根据用户 ID 查询项目列表
     *
     * @param userId 用户 id
     * @return 项目列表
     */
    @Override
    public List<ProjectInfoDO> selectAllProjectByUserId(String userId, int pageNum, int pageSize) {
        if (StringUtils.isNotEmpty(userId)) {
            PageHelper.startPage(pageNum, pageSize);
            return projectInfoDOMapper.selectAllProjectByUserId(userId);
        }
        return null;
    }

    @Override
    public List<ProjectInfoDO> selectAllProjectByUserId(String userId) {
        if (StringUtils.isNotEmpty(userId)) {
            return projectInfoDOMapper.selectAllProjectByUserId(userId);
        }
        return null;
    }

    /**
     * 根据项目 ID 验证项目是否存在
     *
     * @param projectId 项目 ID
     * @return true | false
     */
    @Override
    public ProjectInfo selectByPrimaryKey(Long projectId) {
        if (projectId != null) {
            return projectInfoMapper.selectByPrimaryKey(projectId);
        }
        return null;
    }

    /**
     * 根据项目名称验证项目是否存在
     *
     * @param projectName 项目名称
     * @return true | false
     */
    @Override
    public ProjectInfo selectByProjectName(String projectName) {
        if (StringUtils.isNotEmpty(projectName.trim())) {
            return projectInfoDOMapper.selectByProjectName(projectName.trim());
        }
        return null;
    }

    /**
     * 根据项目编码验证项目是否存在
     *
     * @param projectCode 项目编码
     * @return true | false
     */
    @Override
    public ProjectInfo selectByProjectCode(String projectCode) {
        if (StringUtils.isNotEmpty(projectCode.trim())) {
            return projectInfoDOMapper.selectByProjectCode(projectCode.trim());
        }
        return null;
    }

    /**
     * 验证项目是否存在
     * <p>该验证会执行以下操作:</p>
     * <ul>
     * <li>1. 通过 ID 验证, 如果存在直接返回存在结果, 否则执行下一步</li>
     * <li>2. 通过名称验证, 如果存在直接返回存在结果, 否则执行下一步</li>
     * <li>3. 通过编码验证, 如果存在直接返回存在结果, 否则执行下一步</li>
     * </ul>
     *
     * @param object 验证数据
     * @return true | false
     */
    @Override
    public boolean validateProjectExists(String object) {
        if (StringUtils.isNotEmpty(object)) {
            if (NumberUtils.isNumber(object.trim())) { // 通过 ID 验证
                if (selectByPrimaryKey(Long.parseLong(object)) != null) {
                    return true;
                }
            }
            if (selectByProjectName(object.trim()) != null) { // 通过名称验证
                return true;
            }
            if (selectByProjectCode(object.trim()) != null) { // 通过编码验证
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean validateProjectExistsByCode(String projectCode) {
        ProjectInfo info = projectInfoDOMapper.selectByProjectCode(projectCode);
        if (info == null) {
            return false;
        }
        return true;
    }

    /**
     * 创建项目
     * <p>该操作将传递的数据结果直接入库</p>
     *
     * @param record 项目信息
     * @return 受影响的行数
     */
    @Override
    public int insert(ProjectInfo record) {
        if (record != null) {
            return projectInfoMapper.insert(record);
        }
        return 0;
    }

    /**
     * 更新项目
     * <p>该操作进行各个字段的非空验证, null 的字段数据将无法更新到数据库中</p>
     *
     * @param record 项目信息
     * @return 受影响的行数
     */
    @Override
    public int updateByPrimaryKeySelective(ProjectInfo record) {
        if (record != null) {
            return projectInfoMapper.updateByPrimaryKeySelective(record);
        }
        return 0;
    }

    /**
     * 更新项目
     * <p>该操作将传递的数据结果直接更新</p>
     *
     * @param record 项目信息
     * @return 受影响的行数
     */
    @Override
    public int updateByPrimaryKey(ProjectInfo record) {
        if (record != null) {
            return projectInfoMapper.updateByPrimaryKey(record);
        }
        return 0;
    }

    /**
     * 根据项目 ID 删除项目
     *
     * @param projectId 项目 ID
     * @return 受影响的行数
     */
    @Override
    public int deleteByPrimaryKey(Long projectId) {
        if (projectId != null) {
            return projectInfoMapper.deleteByPrimaryKey(projectId);
        }
        return 0;
    }

    /**
     * 根据项目名称删除项目
     *
     * @param projectName 项目名称
     * @return 受影响的行数
     */
    @Override
    public int deleteByProjectName(String projectName) {
        if (StringUtils.isNotEmpty(projectName.trim())) {
            return projectInfoDOMapper.deleteByProjectName(projectName);
        }
        return 0;
    }

    /**
     * 根据项目编码删除项目
     *
     * @param projectCode 项目编码
     * @return 受影响的行数
     */
    @Override
    public int deleteByProjectCode(String projectCode) {
        if (StringUtils.isNotEmpty(projectCode.trim())) {
            return projectInfoDOMapper.deleteByProjectCode(projectCode);
        }
        return 0;
    }

    /**
     * 根据条件查询项目信息列表
     *
     * @param projectName 项目名称
     * @param projectCode 项目编码
     * @param userId
     * @param isAccurate  是否精确查询
     * @param pageNum
     * @param pageSize    @return 项目列表
     */
    @Override
    public List<ProjectInfo> selectProjectInfoByCondition(String projectName, String projectCode, String userId, boolean isAccurate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return projectInfoDOMapper.selectProjectInfoByCondition(projectName, projectCode, userId, isAccurate);
    }

    /**
     * 添加数据返回主键
     *
     * @param record 数据记录
     * @return 项目信息
     */
    @Override
    public ProjectInfo insertAndGetKey(ProjectInfo record) {
        if (record != null) {
            projectInfoDOMapper.insertAndGetId(record);
            ProjectMemberMapping projectMemberMapping = new ProjectMemberMapping();
            projectMemberMapping.setProjectId(record.getProjectId());
            projectMemberMapping.setUserId(record.getCreateUserId());
            projectMemberMapping.setRoleId("4");
            projectMemberMappingMapper.insert(projectMemberMapping);
            hadoopClient.mkdir(new Path("/user/"+record.getUserName()),new FsPermission(FsAction.ALL,FsAction.READ_EXECUTE,FsAction.READ_EXECUTE),record.getUserName());
            hiveTableInfoServiceV2.createDb(record, record.getUserName());
            initUserHdfsDir(record);
            return record;
        }
        return null;
    }

    @Override
    public void canceledProjectAndReleaseResource(Long projectId , String updateUserId) throws Exception {
        log.info("Start release resource project id =====> "+ projectId);
        ResourceInner resourceInner = resourceInnerService.getByProjectId(projectId);

        if (resourceInner != null && !StatusCode.RESOURCE_DESTROY.getCode().equals(resourceInner.getStatusCode())) {
            resourceInner.setStatusCode(StatusCode.RESOURCE_DESTROY.getCode());
            resourceInner.setUpdateTime(new Date());
            resourceInnerService.updateByPrimaryKeySelective(resourceInner);
            log.info("release inner resource finished.");
        }

        List<ResourceOut> resourceOuts = resourceOutService.selectAllResourceOutByProjectId(projectId);
        resourceOuts.forEach(resourceOut -> {
            if(!StatusCode.RESOURCE_DESTROY.getCode().equals(resourceOut.getStatusCode())) {
                ResourceOut resourceOutNew = new ResourceOut();
                resourceOutNew.setResourceOutId(resourceOut.getResourceOutId());
                resourceOutNew.setUpdateTime(new Date());
                resourceOutNew.setUpdateUserId(updateUserId);
                resourceOutNew.setStatusCode(StatusCode.RESOURCE_DESTROY.getCode());
                resourceOutService.updateByPrimaryKeySelective(resourceOutNew);
            }
        });
        log.info("release out resource finished.");

        disableAuthorization(projectId);
        log.info("disabled authorization finished.");
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setProjectId(projectId);
        projectInfo.setStatusCode(StatusCode.PROJECT_IS_DELETED.getCode());
        projectInfo.setUpdateTime(new Date());
        projectInfo.setUpdateUserId(updateUserId);
        updateByPrimaryKeySelective(projectInfo);
        log.info("release resource of project[{}] finished.", projectId);
    }

    private boolean disableAuthorization(long projectId)throws Exception{
        List<AuthorizationDetail> authorizationDetails = authorizationDetailService.selectByProjectId(projectId);
        for(AuthorizationDetail authorizationDetail : authorizationDetails){
            if(authorizationDetail.getEnabled()==0) continue;
            try {
                authorizationService.disable(authorizationDetail.getPolicyId(), AuthorizationDetailCategory.valueOf(authorizationDetail.getCategory()));
                authorizationDetailService.isEnabled(0,authorizationDetail.getId());
            }catch (Exception ex){
                log.error("disable authorization failed. policyId[{}], id[{}].", authorizationDetail.getPolicyId(), authorizationDetail.getId());
                throw ex;
            }
        }

        return true;
    }

    private List<ProjectInfoDO> filterByOpened(List<ProjectInfoDO> projectListByUserId) {
        return projectListByUserId.stream().filter(p -> p.getStatusCode().equals(StatusCode.PROJECT_IS_OPENED.getCode())).collect(Collectors.toList());
    }

    private void initUserHdfsDir(ProjectInfo project) {
         String dir = HadoopUtil.formatPath(project.getProjectCode());
        // 分别创建目录:
        // /chorus/project/projectcode $projectOwner hadoop 750
        // /chorus/project/projectcode/tmp 777
        // /chorus/project/projectcode/hive $projectOwner hadoop 770
        if (!hadoopClient.mkdir(dir, project.getUserName())) {
            throw new HadoopPathException(StatusCode.PROJECT_HDFS_PATH_CREATE_ERROR);
        }
        if (!hadoopClient.mkdir(HadoopUtil.appendPath(dir, "tmp"),
                DefaultHadoopClient.FILESYSTEM_PROJECT_PERMISSION_777, project.getUserName())) {
            throw new HadoopPathException(StatusCode.PROJECT_HDFS_PATH_CREATE_ERROR);
        }
        if (!hadoopClient.mkdir(HadoopUtil.appendPath(dir, "hive"),
                DefaultHadoopClient.FILESYSTEM_PROJECT_PERMISSION_770, project.getUserName())) {
            throw new HadoopPathException(StatusCode.PROJECT_HDFS_PATH_CREATE_ERROR);
        }
        if (!hadoopClient.mkdir(HadoopUtil.appendPath(dir, "hdfs"),
                DefaultHadoopClient.FILESYSTEM_PROJECT_PERMISSION_770, project.getUserName())) {
            throw new HadoopPathException(StatusCode.PROJECT_HDFS_PATH_CREATE_ERROR);
        }
        if(!hdfsSnapshotService.allowSnapshot(dir)){
            throw new HadoopPathException(StatusCode.PROJECT_HDFS_ALLOW_SNAPSHOT_ERROR);
        };
    }

    public List<ProjectInfo> queryAll(){
        return projectInfoDOMapper.queryAll();
    }
    
    @Override
    public List<ProjectInfo> queryAllActive() {
        return projectInfoDOMapper.queryAllActive();
    }
}
