package cn.rongcapital.chorus.server.metadata.vo;

import cn.rongcapital.chorus.das.entity.TableInfoV2;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Created by alan on 11/30/16.
 */
@AllArgsConstructor
@Data
public class TableColumnAuthorityVoV2 {

    private TableInfoV2 tableInfo;

    private List<ColumnAuthorityVoV2> columnList;

}
