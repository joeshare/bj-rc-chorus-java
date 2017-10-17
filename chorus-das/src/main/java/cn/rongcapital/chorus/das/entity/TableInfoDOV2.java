package cn.rongcapital.chorus.das.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by fuxiangli on 2016-11-23.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TableInfoDOV2 implements Serializable{
    private static final long serialVersionUID = 2221169756507540347L;

    private String tableInfoId;

    private Long projectId;

    private String projectCode;

    private String projectName;

    private String tableName;

    private String securityLevel;

    private String tableDes;
}
