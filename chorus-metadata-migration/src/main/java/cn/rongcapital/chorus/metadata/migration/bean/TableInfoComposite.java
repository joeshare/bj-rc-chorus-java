package cn.rongcapital.chorus.metadata.migration.bean;

import cn.rongcapital.chorus.das.entity.ColumnInfo;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.TableInfo;
import lombok.Data;

import java.util.List;

/**
 * Created by hhlfl on 2017-7-25.
 */
@Data
public class TableInfoComposite {
    private TableInfo tableInfo;
    private String atlasGuid;
    private List<ColumnInfoComposite> columnInfoList;
    private ProjectInfo projectInfo;
}
