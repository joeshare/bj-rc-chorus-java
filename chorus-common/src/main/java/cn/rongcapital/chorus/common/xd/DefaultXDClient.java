package cn.rongcapital.chorus.common.xd;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.xd.rest.client.SpringXDOperations;
import org.springframework.xd.rest.domain.DetailedModuleDefinitionWithPageElementResource;
import org.springframework.xd.rest.domain.JobDefinitionResource;
import org.springframework.xd.rest.domain.JobExecutionInfoResource;
import org.springframework.xd.rest.domain.RESTModuleType;
import org.springframework.xd.rest.domain.StreamDefinitionResource;

import cn.rongcapital.chorus.common.xd.exception.ChorusXDJobDeploymentException;
import cn.rongcapital.chorus.common.xd.model.JobDeploymentProperty;
import cn.rongcapital.chorus.common.xd.model.XDJobDefinitionResource;
import cn.rongcapital.chorus.common.xd.model.XDStreamDefinitionResource;

/**
 * @author li.hzh
 * @date 2016-11-14 15:11
 */
@Component
public class DefaultXDClient implements XDClient {

    private static Logger logger = LoggerFactory.getLogger(DefaultXDClient.class);

    private static final String DSL_TO_GRAPH_API = "/tools/parseJobToGraph";
    private static final String DSL_TO_GRAPH_API_CONTENT_TYPE = "text/plain;charset=UTF-8";
    private static final String GRAPH_TO_DSL_API = "/tools/convertJobGraphToText";
    private static final String GRAPH_TO_DSL_API_CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String GET_JOB_DEFINITION_BY_NAME_API = "/jobs/definitions/{definition}";
    private static final String GET_STREAM_DEFINITION_BY_NAME_API = "/streams/definitions/{definition}";

    @Autowired
    private SpringXDTemplateDecorator springXDOperations;

    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    private final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

    @Override
    public String dslToGraph(String dslText) {
        return doXDPost(DSL_TO_GRAPH_API, dslText, DSL_TO_GRAPH_API_CONTENT_TYPE);
    }

    @Override
    public String graphToDsl(String graphText) {
        return doXDPost(GRAPH_TO_DSL_API, graphText, GRAPH_TO_DSL_API_CONTENT_TYPE);
    }

    @Override
    public XDJobDefinitionResource createXDJobDefinition(String jobDefinitionName, String jobDefinition, boolean deployed) throws ChorusXDJobDeploymentException {
        try {
            JobDefinitionResource jobDefinitionResource = springXDOperations.jobOperations().createJob(jobDefinitionName, jobDefinition, deployed);
            if (jobDefinitionResource == null) {
                // XD的API即使成功也会返回null。
                return null;
            }
            XDJobDefinitionResource xdJobDefinitionResource = new XDJobDefinitionResource(jobDefinitionResource.getName(), jobDefinitionResource.getDefinition());
            return xdJobDefinitionResource;
        } catch (Throwable t) {
            logger.error("XD Job Definition [" + jobDefinitionName + "] 部署失败。", t);
            throw new ChorusXDJobDeploymentException("XD Job Definition [" + jobDefinitionName + "] 部署失败。", t);
        }
    }

    @Override
    public XDStreamDefinitionResource createXDStreamDefinition(String streamDefinitionName, String streamDefinition, boolean deployed) throws ChorusXDJobDeploymentException {
        try {
            StreamDefinitionResource streamDefinitionResource = springXDOperations.streamOperations().createStream(streamDefinitionName, streamDefinition, deployed);
            if (streamDefinitionResource == null) {
                return null;
            }
            XDStreamDefinitionResource xdStreamDefinitionResource = new XDStreamDefinitionResource(streamDefinitionResource);
            return xdStreamDefinitionResource;
        } catch (Throwable t) {
            logger.error("XD Stream Definition [" + streamDefinitionName + "] 部署失败。", t);
            throw new ChorusXDJobDeploymentException("XD Stream Definition [" + streamDefinitionName + "] 部署失败。", t);
        }
    }

    @Override
    public void deployJobDefinition(String jobDefinitionName, String groupName,
            List<JobDeploymentProperty> deploymentProperties) {
        Map<String, String> properties = createDeployProperties(groupName, deploymentProperties);
        springXDOperations.jobOperations().deploy(jobDefinitionName, properties);
    }

    @Override
    public void restartJobExecution(long jobExecutionId) {
        springXDOperations.jobOperations().restartJobExecution(jobExecutionId);
    }

    private Map<String, String> createDeployProperties(String groupName, List<JobDeploymentProperty> deploymentProperties) {
        Map<String, String> properties = new HashMap<>();
        String criteria = String.format("groups.equals('%s')", groupName);
        for (JobDeploymentProperty deploymentProperty : deploymentProperties) {
            String moduleCountKey = String.format("module.%s.count", deploymentProperty.getModuleName());
            String moduleCriteriaKey = String.format("module.%s.criteria", deploymentProperty.getModuleName());
            properties.put(moduleCountKey, deploymentProperty.getSize().toString());
            properties.put(moduleCriteriaKey, criteria);
        }
        return properties;
    }

