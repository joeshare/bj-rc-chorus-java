package cn.rongcapital.chorus.monitor.agent.command.executor;

import cn.rongcapital.chorus.monitor.agent.command.Command;
import cn.rongcapital.chorus.monitor.agent.command.ReturnedCommand;

import java.util.List;

public class ReturnedSyncCommandExecutor implements SyncCommandExecutor<List<String>> {

    @Override
    public List<String> execute(ReturnedCommand command) {
        return command.execute();
    }

}
