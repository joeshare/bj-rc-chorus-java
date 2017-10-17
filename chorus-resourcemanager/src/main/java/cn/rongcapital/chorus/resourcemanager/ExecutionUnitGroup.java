package cn.rongcapital.chorus.resourcemanager;

import cn.rongcapital.chorus.das.entity.ResourceTemplate;
import lombok.Data;

import java.util.List;

/**
 * Created by abiton on 25/11/2016.
 */
@Data
public class ExecutionUnitGroup {
    private Long instanceId;

    private Long projectId;

    private Long resourceInnerId;

    private String groupName;

    private ResourceTemplate resourceTemplate;

    private List<Long> environmentIdList;

    private int instanceSize;

    private String instanceDesc;
}
