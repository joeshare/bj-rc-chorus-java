package cn.rongcapital.chorus.common.xd.service;

/**
 * @author yimin
 */
public interface XDRuntimeService {
    /**
     * 统计该项目{@code projectId}的所有容器的使用状态
     * <p>
     * 使用状态：
     * 0: container 当前部署的模块数=0
     * 1； container 当前部署的模块数>0
     *
     * @param projectId
     *
     * @return 总是返回二位数组; int[0] 代表未使用的container数量; int[1] 代表已使用的container数据;
     */
    int[] containersServiceStatusStats(Long projectId);
}
