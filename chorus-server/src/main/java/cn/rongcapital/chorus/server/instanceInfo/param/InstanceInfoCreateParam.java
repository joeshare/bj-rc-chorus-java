package cn.rongcapital.chorus.server.instanceInfo.param;

import lombok.Data;

import java.util.List;

/**
 * Created by alan on 12/2/16.
 */
@Data
public class InstanceInfoCreateParam {
    private String userId;

    private String userName;

    private Long projectId;

    private String groupName;

    private Long resourceInnerId;

    private Long resourceTemplateId;

    private Integer instanceSize;

    private List<Long> environmentIdList;

    private String instanceDesc;
}
