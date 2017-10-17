package cn.rongcapital.chorus.monitor.agent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@AllArgsConstructor
public class ProcessIOMonitorModel {

    private String pid;
    private String cmdLine;
    private double outputVolume;
    private String user;
    private String projectCode;
    private Date time;
    private String hostname;
}
