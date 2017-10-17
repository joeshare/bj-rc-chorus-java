package cn.rongcapital.chorus.monitor.agent.command.result;

import cn.rongcapital.chorus.monitor.agent.model.ProcessIOMonitorModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NethogsCommandResult implements CommandResult<List<ProcessIOMonitorModel>> {

    private List<ProcessIOMonitorModel> content;

    @Override
    public List<ProcessIOMonitorModel> getContent() {
        return content;
    }

}
