package cn.rongcapital.chorus.common.xd;

import java.util.List;
import cn.rongcapital.chorus.common.xd.exception.ChorusXDJobDeploymentException;
import cn.rongcapital.chorus.common.xd.model.JobDeploymentProperty;
import cn.rongcapital.chorus.common.xd.model.XDJobDefinitionResource;
import cn.rongcapital.chorus.common.xd.model.XDStreamDefinitionResource;
import org.springframework.xd.rest.client.SpringXDOperations;
import org.springframework.xd.rest.domain.DetailedModuleDefinitionWithPageElementResource;
import org.springframework.xd.rest.domain.JobExecutionInfoResource;
import org.springframework.xd.rest.domain.RESTModuleType;

/**
 * SpringXD Rest Client
 *
 * @author li.hzh
 * @date 2016-11-14 14:23
 */
public interface XDClient {
    
    /**
     * 从DSL定义转换成图形定义
     *
     * @param text DSL定义
     * @return 在错误的情况下, 返回值可能为null
     */
    String dslToGraph(String text);
    
    /**
     * 从图形定义到DSL定义
     *
     * @param graphText 图形定义
     * @return 在错误的情况下, 返回值可能为null
     */
    String graphToDsl(String graphText);
    
    /**
     * 创建任务实例
     *
     * @param jobDefinitionName 任务定义名字,需要全局唯一
     * @param jobDefinition     任务定义
     * @param deployed          是否自动部署
     * @return 创建实例结果, 根据底层XD接口返回情况, 有可能为null。
     * @throws ChorusXDJobDeploymentException 部署失败抛出该异常
     */
    XDJobDefinitionResource createXDJobDefinition(String jobDefinitionName, String jobDefinition, boolean deployed) throws ChorusXDJobDeploymentException;
    
    /**
     * 创建Stream 实例
     *
     * @param streamDefinitionName Stream定义名字
     * @param streamDefinition     Stream定义字符串
     * @param deployed             是否自动部署
     * @return
     */
    XDStreamDefinitionResource createXDStreamDefinition(String streamDefinitionName, String streamDefinition, boolean deployed) throws ChorusXDJobDeploymentException;
    
    /**
     * 部署任务
     *
     * @param jobDefinitionName    任务定义名字,需要全局唯一
     * @param groupName            执行单元组名称
     * @param deploymentProperties 部署参数, 包含: { module: xd module名称, size: module需要部署的instance数量}
     */
    void deployJobDefinition(String jobDefinitionName, String groupName,
                             List<JobDeploymentProperty> deploymentProperties);

    /**
     * 重启任务
     *
     * @param jobExecutionId    jobExecutionId
     */
    void restartJobExecution(long jobExecutionId);
    
    /**
     * 部署Stream任务
     */
    void deployStreamDefinition(String streamDefinitionName, String groupName,
                                List<JobDeploymentProperty> deploymentProperties);
    
    /**
     * 卸载任务定义
     *
     * @param jobDefinitionName
     */
    void unDeployJobDefinition(String jobDefinitionName);
    
    /**
     * 删除任务定义
     *
     * @param jobDefinitionName
     */
    void destroyJobDefinition(String jobDefinitionName);
    
    
    /**
     * 获取Job Definition定义
     * 由于XD接口限制,目前仅能返回状态信息
     *
     * @param jobDefinitionName 定义名
     * @return 如果定义不存在返回NULL
     */
    XDJobDefinitionResource getJobDefinition(String jobDefinitionName);
    
    /**
     * 单次执行任务
     *
     * @param jobDefinitionName 任务定义名称
     * @param parameters        任务运行参数
     */
    void lanuchJob(String jobDefinitionName, String parameters);
    
    /**
     * 停止任务执行
     * @param jobExecutionId 任务执行ID
     */
    void stopJob(long jobExecutionId);
    
    /**
     * 检索任务执行信息
     * @param jobExecutionId 任务执行ID
     * @return 任务执行信息
     */
    public JobExecutionInfoResource displayJobExecution(long jobExecutionId);
    
    /**
     * 判断Job定义是否存在
     *
     * @param jobDefinitionName
     * @return
     */
    boolean isJobExist(String jobDefinitionName);
    
    /**
     * 判断Stream定义是否存在
     *
     * @param streamDefinitionName
     * @return true 存在; false 不存在
     */
    boolean isStreamExist(String streamDefinitionName);
    
    /**
     * 获取Stream 定义
     *
     * @param streamDefinitionName
     * @return
     */
    XDStreamDefinitionResource getStreamDefinition(String streamDefinitionName);
    
    /**
     * 删除Stream定义
     *
     * @param streamDefinitionName
     */
    void destroyStream(String streamDefinitionName);

    SpringXDOperations getSpringXDTemplate();

    /**
     * 获取Module参数定义, 包含通用页面元素配置
     */
    DetailedModuleDefinitionWithPageElementResource getModuleDefinitionWithPageElement(RESTModuleType moduleType, String moduleName);
}
