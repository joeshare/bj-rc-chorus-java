package cn.rongcapital.chorus.server.xd.controller;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.xd.service.XDRuntimeService;
import cn.rongcapital.chorus.das.entity.InstanceInfo;
import cn.rongcapital.chorus.das.service.InstanceInfoService;
import cn.rongcapital.chorus.server.vo.ResultVO;
import cn.rongcapital.chorus.server.xd.controller.vo.ContainersServiceStatusStatsVO;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yimin
 */
@Slf4j
@RestController
@RequestMapping(RuntimeResources.PROJECT_RESTFUL_TEMPLATE)
public class RuntimeResources {
    public static final String PROJECT_RESTFUL_TEMPLATE = "/project/{projectId}";
    public static final String CONTAINERS_STATS         = "/containers/stats";

    private final XDRuntimeService    xdRuntimeService;
    private final InstanceInfoService instanceInfoService;

    @Autowired
    public RuntimeResources(XDRuntimeService xdRuntimeService, InstanceInfoService instanceInfoService) {
        this.xdRuntimeService = xdRuntimeService;
        this.instanceInfoService = instanceInfoService;
    }

    @RequestMapping(CONTAINERS_STATS)
    public ResultVO<ContainersServiceStatusStatsVO> containersStats(@PathVariable @NonNull Long projectId) {
        try {
            //available[created and started] instance stat
            int[] sum = xdRuntimeService.containersServiceStatusStats(projectId);
            final int started = sum[0] + sum[1];
            final List<InstanceInfo> allInstance = instanceInfoService.listByProject(projectId);//all instance, created, started or un-started
            final int unStarted = (allInstance == null ? 0 :
                                    allInstance.stream()
                                                     .filter(instanceInfo -> !StringUtils.equals(StatusCode.EXECUTION_UNIT_DESTROYED.getCode(), instanceInfo.getStatusCode()))
                                                     .mapToInt(InstanceInfo::getInstanceSize).sum()) - started;
            sum[0] = sum[0] + unStarted;
            final ContainersServiceStatusStatsVO stats = ContainersServiceStatusStatsVO.builder()
                                                                                       .projectId(projectId)
                                                                                       .remaining(sum[0])
                                                                                       .used(sum[1])
                                                                                       .timestamp(System.currentTimeMillis())
                                                                                       .build();
            return ResultVO.success(stats);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            return ResultVO.error(new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getLocalizedMessage()));
        }
    }

}
