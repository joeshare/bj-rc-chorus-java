package cn.rongcapital.chorus.server.datalab.vo;

import cn.rongcapital.chorus.datalab.service.DatalabStatus;
import lombok.Data;

import java.util.Date;

/**
 * Created by abiton on 21/03/2017.
 */
@Data
public class DatalabVO {
    private String projectCode;

    private String labName;

    private String labCode;

    private String labDesc;

    private String labStatus;

    private String labStatusCode;

    private String createUserName;

    private Date createTime;

    public void setStatus(DatalabStatus status){
        this.labStatusCode = status.getCode();
        this.labStatus = status.getDesc();
    }
}
