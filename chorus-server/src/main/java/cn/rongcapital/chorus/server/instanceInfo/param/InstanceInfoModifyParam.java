package cn.rongcapital.chorus.server.instanceInfo.param;

import lombok.Data;

/**
 * Created by alan on 12/2/16.
 */
@Data
public class InstanceInfoModifyParam {
    private String userId;

    private String userName;

    private Long instanceId;

    private Integer instanceSize;

    private String instanceDesc;
}
