package cn.rongcapital.chorus.server.resourceoperate.param;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by abiton on 20/04/2017.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceApproveParam implements Serializable{
    private String userId;
    private String userName;
    private Long operationId;
    private boolean approve;
    private String notes;
    private String projectCode;
}
