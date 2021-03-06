package cn.rongcapital.chorus.server.metadata.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fuxiangli on 2016-11-24.
 */

@Data
public class ApplyFormParamV2 implements Serializable {

    private String tableInfoId;

    private String userId;

    private Integer duration;

    private String reason;

    private List<String> columnList;

    private String userName;

}
