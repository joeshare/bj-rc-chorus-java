package cn.rongcapital.chorus.datalab.service.impl;

import com.alibaba.fastjson.JSON;
import io.fabric8.etcd.api.EtcdClient;
import io.fabric8.etcd.api.Response;
import io.fabric8.etcd.core.EtcdClientImpl;
import io.fabric8.etcd.reader.jackson.JacksonResponseReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

/**
 * Created by abiton on 15/03/2017.
 */
public class EtcdClientTest {

    EtcdClient client;

    @Before
    public void init() throws URISyntaxException {
        client =  new EtcdClientImpl.Builder()
                .responseReader(new JacksonResponseReader()).baseUri(new URI("http://192.168.199.78:2379"))
                .build();
        client.start();
    }

    @After
    public void end() {
        client.stop();
    }

    @Test
    public void testPut() throws ExecutionException, InterruptedException {
        String key = ("/myapp/database/user");
        Modle modle = new Modle();
        modle.setApplicationId("applicationId_1244314324_12");
        modle.setUrl("localhost:80882");
        String jsonString = JSON.toJSONString(modle);
        client.setData().value(jsonString).forKey(key);

        Response response = client.getData().forKey(key);
        System.out.println(response.getNode().getValue());
    }

    @Test
    public void testDelete() throws ExecutionException, InterruptedException {
        String key = ("/myapp/database/user");
        client.delete().forKey(key);
        client.delete().forKey(key);
    }

    private class Modle{
        String applicationId;
        String url;

        public String getApplicationId() {
            return applicationId;
        }

        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}

