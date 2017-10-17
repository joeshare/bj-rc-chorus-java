package cn.rongcapital.chorus.server.dynamic_source.param;

import lombok.Data;

/**
 * Created by abiton on 18/07/2017.
 */
@Data
public class ExternalDataSourceRDBParam {
    private String url;
    private String userName;
    private String password;
}
