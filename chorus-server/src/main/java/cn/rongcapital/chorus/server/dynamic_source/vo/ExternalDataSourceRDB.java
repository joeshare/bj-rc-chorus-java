package cn.rongcapital.chorus.server.dynamic_source.vo;

import lombok.Data;

/**
 * Created by abiton on 18/07/2017.
 */
@Data
public class ExternalDataSourceRDB {
    private Long id;
    private String name;
    private String connUrl;
    private String connUser;
    private String connPass;
}
