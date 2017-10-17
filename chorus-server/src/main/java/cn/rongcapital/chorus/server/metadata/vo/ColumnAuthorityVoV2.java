package cn.rongcapital.chorus.server.metadata.vo;

import lombok.Data;

/**
 * Created by alan on 11/30/16.
 */
@Data
public class ColumnAuthorityVoV2 {

    private String columnInfoId;

    private String tableInfoId;

    private String columnName;

    private String columnDesc;

    private String columnType;

    private String securityLevel;

    private Boolean authorized;

}

