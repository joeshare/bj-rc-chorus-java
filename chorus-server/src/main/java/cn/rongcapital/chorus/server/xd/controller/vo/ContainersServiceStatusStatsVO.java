package cn.rongcapital.chorus.server.xd.controller.vo;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

/**
 * @author yimin
 */
@Getter
@Builder
public class ContainersServiceStatusStatsVO implements Serializable{

    private static final long serialVersionUID = 1036865706384633581L;

    private final Long projectId;

    /**
     * the sum of containers which have one deployed module at least
     */
    private final int used;

    /**
     * the sum of containers which there was no module deployed
     */
    private final int remaining;

    private final long timestamp;
}
