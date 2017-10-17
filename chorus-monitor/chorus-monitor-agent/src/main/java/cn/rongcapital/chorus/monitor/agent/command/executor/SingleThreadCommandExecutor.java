package cn.rongcapital.chorus.monitor.agent.command.executor;

import cn.rongcapital.chorus.monitor.agent.command.Command;

/**
 * 单线程命令执行器，每次执行新启动一个线程
 */
public class SingleThreadCommandExecutor implements CommandExecutor {

    @Override
    public void executor(Command command) {
        Thread executeThread = new Thread(() -> {
            command.run();
        });
        executeThread.start();
    }

}
