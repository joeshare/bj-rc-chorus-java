package cn.rongcapital.chorus.das.entity;

import lombok.Data;

/**
 * Created by abiton on 28/11/2016.
 */
@Data
public class InstanceHostDO {

    private Long instanceId;

    private Long hostId;

    private String hostName;

    private Integer size;

    private Integer cpu;

    private Integer memory;
}
