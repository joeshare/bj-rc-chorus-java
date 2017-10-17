package cn.rongcapital.chorus.server.datalab.param;

import lombok.Data;


/**
 * Created by abiton on 17/03/2017.
 */
@Data
public class DatalabCreate {
    private String createUserName;
    private String projectCode;
    private String labCode;
    private String labDesc;
    private String labName;
    private Integer cpu;
    private Integer memory;

}
