package cn.rongcapital.chorus.server.resourceoperate.param;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by abiton on 22/11/2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceOperateParam implements Serializable {
    private String userId;
    private String userName;
    private Long projectId;
    private Integer cpu;
    private Integer memory;
    private Integer storage;
    private String reason;
    private String notes;
}
