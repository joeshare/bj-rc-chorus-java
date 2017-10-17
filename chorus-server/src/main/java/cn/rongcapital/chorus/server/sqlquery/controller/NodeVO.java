package cn.rongcapital.chorus.server.sqlquery.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


/**
 * @author yimin
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeVO implements Serializable {
    public static final String AUTHORIZED_COLUMNS       = "authorized-columns";
    public static final String APPLIED_COLUMNS          = "applied-columns";
    public static final String APPLIED_TABLE            = "applied-table";
    public static final String PROJECT_TABLE_AUTHORIZED = "project-table-authorized";
    public static final String AUTHORIZED_TABLE         = "authorized-table";
    public static final String PROJECT_AUTHORIZED       = "project-authorized";

    private static final long serialVersionUID = 8998511480479966072L;

    private String code;
    private String text;
    private String type;
    private boolean isLeaf = false;
}
