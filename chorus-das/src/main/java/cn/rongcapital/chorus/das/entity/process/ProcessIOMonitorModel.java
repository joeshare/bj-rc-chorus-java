package cn.rongcapital.chorus.das.entity.process;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProcessIOMonitorModel {

    private String pid;
    private String cmdLine;
    private double outputVolume;
    private String user;
    private String projectCode;
    private Date time;
    private String hostname;

}