    private Map<String, String> createStreamDeployProperties(List<JobDeploymentProperty> deploymentProperties) {
        Map<String, String> properties = new HashMap<>();
        for (JobDeploymentProperty deploymentProperty : deploymentProperties) {
            String moduleCountKey = String.format("module.%s.count", deploymentProperty.getModuleName());
            String moduleCriteriaKey = String.format("module.%s.criteria", deploymentProperty.getModuleName());
            properties.put(moduleCountKey, deploymentProperty.getSize().toString());
            properties.put(moduleCriteriaKey, String.format("groups.equals('%s')", deploymentProperty.getGroupName()));
        }
        return properties;
    }

    @Override
    public void deployStreamDefinition(String streamDefinitionName, String groupName,
            List<JobDeploymentProperty> deploymentProperties) {
        Map<String, String> properties = createStreamDeployProperties(deploymentProperties);
        springXDOperations.streamOperations().deploy(streamDefinitionName, properties);
    }

    @Override
    public void unDeployJobDefinition(String jobDefinitionName) {
        springXDOperations.jobOperations().undeploy(jobDefinitionName);
    }

    @Override
    public void destroyJobDefinition(String jobDefinitionName) {
        springXDOperations.jobOperations().destroy(jobDefinitionName);
    }

    @Override
    public void lanuchJob(String jobDefinitionName, String parameters) {
        springXDOperations.jobOperations().launchJob(jobDefinitionName, parameters);
    }
    
    @Override
    public void stopJob(long jobExecutionId) {
        springXDOperations.jobOperations().stopJobExecution(jobExecutionId);
    }
    
    @Override
    public JobExecutionInfoResource displayJobExecution(long jobExecutionId) {
        return springXDOperations.jobOperations().displayJobExecution(jobExecutionId);
    }

    @Override
    public XDJobDefinitionResource getJobDefinition(String jobDefinitionName) {
        String fullURI = springXDOperations.getXdAdminServerBaseURI().toString() + GET_JOB_DEFINITION_BY_NAME_API;
        JobDefinitionResource jobDefinitionResource = doGetDefinition(fullURI, JobDefinitionResource.class, jobDefinitionName);
        if (jobDefinitionResource != null) {
            return new XDJobDefinitionResource(jobDefinitionResource);
        }
        return null;
    }

    private <T> T doGetDefinition(String uri, Class<T> type, String definitionName) {
        try {
            logger.debug("获取任务定义[{}]。", definitionName);
            return restTemplate.getForObject(uri, type, definitionName);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                logger.warn("任务定义[{}]不存在。", definitionName);
            } else {
                // 其他情况暂时统一处理, 后续可具体问题具体分析。
                logger.error("获取任务定义[{}]失败。", definitionName);
                logger.error("错误信息。", e);
            }
            return null;
        }
    }

    @Override
    public boolean isJobExist(String jobDefinitionName) {
        return getJobDefinition(jobDefinitionName) != null;
    }

    @Override
    public boolean isStreamExist(String streamDefinitionName) {
        return getStreamDefinition(streamDefinitionName) != null;
    }

    private String doXDPost(String api, String requestBody, String contentType) {
        CloseableHttpClient httpClient = null;
        try {
            final String uri = springXDOperations.getXdAdminServerBaseURI().toString() + api;
            HttpPost post = new HttpPost(uri);
            post.setEntity(new StringEntity(requestBody));
            post.setHeader("Content-Type", contentType);
            logger.debug("XD http post address: {}。", uri);
            httpClient = httpClientBuilder.build();
            CloseableHttpResponse response = httpClient.execute(post);
            //TODO Status 状态处理
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            logger.error("XD http request error: {}", e);
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.error("Close httpclient error.", e);
                }
            }
        }
        return null;
    }

    @Override
    public XDStreamDefinitionResource getStreamDefinition(String streamDefinitionName) {
        String fullUri = springXDOperations.getXdAdminServerBaseURI().toString() + GET_STREAM_DEFINITION_BY_NAME_API;
        StreamDefinitionResource streamDefinitionResource = doGetDefinition(fullUri, StreamDefinitionResource.class, streamDefinitionName);
        if (streamDefinitionResource != null) {
            return new XDStreamDefinitionResource(streamDefinitionResource);
        }
        return null;
    }

    @Override
    public void destroyStream(String streamDefinitionName) {
        springXDOperations.streamOperations().destroy(streamDefinitionName);
    }

    @Override
    public SpringXDOperations getSpringXDTemplate() {
        return springXDOperations;
    }

    @Override
    public DetailedModuleDefinitionWithPageElementResource getModuleDefinitionWithPageElement(RESTModuleType moduleType,
            String moduleName) {
        return springXDOperations.moduleOperations().infoWithPageElement(moduleName, moduleType);
    }
}

