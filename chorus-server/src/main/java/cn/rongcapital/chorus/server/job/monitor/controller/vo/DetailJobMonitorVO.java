package cn.rongcapital.chorus.server.job.monitor.controller.vo;

import java.util.List;

import lombok.Data;

@Data
public class DetailJobMonitorVO {
    private String workFlowDSL;
    private List<JobMonitorVO> list;
    private Long recordCount;
}
