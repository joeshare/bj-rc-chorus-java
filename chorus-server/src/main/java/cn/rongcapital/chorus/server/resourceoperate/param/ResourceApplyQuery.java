package cn.rongcapital.chorus.server.resourceoperate.param;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by abiton on 22/11/2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceApplyQuery implements Serializable{
    private String status;
    private String userId;
    private Long operateId;
    private Long projectId;
}
