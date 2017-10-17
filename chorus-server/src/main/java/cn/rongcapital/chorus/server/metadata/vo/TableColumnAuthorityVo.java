package cn.rongcapital.chorus.server.metadata.vo;

import cn.rongcapital.chorus.das.entity.TableInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Created by alan on 11/30/16.
 */
@AllArgsConstructor
@Data
public class TableColumnAuthorityVo {

    private TableInfo tableInfo;

    private List<ColumnAuthorityVoV2> columnList;

}
