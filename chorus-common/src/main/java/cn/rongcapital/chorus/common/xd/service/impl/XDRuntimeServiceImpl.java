package cn.rongcapital.chorus.common.xd.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import cn.rongcapital.chorus.common.xd.service.XDRuntimeService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.xd.rest.client.SpringXDOperations;
import org.springframework.xd.rest.domain.DetailedContainerResource;

/**
 * @author yimin
 */
@Slf4j
@Service
public class XDRuntimeServiceImpl implements XDRuntimeService {
    private static final int[] EMPTY = new int[2];
    private final SpringXDOperations springXDOperations;
    private final int FREE = 0;
    private final int ON_SERVICE = 1;

    @Autowired
    public XDRuntimeServiceImpl(SpringXDOperations springXDOperations) {
        this.springXDOperations = springXDOperations;
    }

    @Override
    public int[] containersServiceStatusStats(@NonNull Long projectId) {
        final Collection<DetailedContainerResource> allContainers = springXDOperations.runtimeOperations().listContainers().getContent(); // default page size 10,000
        if (CollectionUtils.isEmpty(allContainers)) {
            log.info("there is no containers");
            return EMPTY;
        }

        final String groupPrefix = String.valueOf(projectId) + "_";

        final Stream<DetailedContainerResource> projectContainersStream = allContainers.parallelStream().filter(
                container -> Arrays.stream(StringUtils.split(container.getGroups(), ",")).filter(group -> StringUtils.startsWith(group, groupPrefix)).count() > 0);

        final Map<Integer, List<DetailedContainerResource>> groupByServiceStatus =
                projectContainersStream.collect(Collectors.groupingBy(this::serviceStatus));

        if (MapUtils.isEmpty(groupByServiceStatus)) {
            log.info("there is no containers of project " + projectId);
            return EMPTY;
        }

        final int[] statsResult = new int[2];

        List<DetailedContainerResource> tmp;
        statsResult[ON_SERVICE] = (tmp = groupByServiceStatus.get(ON_SERVICE)) == null ? 0 : tmp.size();
        statsResult[FREE] = (tmp = groupByServiceStatus.get(FREE)) == null ? 0 : tmp.size();

        return statsResult;
    }

    private int serviceStatus(DetailedContainerResource detailedContainerResource) {
        return detailedContainerResource.getDeploymentSize() > 0 ? ON_SERVICE : FREE;
    }
}
