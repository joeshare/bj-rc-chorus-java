package cn.rongcapital.chorus.resourcemanager;

/**
 * Created by abiton on 25/11/2016.
 */
public interface DeploymentOperation {
    // TODO: 11/27/16 stream实现.
    void deployStreamOnExecutionUnitGroup(String streamName,String groupName);
    void unDeployStreamOnExecutionUnitGroup(String streamName,String groupName);
}
