package cn.rongcapital.chorus.das.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by alan on 12/2/16.
 */
@Data
public class HostReservationSizeDO {
    public static final HostReservationSizeDO emptyReservationDO = new HostReservationSizeDO() {{
        setHostId(-1L);
        setCpu(0);
        setMemory(0);
    }};

    private Long hostId;

    private Integer cpu;

    private Integer memory;
}