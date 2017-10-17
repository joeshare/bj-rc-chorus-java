package cn.rongcapital.chorus.resourcemanager.service;

import cn.rongcapital.chorus.das.entity.InstanceInfo;
import cn.rongcapital.chorus.resourcemanager.ExecutionUnitGroup;
import org.springframework.yarn.support.console.ContainerClusterReport.ClustersInfoReportData;

/**
 * Created by abiton on 25/11/2016.
 */
public interface ExecutionUnitService {

    /**
     * 查看执行单元组状态
     *
     * @param instanceId 执行单元组Id
     */
    ClustersInfoReportData executionUnitGroupInfo(Long instanceId);

    /**
     * 创建执行单元组.
     *
     * @param unit   执行单元组定义Param.
     * @param userId 操作用户Id
     */
    Long executionUnitGroupCreate(ExecutionUnitGroup unit, String userId, String userName);

    /**
     * 修改执行单元组.
     *  @param instanceId   执行单元组Id
     * @param instanceSize 修改后的实例数量
     * @param instanceDesc
     * @param userId       操作用户Id
     */
    void executionUnitGroupModify(Long instanceId, Integer instanceSize, String instanceDesc, String userId, String userName);

    /**
     * 启动执行单元组.
     *
     * @param instanceId 执行单元组Id
     * @param userId     操作用户Id
     */
    void executionUnitGroupStart(Long instanceId, String userId, String userName);

    /**
     * 停止执行单元组.
     *
     * @param instanceId 执行单元组Id
     * @param userId     操作用户Id
     */
    void executionUnitGroupStop(Long instanceId, String userId, String userName);

    /**
     * 销毁执行单元组.
     *
     * @param instanceId 执行单元组Id
     * @param userId     操作用户Id
     */
    void executionUnitGroupDestroy(Long instanceId, String userId, String userName);

    String assembleClusterId(Long projectId, String groupName);
}
