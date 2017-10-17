package cn.rongcapital.chorus.monitor.agent.command.handler;

import cn.rongcapital.chorus.monitor.agent.command.parser.CommandOutputParser;
import cn.rongcapital.chorus.monitor.agent.command.result.CommandResult;

public interface CommandResultHandler<T> {

    void handle(CommandResult<T> result);

    CommandOutputParser getParser();
}
