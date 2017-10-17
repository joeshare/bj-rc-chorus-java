package cn.rongcapital.chorus.common.xd;

import java.net.URI;
import java.util.List;
import cn.rongcapital.chorus.common.util.JsonUtils;
import cn.rongcapital.chorus.common.xd.model.XDAdminConfig;
import cn.rongcapital.chorus.common.xd.model.XDAdminNodeData;
import cn.rongcapital.chorus.common.zk.ZKClient;
import cn.rongcapital.chorus.common.zk.ZKWatcher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.xd.rest.client.AggregateCounterOperations;
import org.springframework.xd.rest.client.CompletionOperations;
import org.springframework.xd.rest.client.CounterOperations;
import org.springframework.xd.rest.client.FieldValueCounterOperations;
import org.springframework.xd.rest.client.GaugeOperations;
import org.springframework.xd.rest.client.JobOperations;
import org.springframework.xd.rest.client.ModuleOperations;
import org.springframework.xd.rest.client.RichGaugeOperations;
import org.springframework.xd.rest.client.RuntimeOperations;
import org.springframework.xd.rest.client.SpringXDOperations;
import org.springframework.xd.rest.client.StreamOperations;
import org.springframework.xd.rest.client.impl.SpringXDTemplate;

/**
 * @author yimin
 */
@Slf4j
public class SpringXDTemplateDecorator implements SpringXDOperations, ZKWatcher {

    private final ClientHttpRequestFactory factory;
    private final String rabbitMqAdminUri;
    private final String rabbitMqPassword;
    private final String rabbitMqUsername;
    private final String rabbitMqVhost;
    private final String busPrefix;

    private volatile SpringXDOperations springXDOperations;

    @Getter
    private volatile URI xdAdminServerBaseURI;

    private final XDAdminConfig xdAdminConfig;
    private final ZKClient zkClient;

    public SpringXDTemplateDecorator(XDAdminConfig xdAdminConfig, ZKClient zkClient) {
        this(xdAdminConfig, zkClient, null);
    }

    // copy from SpringXDTemplate constructor
    public SpringXDTemplateDecorator(XDAdminConfig xdAdminConfig, ZKClient zkClient, URI baseURI) {
        this(xdAdminConfig, zkClient, baseURI, null, null, null, null, null);
    }

    // copy from SpringXDTemplate constructor
    public SpringXDTemplateDecorator(XDAdminConfig xdAdminConfig, ZKClient zkClient, ClientHttpRequestFactory factory, URI baseURI) {
        this(xdAdminConfig, zkClient, factory, baseURI, null, null, null, null, null);
    }

    // copy from SpringXDTemplate constructor
    public SpringXDTemplateDecorator(XDAdminConfig xdAdminConfig, ZKClient zkClient, URI baseURI, String rabbitMqAdminUri,
            String rabbitMqPassword, String rabbitMqUsername, String rabbitMqVhost, String busPrefix) {
        this(xdAdminConfig, zkClient, new SimpleClientHttpRequestFactory(), baseURI, rabbitMqAdminUri, rabbitMqPassword, rabbitMqUsername,
                rabbitMqVhost, busPrefix);
    }

    // local store all params, and create target
    public SpringXDTemplateDecorator(XDAdminConfig xdAdminConfig, ZKClient zkClient, ClientHttpRequestFactory factory, URI baseURI, String rabbitMqAdminUri,
            String rabbitMqPassword, String rabbitMqUsername, String rabbitMqVhost, String busPrefix) {
        this.xdAdminConfig = xdAdminConfig;
        this.zkClient = zkClient;

        this.factory = factory;
        this.xdAdminServerBaseURI = baseURI;
        this.rabbitMqAdminUri = rabbitMqAdminUri;
        this.rabbitMqPassword = rabbitMqPassword;
        this.rabbitMqUsername = rabbitMqUsername;
        this.rabbitMqVhost = rabbitMqVhost;
        this.busPrefix = busPrefix;

        if (null == xdAdminServerBaseURI || StringUtils.isBlank(this.xdAdminServerBaseURI.toString()))
            discoverSpringXdServerAndRegisterWatcher();

        this.springXDOperations = buildSpringXDOperationsTarget();
    }

    private void discoverSpringXdServerAndRegisterWatcher() {
        try {
            String adminNodePath = xdAdminConfig.getZkBasePath() + "/admins";

            List<String> adminNodeList = zkClient.getChildren(adminNodePath, this);
            if (adminNodeList == null || adminNodeList.isEmpty()) {
                log.warn("没有可用的XD Admin服务。");
                return;
            }
            if (adminNodeList.size() > 1) {
                log.warn("存在多个XD Admin服务,当前仅支持获取第一个节点信息。");
            }
            String adminNode = adminNodeList.get(0);
            String nodePath = adminNodePath + "/" + adminNode;

            byte[] nodeData = zkClient.getData(nodePath, this);
            String jsonStr = new String(nodeData);
            log.info("从ZooKeeper中获取到新的XDAdmin配置: {}", jsonStr);
            XDAdminNodeData xdAdminNodeData = JsonUtils.Json2Object(jsonStr, XDAdminNodeData.class);
            this.xdAdminServerBaseURI = new URI(xdAdminNodeData.adminAddress());
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    private SpringXDTemplate buildSpringXDOperationsTarget() {
        return new SpringXDTemplate(factory, xdAdminServerBaseURI, rabbitMqAdminUri, rabbitMqPassword, rabbitMqUsername, rabbitMqVhost, busPrefix);
    }

    @Override
    public void process(WatchedEvent event) {
        if (springXDAdminServerChanged(event)) {
            log.info("spring xd admin server nodes changed");
            discoverSpringXdServerAndRegisterWatcher();
            this.springXDOperations = buildSpringXDOperationsTarget();
        }
    }

    private boolean springXDAdminServerChanged(WatchedEvent event) {
        log.info("zk watched event: {}", event);
        final EventType eventType = event.getType();
        return eventType.equals(Event.EventType.NodeChildrenChanged)
                || eventType.equals(Event.EventType.NodeDataChanged);
    }

    @Override public StreamOperations streamOperations() {
        SpringXDOperations local = springXDOperations;
        return local.streamOperations();
    }

    @Override public JobOperations jobOperations() {
        SpringXDOperations local = springXDOperations;
        return local.jobOperations();
    }

    @Override public ModuleOperations moduleOperations() {
        SpringXDOperations local = springXDOperations;
        return local.moduleOperations();
    }

    @Override public RuntimeOperations runtimeOperations() {
        SpringXDOperations local = springXDOperations;
        return local.runtimeOperations();
    }

    @Override public CounterOperations counterOperations() {
        SpringXDOperations local = springXDOperations;
        return local.counterOperations();
    }

    @Override public FieldValueCounterOperations fvcOperations() {
        SpringXDOperations local = springXDOperations;
        return local.fvcOperations();
    }

    @Override public AggregateCounterOperations aggrCounterOperations() {
        SpringXDOperations local = springXDOperations;
        return local.aggrCounterOperations();
    }

    @Override public GaugeOperations gaugeOperations() {
        SpringXDOperations local = springXDOperations;
        return local.gaugeOperations();
    }

    @Override public RichGaugeOperations richGaugeOperations() {
        SpringXDOperations local = springXDOperations;
        return local.richGaugeOperations();
    }

    @Override public CompletionOperations completionOperations() {
        SpringXDOperations local = springXDOperations;
        return local.completionOperations();
    }
}
