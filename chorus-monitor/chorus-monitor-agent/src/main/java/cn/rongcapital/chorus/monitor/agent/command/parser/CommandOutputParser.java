package cn.rongcapital.chorus.monitor.agent.command.parser;

/**
 * Parse the message of the command output.
 *
 * @author li.hzh
 */
public interface CommandOutputParser<I, O> {

    O parse(I input);

}
