package cn.rongcapital.chorus.metadata.migration.bean;

import cn.rongcapital.chorus.das.entity.ColumnInfo;
import lombok.Data;

/**
 * Created by hhlfl on 2017-7-25.
 */
@Data
public class ColumnInfoComposite {
    private ColumnInfo columnInfo;
    private String atlasGuid;
}
