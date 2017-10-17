package cn.rongcapital.chorus.monitor.agent.command.executor;

import cn.rongcapital.chorus.monitor.agent.command.Command;

/**
 * 命令执行器
 */
public interface CommandExecutor {

    void executor(Command command);

}
