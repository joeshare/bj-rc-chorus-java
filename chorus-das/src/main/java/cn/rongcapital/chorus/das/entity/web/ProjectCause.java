package cn.rongcapital.chorus.das.entity.web;

import lombok.Data;
import lombok.ToString;

/**
 * Created by shicheng on 2016/11/29.
 */
@Data
@ToString
public class ProjectCause extends CommonCause {

    private String userId;
    private Long projectId;
    private String roleId;
    private String projectName;

}
