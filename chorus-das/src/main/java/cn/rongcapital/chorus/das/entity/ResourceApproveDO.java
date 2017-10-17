package cn.rongcapital.chorus.das.entity;

import lombok.Data;

/**
 * Created by abiton on 20/04/2017.
 */
@Data
public class ResourceApproveDO {
    private String userId;
    private String userName;
    private Long operationId;
    private boolean approve;
    private String notes;
    private TotalResource leftResource;
    private TotalResource totalResource;

    public boolean isApprove() {
        return approve;
    }

    public void setApprove(boolean approve) {
        this.approve = approve;
    }
}
