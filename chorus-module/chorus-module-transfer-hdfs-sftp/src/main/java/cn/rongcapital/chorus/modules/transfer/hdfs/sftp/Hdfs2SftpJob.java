package cn.rongcapital.chorus.modules.transfer.hdfs.sftp;

import cn.rongcapital.chorus.modules.utils.retry.SimpleTasklet;
import cn.rongcapital.log.JobListenerForLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.net.URI;
import java.net.URLDecoder;

import static cn.rongcapital.chorus.modules.transfer.hdfs.sftp.Hdfs2SftpMetadata.AUTH_TYPE_PASSWORD;



@Slf4j
@org.springframework.context.annotation.Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "cn.rongcapital.chorus.modules.transfer.hdfs.sftp")
public class Hdfs2SftpJob {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    public JobExecutionListener logListener() {
        return new JobListenerForLog();
    }

    @Bean
    public Job job(@Qualifier("step1") Step step1,
                   @Qualifier("logListener") JobExecutionListener logListener) {
        return jobs.get("Hdfs2SftpJob").listener(logListener).start(step1).build();
    }

    @Bean
    public Step step1(@Value("${sftpHost}") String sftpHost,
                      @Value("${sftpHostPort}") String sftpHostPort,
                      @Value("${sftpUserName}") String sftpUserName,
                      @Value("${sftpAuthType}") Byte sftpAuthType,
                      @Value("${sftpPassword:}") String sftpPassword,
                      @Value("${sftpPrivateKey:}") String sftpPrivateKey,
                      @Value("${sftpFilePath}") String sftpFilePathStr,
                      @Value("${hdfsFileFolderPath}") String hdfsFileFolderPathStr,
                      @Value("${retryCount:}") Integer retryCount) {
        return steps.get("Hdfs2SftpStep1").tasklet(new SimpleTasklet() {
            @Override
            public RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) throws Exception {
                log.info("find sftp params: sftp://{}:{}@{}:{}, path regex: {}, hdfsPath: {}.",
                        sftpUserName, sftpPassword, sftpHost, sftpHostPort,
                        sftpFilePathStr, hdfsFileFolderPathStr);
                String sftpUriStr = sftpAuthType.equals(AUTH_TYPE_PASSWORD) ?
                        String.format("sftp://%s:%s@%s:%s%s", sftpUserName, sftpPassword, sftpHost, sftpHostPort, sftpFilePathStr) :
                        String.format("sftp://%s@%s:%s%s", sftpUserName, sftpHost, sftpHostPort, sftpFilePathStr);
                log.info("uri string: {}", sftpUriStr);
                URI sftpFileUri = new Path(sftpUriStr).toUri();
                // create tmp folder
                File sshDir = new File(SftpFileSystem.VFS_SFTP_SSHDIR);
                if (!sshDir.exists())
                    sshDir.mkdirs();
                Configuration conf = new Configuration();
                if (StringUtils.isNotBlank(sftpPrivateKey)) {
                    String finalKey = URLDecoder.decode(sftpPrivateKey,"UTF-8");
                    conf.set(SftpFileSystem.FS_SFTP_KEY_PREFIX + sftpFileUri.getHost(), finalKey);
                }

                SftpFileSystem sftpFileSystem = new SftpFileSystem();
                sftpFileSystem.initialize(sftpFileUri,conf);
                Hdfs2SftpTransfer transfer = new Hdfs2SftpTransfer(sftpFileSystem);
                transfer.copy(hdfsFileFolderPathStr, sftpFileUri.toString());
                log.info("upload file from hdfs:{} to sftp:{} success.", hdfsFileFolderPathStr,sftpFilePathStr);
                return RepeatStatus.FINISHED;
            }
        }.retry(retryCount)).build();
    }
}