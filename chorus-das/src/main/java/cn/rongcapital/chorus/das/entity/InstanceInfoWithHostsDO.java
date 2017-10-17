package cn.rongcapital.chorus.das.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by alan on 12/2/16.
 */
@Data
public class InstanceInfoWithHostsDO {

    private Long instanceId;

    private Long projectId;

    private Long resourceInnerId;

    private Integer instanceSize;

    private String groupName;

    private Date createTime;

    private Date updateTime;

    private String statusCode;

    private String instanceDesc;

    private ResourceTemplate resourceTemplate;

}