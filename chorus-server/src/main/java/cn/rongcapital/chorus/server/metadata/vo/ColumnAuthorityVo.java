package cn.rongcapital.chorus.server.metadata.vo;

import lombok.Data;

/**
 * Created by alan on 11/30/16.
 */
@Data
public class ColumnAuthorityVo {

    private Long columnInfoId;

    private Long tableInfoId;

    private String columnName;

    private String columnDesc;

    private String columnType;

    private String securityLevel;

    private Boolean authorized;

}

