package cn.rongcapital.chorus.monitor.agent.command.executor;

import cn.rongcapital.chorus.monitor.agent.command.Command;
import cn.rongcapital.chorus.monitor.agent.command.ReturnedCommand;

import java.util.List;

/**
 * Sync command executor. Run command and return the command output message as {@code List<String>}.
 * It will block the thread when the command is running.
 */
public interface SyncCommandExecutor<T> {

    T execute(ReturnedCommand command);

}
