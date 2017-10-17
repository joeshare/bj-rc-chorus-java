package cn.rongcapital.chorus.common.constant;

import org.springframework.xd.rest.domain.DeployableResource;

/**
 * Created by hhlfl on 2017-8-25.
 */
public enum JobStatus {
    DEPLOY("DEPLOY"),UNDEPLOY("UNDEPLOY"),DELETE("DELETE");
    private String type;
    JobStatus(String type){
        this.type = type;
    }
    public String getType(){
        return type;
    }
}
