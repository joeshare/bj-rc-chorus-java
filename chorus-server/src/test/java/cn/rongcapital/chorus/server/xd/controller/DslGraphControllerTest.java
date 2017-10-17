package cn.rongcapital.chorus.server.xd.controller;

import org.junit.Test;

import java.io.IOException;

/**
 * @author li.hzh
 * @date 2016-11-15 15:43
 */
//TODO MockMVCTest
public class DslGraphControllerTest {

    @Test
    public void testGraphToDsl() throws IOException {
        /*
        String input = "{\"nodes\":[{\"name\":\"START\",\"id\":\"14c54cad-8525-4f57-879b-989c54212c60\"},{\"name\":\"END\",\"id\":\"bb8f2217-cc31-43b3-a2fd-442a726503cd\",\"properties\":{}},{\"name\":\"ly-hello-job\",\"id\":\"0a43b82d-91a4-4578-9c99-b7cd2eabcbb4\",\"properties\":{}},{\"name\":\"ly-hello-log\",\"id\":\"7f245383-3e32-45f0-b62e-faa91a7a51fd\",\"properties\":{}},{\"name\":\"FAIL\",\"id\":\"4978125d-6574-44be-8eff-308acb080c66\",\"properties\":{}},{\"name\":\"ly-hello\",\"id\":\"5ad4983a-9e68-4109-a7dd-ac49354260b9\",\"properties\":{}}],\"links\":[{\"from\":\"14c54cad-8525-4f57-879b-989c54212c60\",\"to\":\"0a43b82d-91a4-4578-9c99-b7cd2eabcbb4\"},{\"from\":\"0a43b82d-91a4-4578-9c99-b7cd2eabcbb4\",\"to\":\"bb8f2217-cc31-43b3-a2fd-442a726503cd\"},{\"from\":\"7f245383-3e32-45f0-b62e-faa91a7a51fd\",\"to\":\"4978125d-6574-44be-8eff-308acb080c66\"},{\"from\":\"14c54cad-8525-4f57-879b-989c54212c60\",\"to\":\"5ad4983a-9e68-4109-a7dd-ac49354260b9\"},{\"from\":\"5ad4983a-9e68-4109-a7dd-ac49354260b9\",\"to\":\"bb8f2217-cc31-43b3-a2fd-442a726503cd\"}],\"properties\":{}}";
        HttpClientBuilder builder = HttpClientBuilder.create();
        CloseableHttpClient client = builder.build();
        HttpPost post = new HttpPost("http://localhost:8080/tools/convertJobGraphToDsl");
//        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//        NameValuePair nvp = new BasicNameValuePair("text", input);
//        nvps.add(nvp);
//        HttpEntity reqEntity = new UrlEncodedFormEntity(nvps, Consts.UTF_8);
        post.setEntity(new StringEntity(input));
        post.setHeader("Content-type", "application/json;charset=UTF-8");
        HttpResponse response = client.execute(post);
        System.out.println("Result: " + EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testDslToGraph() throws IOException {
        String input = "<ly-hello-job & ly-hello> || ly-hello-log || FAIL";
        HttpClientBuilder builder = HttpClientBuilder.create();
        CloseableHttpClient client = builder.build();
        HttpPost post = new HttpPost("http://localhost:8080/tools/fromDslToGraph");
        post.setEntity(new StringEntity(input));
        post.setHeader("Content-type", "text/plain;charset=UTF-8");
        HttpResponse response = client.execute(post);
        System.out.println("Result: " + EntityUtils.toString(response.getEntity()));
        */
    }
}
