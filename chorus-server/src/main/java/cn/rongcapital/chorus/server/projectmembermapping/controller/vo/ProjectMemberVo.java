package cn.rongcapital.chorus.server.projectmembermapping.controller.vo;

import lombok.Data;
import lombok.ToString;

/**
 * Created by shicheng on 2016/11/25.
 */
@Data
@ToString
public class ProjectMemberVo {

    private Long projectMemberId;
    private Long projectId;
    private String roleId;
    private String userId;
    private String userName;

}
