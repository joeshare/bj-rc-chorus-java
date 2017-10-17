package cn.rongcapital.chorus.monitor.agent.command.parser;

import cn.rongcapital.chorus.monitor.agent.command.IDCommand;
import cn.rongcapital.chorus.monitor.agent.command.executor.ReturnedSyncCommandExecutor;
import cn.rongcapital.chorus.monitor.agent.command.result.NethogsCommandResult;
import cn.rongcapital.chorus.monitor.agent.model.ProcessIOMonitorModel;
import cn.rongcapital.chorus.monitor.agent.util.ConfigCenter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class NethogsCommandOutputParser implements CommandOutputParser<List<String>, NethogsCommandResult> {

    private static final String CHORUS_PROCESS_MAKR = "chorus_";
    private static final String CHORUS_PROCESS_MAKR_PATTERN = "chorus_([\\w_]+)";

    @Override
    public NethogsCommandResult parse(List<String> input) {
        log.debug("Input message is: {}", input);
        if (input == null || input.isEmpty()) {
            // may never happen, but just leave a judgement.
            log.warn("Input message is null or empty. Nothing to parse.");
            return null;
        }
        List<ProcessIOMonitorModel> ioMonitorModels
                = input.stream().filter(containSlashFilter().and(excludeUNKnownFilter()).and(overThresholdFilter())).flatMap(transToIOModel())
                .filter(excludeNullModelFilter()).collect(Collectors.toList());
        log.debug("IO monitor model size: {}", ioMonitorModels.size());
        return new NethogsCommandResult(ioMonitorModels);
    }

    private Function<String, Stream<ProcessIOMonitorModel>> transToIOModel() {
        return s -> {
            String[] sArray = s.split("\t");
            String[] firstPartArray = sArray[0].split("/");
            String pid = getPid(firstPartArray);
            String cmdLine = getCmdline(pid);
            if (cmdLine == null || cmdLine.isEmpty()) {
                log.debug("Can not get the infomations of process {}. May have finished.", pid);
                // process has finished. skip it.
                return null;
            }
            if (notChorusProcess(cmdLine)) {
                log.debug("Process [{}] is not a chorus process, skip it.", cmdLine);
                return null;
            }
            double output = getOutputVolume(sArray);
            String user = getUser(firstPartArray);
            Set<String> projectCodes = getProjectCodes(cmdLine);
            String hostname = getHostname();
            return projectCodes.stream().map(code ->
                    new ProcessIOMonitorModel(pid, cmdLine, output, user, code, new Date(), hostname)
            );
        };
    }

    private String getHostname() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostName();
        } catch (UnknownHostException e) {
            log.error("Get hostname error.", e);
            return "UNKNOWN_HOSTNAME";
        }
    }

    private boolean notChorusProcess(String cmdLine) {
        return !cmdLine.contains(CHORUS_PROCESS_MAKR);
    }

    //TODO
    private Set<String> getProjectCodes(String cmdLine) {
        Pattern pattern = Pattern.compile(CHORUS_PROCESS_MAKR_PATTERN);
        Matcher matcher = pattern.matcher(cmdLine);
        Set<String> codes = new HashSet<>();
        while (matcher.find()) {
            codes.add(matcher.group(1));
        }
        return codes;
    }

