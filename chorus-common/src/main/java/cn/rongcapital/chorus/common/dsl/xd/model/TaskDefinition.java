package cn.rongcapital.chorus.common.dsl.xd.model;

import java.util.List;
import java.util.Map;
/**
 * 关联自定义 DSL 任务定义对象
 * @author Lovett
 */
public class TaskDefinition {
    private String name;
    private String retryCount;
    private String timeoutSeconds;
    private List<String> inputKeys;
    private List<String> outputKeys;
    private Map<String,String> staticParams;
    private String timeoutPolicy;
    private String retryLogic;
    private String retryDelaySeconds;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getRetryCount() {
        return retryCount;
    }
    public void setRetryCount(String retryCount) {
        this.retryCount = retryCount;
    }
    public String getTimeoutSeconds() {
        return timeoutSeconds;
    }
    public void setTimeoutSeconds(String timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
    public List<String> getInputKeys() {
        return inputKeys;
    }
    public void setInputKeys(List<String> inputKeys) {
        this.inputKeys = inputKeys;
    }
    public List<String> getOutputKeys() {
        return outputKeys;
    }
    public void setOutputKeys(List<String> outputKeys) {
        this.outputKeys = outputKeys;
    }
    public Map<String, String> getStaticParams() {
        return staticParams;
    }
    public void setStaticParams(Map<String, String> staticParams) {
        this.staticParams = staticParams;
    }
    public String getTimeoutPolicy() {
        return timeoutPolicy;
    }
    public void setTimeoutPolicy(String timeoutPolicy) {
        this.timeoutPolicy = timeoutPolicy;
    }
    public String getRetryLogic() {
        return retryLogic;
    }
    public void setRetryLogic(String retryLogic) {
        this.retryLogic = retryLogic;
    }
    public String getRetryDelaySeconds() {
        return retryDelaySeconds;
    }
    public void setRetryDelaySeconds(String retryDelaySeconds) {
        this.retryDelaySeconds = retryDelaySeconds;
    }
}
