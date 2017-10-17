package cn.rongcapital.chorus.monitor.agent.command;

import cn.rongcapital.chorus.monitor.agent.cache.CacheRecord;
import cn.rongcapital.chorus.monitor.agent.cache.NotifyRecordCache;
import cn.rongcapital.chorus.monitor.agent.command.handler.CommandResultHandler;
import cn.rongcapital.chorus.monitor.agent.command.parser.NethogsCommandOutputParser;
import cn.rongcapital.chorus.monitor.agent.command.result.NethogsCommandResult;
import cn.rongcapital.chorus.monitor.agent.model.ProcessIOMonitorModel;
import cn.rongcapital.chorus.monitor.agent.msgbus.JsonJMSMessage;
import cn.rongcapital.chorus.monitor.agent.util.ConfigCenter;
import cn.rongcapital.chorus.monitor.agent.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class NethogsCommand implements Command {

    private static final String CMD_NAME = "/usr/sbin/nethogs -t -v 3 -d 5";
    private static final String BEGIN_LINE = "Refreshing:";
    private CommandResultHandler resultHandler;
    //TODO support device list
    private String device;

    public NethogsCommand(String device, CommandResultHandler handler) {
        this.resultHandler = handler;
        this.device = device;
    }

    public NethogsCommand(CommandResultHandler handler) {
        this(null, handler);
    }

    @Override
    public void run() {
        log.info("Start monitoring network output.");
        String cmd = device == null ? CMD_NAME : CMD_NAME + " " + device;
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            //TODO handle error
            BufferedReader bf = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("utf8")));
            List<String> inputList = null;
            boolean isContentBegin = false;
            String line;
            while ((line = bf.readLine()) != null) {
                try {
                    log.trace("Nethogs output: {}.", line);
                    if (isStartLine(line)) {
                        if (inputList != null && !inputList.isEmpty()) {
                            NethogsCommandResult result = ((NethogsCommandOutputParser) resultHandler.getParser()).parse(inputList);
                            NethogsCommandResult finalResult = filterNotifyResult(result);
                            if (finalResult.getContent() != null && !finalResult.getContent().isEmpty()) {
                                log.info("Find over threshold processes. {}", finalResult.getContent());
                                resultHandler.handle(result);
                            }
                        }
                        inputList = new ArrayList<>();
                        isContentBegin = true;
                        continue;
                    }
                    if (isEndLine(line)) {
                        continue;
                    }
                    if (isContentBegin) {
                        inputList.add(line);
                    }
                }catch (Exception e) {
                    log.error("Execute nethogs command error!!", e);
                }
            }
        } catch (IOException e) {
            log.error("Execute nethogs command error!!", e);
        }
    }

    private NethogsCommandResult filterNotifyResult(NethogsCommandResult result) {
        List<ProcessIOMonitorModel> modelList = result.getContent().stream().filter(needNotify()).collect(Collectors.toList());
        return new NethogsCommandResult(modelList);
    }

    private Predicate<ProcessIOMonitorModel> needNotify() {
        return processIOMonitorModel -> {
            long now = System.currentTimeMillis();
            String cacheKey = generateKey(processIOMonitorModel);
            CacheRecord<ProcessIOMonitorModel> record = NotifyRecordCache.getRecord(cacheKey);
            if (record == null) {
                CacheRecord<ProcessIOMonitorModel> newRecord = new CacheRecord<>();
                newRecord.setTimestamp(now);
                newRecord.setContent(processIOMonitorModel);
                NotifyRecordCache.cache(cacheKey, newRecord);
                return true;
            } else {
                long cacheTimestamp = record.getTimestamp();
                int minInterval = ConfigCenter.getMinIntevrval();
                if (now - cacheTimestamp > minInterval * 60 * 1000L) {
                    record.setTimestamp(now);
                    NotifyRecordCache.cache(cacheKey, record);
                    return true;
                } else {
                    return false;
                }
            }
        };
    }

    private String generateKey(ProcessIOMonitorModel processIOMonitorModel) {
        return processIOMonitorModel.getPid() + processIOMonitorModel.getUser() + processIOMonitorModel.getCmdLine();
    }

    private boolean isEndLine(String line) {
        return line == null || line.isEmpty();
    }

    private boolean isStartLine(String line) {
        return line.startsWith(BEGIN_LINE);
    }

}
