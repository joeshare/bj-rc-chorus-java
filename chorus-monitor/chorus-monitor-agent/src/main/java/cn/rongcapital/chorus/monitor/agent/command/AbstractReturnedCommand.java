package cn.rongcapital.chorus.monitor.agent.command;

import cn.rongcapital.chorus.monitor.agent.command.result.NethogsCommandResult;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractReturnedCommand implements ReturnedCommand {

    protected abstract String getCommandLine();

    @Override
    public List<String> execute() {
        String cmd = getCommandLine();
        log.info("Execute command [{}].", cmd);
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            //TODO handle error
            try (BufferedReader bf = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = null;
                List<String> outputArray = new ArrayList<>();
                while ((line = bf.readLine()) != null) {
                    log.trace("Command output: {}.", line);
                    outputArray.add(line);
                }
                return outputArray;
            }

        } catch (IOException e) {
            log.error("Execute command error!!", e);
        }
        return null;
    }

}