//    public static void main(String[] args) {
////        String cmdLine = "/usr/jdk64/jdk1.8.0_60/bin/java -Xmx1024m -Dhdp.version=2.4.2.0-258 -Djava.net.preferIPv4Stack=true -Dhdp.version=2.4.2.0-258 " +
////                "-Djava.net.preferIPv4Stack=true -Dhdp.version= -Djava.net.preferIPv4Stack=true -Dhadoop.log.dir=/data/log/hadoop/yarn -Dhadoop.log.file=hadoop.log -Dhadoop.home.dir=/data/hdp/2.4.2.0-258/hadoop -Dhadoop.id.str=yarn -Dhadoop.root.logger=INFO,console -Djava.library.path=:/usr/hdp/2.4.2.0-258/hadoop/lib/native/Linux-amd64-64:/data/hdp/2.4.2.0-258/hadoop/lib/native -Dhadoop.policy.file=hadoop-policy.xml -Djava.net.preferIPv4Stack=true -Dhdp.version=2.4.2.0-258 -Dhadoop.log.dir=/data/log/hadoop/yarn -Dhadoop.log.file=hadoop.log -Dhadoop.home.dir=/data/hdp/2.4.2.0-258/hadoop -Dhadoop.id.str=yarn -Dhadoop.root.logger=INFO,console -Djava.library.path=:/usr/hdp/2.4.2.0-258/hadoop/lib/native/Linux-amd64-64:/data/hdp/2.4.2.0-258/hadoop/lib/native:/var/lib/ambari-agent/tmp/hadoop_java_io_tmpdir:/data/hdp/2.4.2.0-258/hadoop/lib/native/Linux-amd64-64:/data/hdp/2.4.2.0-258/hadoop/lib/native -Dhadoop.policy.file=hadoop-policy.xml -Djava.net.preferIPv4Stack=true -Dhdp.version=2.4.2.0-258 -Dhadoop.log.dir=/data/log/hadoop/yarn -Dhadoop.log.file=hadoop.log -Dhadoop.home.dir=/data/hdp/2.4.2.0-258/hadoop -Dhadoop.id.str=yarn -Dhadoop.root.logger=INFO,console -Djava.library.path=:/usr/hdp/2.4.2.0-258/hadoop/lib/native/Linux-amd64-64:/data/hdp/2.4.2.0-258/hadoop/lib/native:/var/lib/ambari-agent/tmp/hadoop_java_io_tmpdir:/data/hdp/2.4.2.0-258/hadoop/lib/native/Linux-amd64-64:/data/hdp/2.4.2.0-258/hadoop/lib/native:/var/lib/ambari-agent/tmp/hadoop_java_io_tmpdir:/data/hdp/2.4.2.0-258/hadoop/lib/native/Linux-amd64-64:/data/hdp/2.4.2.0-258/hadoop/lib/native -Dhadoop.policy.file=hadoop-policy.xml -Djava.net.preferIPv4Stack=true -Xmx1024m -Xmx1024m -Xmx1024m -Dhadoop.security.logger=INFO,NullAppender org.apache.sqoop.Sqoop import -Dmapreduce.job.queuename=mbxpro5 -Dtez.queue.name=mbxpro5 --connect jdbc:mysql://10.200.48.79:3306/chorus?useUnicode=true&characterEncoding=utf-8 --username dps --password Dps@10.200.48.MySQL --table project_info --hcatalog-database chorus_mbxpro5 --hcatalog-table teste_1504750810054 --delete-target-dir --hive-drop-import-delims --columns project_code,project_desc,project_id,project_name --m 1\n";
////        String cmdLine = "/bin/bash /usr/hdp/2.4.2.0-258//sqoop/bin/sqoop.distro import -Dmapreduce.job.queuename=mbxpro5 -Dtez.queue.name=mbxpro5 --connect jdbc:mysql://10.200.48.79:3306/chorus?useUnicode=true&characterEncoding=utf-8 --username dps --password Dps@10.200.48.MySQL --table project_info --hcatalog-database chorus_mbxpro5 --hcatalog-table teste_1504750532777 --delete-target-dir --hive-drop-import-delims chorus_lhzpro --columns project_code,project_desc,project_id,project_name --m 1\n";
////        System.out.println(new NethogsCommandOutputParser().getProjectCodes(cmdLine));
//        System.out.println(new NethogsCommandOutputParser().getHostname());
//    }

    private String getUser(String[] part) {
        String uid = part[part.length - 1];
        List<String> outputs = new ReturnedSyncCommandExecutor().execute(new IDCommand(uid));
        if (outputs == null || outputs.isEmpty()) {
            log.warn("UID {} does not exist.", uid);
            return null;
        }
        return parseUser(outputs.get(0));
    }

    private String parseUser(String s) {
        int beginIndex = s.indexOf("(");
        int endIndex = s.indexOf(")");
        return s.substring(beginIndex + 1, endIndex);
    }

    private double getOutputVolume(String[] sArray) {
        if (sArray.length > 1) {
            return Double.parseDouble(sArray[1]);
        } else {
            return 0d;
        }
    }

    private String getCmdline(String pid) {
        String fileName = "/proc/" + pid + "/cmdline";
        File cmdlineFile = new File(fileName);
        if (!cmdlineFile.exists()) {
            log.info("Cmdline file {} does not exist.", fileName);
            return null;
        }
        if (!cmdlineFile.canRead()) {
            log.warn("Can not read cmdlien file {}. ", fileName);
            return null;
        }
        String cmdLine = null;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(cmdlineFile));) {
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = fileReader.readLine()) != null) {
                sb.append(line);
            }
            cmdLine = sb.toString();
        } catch (IOException e) {
            log.error("", e);
        }
        return cmdLine;
    }

    private String getPid(String[] part) {
        return part[part.length - 2];
    }


    private Predicate<String> containSlashFilter() {
        return s -> s.contains("/");
    }

    private Predicate<String> excludeUNKnownFilter() {
        return s -> !s.startsWith("unknown");
    }

    private Predicate<ProcessIOMonitorModel> excludeNullModelFilter() {
        return model -> model != null;
    }

    private Predicate<String> overThresholdFilter() {
        return s -> {
            String[] sArray = s.split("\t");
            double output = getOutputVolume(sArray);
            return output >= ConfigCenter.getThresHold();
        };
    }
}
