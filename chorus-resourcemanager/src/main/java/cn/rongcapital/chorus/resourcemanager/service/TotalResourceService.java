package cn.rongcapital.chorus.resourcemanager.service;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.entity.TotalResource;

/**
 * Created by alan on 05/01/2017.
 */
public interface TotalResourceService {
    /**
     * 获取对应状态的申请所占资源总数
     *
     * @param statusCode 状态: 待审批/通过/拒绝
     * @return 资源总数
     */
    TotalResource sumResourceByStatus(StatusCode statusCode);

    /**
     * 从Yarn中获取集群中所有节点的资源总数
     *
     * @return 资源总数
     */
    TotalResource getTotalResource();

    /**
     * 获取集群中未被分配到项目的硬件资源总数
     *
     * @return 资源总数
     */
    TotalResource getLeftResource();
    /**
     * 从Yarn中获取集群中所有节点的资源总数(依据chorus队列比例)
     * @return 资源总数
     */
    TotalResource getTotalResourceWithQueueCapacity();
    /**
     * 获取集群中未被分配到项目的硬件资源总数(依据chorus队列比例)
     *
     * @return 资源总数
     */
    TotalResource getLeftResourceWithQueueCapacity();
}
