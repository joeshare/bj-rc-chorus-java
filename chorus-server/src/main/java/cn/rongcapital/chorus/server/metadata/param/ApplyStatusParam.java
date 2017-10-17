package cn.rongcapital.chorus.server.metadata.param;

import cn.rongcapital.chorus.das.entity.ApplyDetail;
import lombok.Data;

import java.util.List;

/**
 * Created by fuxiangli on 2016-11-27.
 */
@Data
public class ApplyStatusParam {
    private Long applyFormId;
    private String statusCode;
    private String dealInstruction;
    private String dealUserId;
    private String userName;
}
