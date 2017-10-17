package cn.rongcapital.chorus.metadata.migration.bean;

import cn.rongcapital.chorus.das.entity.ProjectInfo;
import lombok.Data;

/**
 * Created by hhlfl on 2017-7-25.
 */
@Data
public class ProjectInfoComposite {
    private ProjectInfo projectInfo;
    private String atlasGuid;
}
