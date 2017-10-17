package cn.rongcapital.chorus.monitor.agent;

import cn.rongcapital.chorus.monitor.agent.command.NethogsCommand;
import cn.rongcapital.chorus.monitor.agent.command.executor.CommandExecutor;
import cn.rongcapital.chorus.monitor.agent.command.executor.SingleThreadCommandExecutor;
import cn.rongcapital.chorus.monitor.agent.command.handler.CommandResultToKafkaHandler;
import cn.rongcapital.chorus.monitor.agent.command.parser.NethogsCommandOutputParser;
import lombok.extern.slf4j.Slf4j;

/**
 * Chorus监控Agent启动类
 *
 * @author li.hzh
 */
@Slf4j
public class AgentLauncher {

    public static void main(String[] args) {
        log.info("Starting Chorus Monitor Agent....");
        String device = null;
        if (args != null && args.length > 0) {
            device = args[0];
            log.debug("Monitor device {}.", device);
        }
        CommandExecutor executor = new SingleThreadCommandExecutor();
        executor.executor(new NethogsCommand(device, new CommandResultToKafkaHandler(new NethogsCommandOutputParser(), "chorus-network-monitor-topic")));
    }

}
