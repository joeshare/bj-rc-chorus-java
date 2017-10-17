package cn.rongcapital.chorus.datalab.service.impl;

import cn.rongcapital.chorus.datalab.service.LabStatusService;
import io.fabric8.etcd.api.EtcdClient;
import io.fabric8.etcd.api.EtcdException;
import io.fabric8.etcd.api.Response;
import io.fabric8.etcd.core.EtcdClientImpl;
import io.fabric8.etcd.reader.jackson.JacksonResponseReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * Created by abiton on 16/03/2017.
 */
@Slf4j
@Service("LabStatusService")
public class LabStatusServiceImpl implements LabStatusService, InitializingBean, DisposableBean {

    @Value("${etcd.urls}")
    private String etcdUrls;
    @Value("${etcd.dictionary}")
    private String prefix;
    private EtcdClient client;

    @Override
    public boolean isAlive(String projectCode, String labCode) {

        try {
            Response response = client.getData().forKey(getProjectLabCode(projectCode, labCode));
            if (response.getNode() != null) {
                return true;
            }
        } catch (EtcdException e) {
            return false;
        }
        return false;
    }

    /**
     * @param projectCode
     * @param labCode
     * @return exam : {"applicationId":"application_1400299192_1211","url":"localhost:90883"}
     */
    @Override
    public String getStatus(String projectCode, String labCode) {

        try {
            Response response = client.getData().forKey(getProjectLabCode(projectCode, labCode));
            if (response.getNode() != null) {
                return response.getNode().getValue();
            }
        } catch (EtcdException e) {
            log.error("etcd client get key error", e);
        }
        return null;
    }

    @Override
    public void delete(String projectCode, String labCode) {
        try {
            client.delete().forKey(getProjectLabCode(projectCode, labCode));
        } catch (EtcdException e) {
            log.error("etcd client delete key error ", e);
        }
    }


    private String getProjectLabCode(String projectCode, String labCode) {
        return prefix + "/" + projectCode + "-" + labCode;
    }

    @Override
    public void destroy() throws Exception {
        client.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        client = new EtcdClientImpl.Builder().baseUri(new URI(etcdUrls))
                .responseReader(new JacksonResponseReader()).build();
        client.start();
    }
}
