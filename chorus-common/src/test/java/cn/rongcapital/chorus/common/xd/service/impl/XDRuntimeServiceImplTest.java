package cn.rongcapital.chorus.common.xd.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.xd.rest.client.RuntimeOperations;
import org.springframework.xd.rest.client.SpringXDOperations;
import org.springframework.xd.rest.domain.DetailedContainerResource;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author yimin
 */
public class XDRuntimeServiceImplTest {
    @InjectMocks XDRuntimeServiceImpl xdRuntimeService;
    @Mock SpringXDOperations springXDOperations;
    @Mock RuntimeOperations runtimeOperations;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(springXDOperations.runtimeOperations()).thenReturn(runtimeOperations);
    }

    @Test
    public void containersServiceStatusStats() throws Exception {
        Long projectId = 209L;
        m(projectId, 2, 3, 1);
    }

    @Test
    public void thereIsNoContainers() throws Exception {
        Long projectId = 209L;
        m(projectId, 0, 0, 0);
    }

    @Test
    public void thereIsNoContainersForProject() throws Exception {
        Long projectId = 209L;
        m(projectId, 0, 0, 2);
    }

    @Test
    public void onlyFreeContainersForProject() throws Exception {
        Long projectId = 209L;
        m(projectId, 1, 0, 2);
    }

    private void m(Long projectId, int free, int onService, int others) {
        PagedResources<DetailedContainerResource> container = build(projectId + "_", free, onService, others);
        when(runtimeOperations.listContainers()).thenReturn(container);

        final int[] ints = xdRuntimeService.containersServiceStatusStats(projectId);

        assertThat(ints[0], is(free));
        assertThat(ints[1], is(onService));
        verify(runtimeOperations, new Times(1)).listContainers();
    }

    private PagedResources<DetailedContainerResource> build(String groupPrefix, int free, int onService, int others) {
        final List<DetailedContainerResource> freeContainers = IntStream.range(0, free).mapToObj(value -> free(groupPrefix)).collect(Collectors.toList());
        final List<DetailedContainerResource> onServiceContainers = IntStream.range(0, onService).mapToObj(value -> onService(groupPrefix)).collect(Collectors.toList());
        final List<DetailedContainerResource> othersContainers = IntStream.range(0, others).mapToObj(value -> others()).collect(Collectors.toList());

        Collection<DetailedContainerResource> all = new ArrayList<>();
        all.addAll(onServiceContainers);
        all.addAll(othersContainers);
        all.addAll(freeContainers);
        return new PagedResources<>(all, null, Lists.newArrayList(new Link("/runtime/containers")));
    }

    private DetailedContainerResource others() {
        Map<String, String> attribute = new HashMap<>();
        attribute.put("groups", RandomStringUtils.randomAlphabetic(9));
        return new DetailedContainerResource(attribute, RandomUtils.nextInt(0, 20), null, null);
    }

    private DetailedContainerResource onService(String groupPrefix) {
        Map<String, String> attribute = new HashMap<>();
        attribute.put("groups", groupPrefix + RandomStringUtils.randomAlphabetic(5) + "," + groupPrefix + RandomStringUtils.randomAlphabetic(5));
        return new DetailedContainerResource(attribute, RandomUtils.nextInt(1, 20), null, null);
    }

    private DetailedContainerResource free(String groupPrefix) {
        Map<String, String> attribute = new HashMap<>();
        attribute.put("groups", groupPrefix + RandomStringUtils.randomAlphabetic(5));
        return new DetailedContainerResource(attribute, 0, null, null);
    }

}
