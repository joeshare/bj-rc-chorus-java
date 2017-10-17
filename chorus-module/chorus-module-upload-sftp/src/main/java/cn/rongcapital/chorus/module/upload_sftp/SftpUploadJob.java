package cn.rongcapital.chorus.module.upload_sftp;

import static cn.rongcapital.chorus.module.upload_sftp.SftpMeta.AUTH_TYPE_PASSWORD;

import java.io.File;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.IOUtils;
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

import cn.rongcapital.chorus.modules.utils.retry.SimpleTasklet;
import cn.rongcapital.log.JobListenerForLog;
import lombok.extern.slf4j.Slf4j;


/**
 * Created by alan on 16/01/2017.
 */
@Slf4j
@org.springframework.context.annotation.Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "cn.rongcapital.chorus.module.upload_sftp")
public class SftpUploadJob {

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
        return jobs.get("sftpUploadJob").listener(logListener).start(step1).build();
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
        return steps.get("sftpUploadStep1").tasklet(new SimpleTasklet() {
            @Override
            public RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) throws Exception {
                log.info("find sftp params: sftp://{}:{}@{}:{}, path regex: {}, hdfsPath: {}.",
                        sftpUserName, sftpPassword, sftpHost, sftpHostPort,
                        sftpFilePathStr, hdfsFileFolderPathStr);
                String sftpUriStr = sftpAuthType.equals(AUTH_TYPE_PASSWORD) ?
                        String.format("sftp://%s:%s@%s:%s%s", sftpUserName, sftpPassword, sftpHost, sftpHostPort, sftpFilePathStr) :
                        String.format("sftp://%s@%s:%s%s", sftpUserName, sftpHost, sftpHostPort, sftpFilePathStr);
                log.info("uri string: {}", sftpUriStr);
                Path sftpFilePath = new Path(sftpUriStr);
                URI sftpFileUri = sftpFilePath.toUri();
    
                // create tmp folder
                File sshDir = new File(SftpFileSystem.VFS_SFTP_SSHDIR);
                if (!sshDir.exists())
                    sshDir.mkdirs();
    
                Configuration conf = new Configuration();
                if (StringUtils.isNotBlank(sftpPrivateKey)) {
//                    String finalKey = sftpPrivateKey.replace("\\n", "\n");
                    String finalKey = URLDecoder.decode(sftpPrivateKey,"UTF-8");
//                    log.info("finalKey: {}", finalKey);
                    conf.set(SftpFileSystem.FS_SFTP_KEY_PREFIX + sftpFileUri.getHost(), finalKey);
                }
    
                // source file system validation
                String fileName = sftpFilePath.getName();
                log.info("sftp file path.getName: {}", fileName);
                SftpFileSystem sftpFs = new SftpFileSystem();
                sftpFs.initialize(sftpFileUri, conf);
                FSDataInputStream fsDIn = sftpFs.open(sftpFilePath, 1000);
    
                // sink file system validation
                Path hdfsFileFolderPath = new Path(hdfsFileFolderPathStr);
                FileSystem hdfsFs = FileSystem.get(conf);
                if (!hdfsFs.exists(hdfsFileFolderPath)){
                    hdfsFs.mkdirs(hdfsFileFolderPath,FsPermission.getDirDefault());
                }  else if (!hdfsFs.isDirectory(hdfsFileFolderPath))
                    throw new Exception("HDFS file folder path is not a folder!");
                Path hdfsFilePath = new Path(hdfsFileFolderPathStr + "/" + fileName);
                OutputStream outputStream = hdfsFs.create(hdfsFilePath);
    
                IOUtils.copyBytes(fsDIn, outputStream, conf, true);
                return RepeatStatus.FINISHED;
            }
        }.retry(retryCount)).build();
    }
}