package cn.rongcapital.chorus.server.metadata.param;

import cn.rongcapital.chorus.das.entity.ColumnInfoV2;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by fuxiangli on 2016-11-18.
 */
@Data
public class TableInfoParam {
    private String tableInfoId;

    private Long projectId;

    private String tableCode;

    private String tableName;

    private String dataField;

    private String tableType;

    private String isSnapshot;

    private String updateFrequence;

    private String sla;

    private String securityLevel;

    private Byte isOpen;

    private String tableDes;

    private Date createTime;

    private Date updateTime;

    private String statusCode;

    private List<ColumnInfoV2> columnInfoList;

}
