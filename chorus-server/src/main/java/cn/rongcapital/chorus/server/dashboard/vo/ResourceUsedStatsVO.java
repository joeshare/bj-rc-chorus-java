package cn.rongcapital.chorus.server.dashboard.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by hhlfl on 2017-6-22.
 */
@NoArgsConstructor
@Data
public class ResourceUsedStatsVO {
    private String resourceName;
    private Long usedCount;
}
