package cn.rongcapital.chorus.server.instanceInfo.controller;

import cn.rongcapital.chorus.common.hadoop.HadoopClient;
import cn.rongcapital.chorus.das.service.JobMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.RemoteIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by abiton on 07/12/2016.
 * @author Li.ZhiWei
 */
@Controller
@RequestMapping("/downloadFile")
@Slf4j
public class LogDownloadController {
    @Autowired
    private HadoopClient hClient;
    @Value(value = "${hdfsLogDir}")
    private String hdfsLogDir;
    @Autowired
    private JobMonitorService jobMonitorService;

    @RequestMapping("{date}/{executionId}")
    public void download(HttpServletResponse response, @PathVariable String date, @PathVariable long executionId) throws ExecutionException, InterruptedException, IOException {

        try {
            String uri = String.format(hdfsLogDir, date, executionId);
            List<String> uris = new ArrayList<>();
            List<Long> subJobExecutionIds = jobMonitorService.getSubJobExecutionIds(executionId);
            BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());

            if (subJobExecutionIds != null) {
                response.setContentType("Content-type: text/zip");
                response.setHeader("Content-Disposition", "attachment; filename=" + executionId + "logs.zip");

                for (Long exId : subJobExecutionIds) {
                    uris.add(String.format(hdfsLogDir, date, exId));
                }
                ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(outputStream));

                for (String u : uris) {
                    RemoteIterator<LocatedFileStatus> iterator = hClient.listFiles(u, false);

                    zos.putNextEntry(new ZipEntry(u + ".txt"));
                    hClient.readFileAndWrite(iterator, zos, false);
                    zos.closeEntry();
                }

                zos.close();
            } else {
                response.setHeader("Content-Disposition", "attachment;filename=" + executionId + "logs");
                RemoteIterator<LocatedFileStatus> iterator = hClient.listFiles(uri, false);
                hClient.readFileAndWrite(iterator, outputStream, false);
                outputStream.close();
            }

            response.flushBuffer();
        } catch (Exception se) {
            log.error("Caught exception in download log file, date: {}, executionId: {} !!!", date, executionId);
            log.error("Caught exception in download log file !!!", se);
//            response.sendError(500, "日志文件不存在!");
            response.flushBuffer();
        }
    }
}
