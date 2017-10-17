package cn.rongcapital.chorus.monitor.agent.command;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IDCommand extends AbstractReturnedCommand {

    private String uid;

    @Override
    protected String getCommandLine() {
        return "id " + uid;
    }
}
