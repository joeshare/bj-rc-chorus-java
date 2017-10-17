package cn.rongcapital.chorus.module.upload_ftp;

import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.ftp.FTPFileSystem;
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
@ComponentScan(basePackages = "cn.rongcapital.chorus.module.upload_ftp")
public class FtpUploadJob {

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
        return jobs.get("ftpUploadJob").listener(logListener).start(step1).build();
    }

    @Bean
    public Step step1(@Value("${ftpHost}") String ftpHost,
                      @Value("${ftpHostPort}") String ftpHostPort,
                      @Value("${ftpUserName}") String ftpUserName,
                      @Value("${ftpPassword}") String ftpPassword,
                      @Value("${ftpFilePath}") String ftpFilePath,
                      @Value("${hdfsFileFolderPath}") String hdfsFileFolderPathStr,
                      @Value("${retryCount:}") Integer retryCount) {
        return steps.get("ftpUploadStep1").tasklet(new SimpleTasklet() {
            @Override
            public RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) throws Exception {
                log.info("find ftp params: ftp://{}:{}@{}:{}, path regex: {}.",
                        ftpUserName, ftpPassword, ftpHost, ftpHostPort, ftpFilePath);
                String ftpUri = String.format("ftp://%s:%s@%s:%s", ftpUserName, ftpPassword, ftpHost, ftpHostPort);
    
                Configuration conf = new Configuration();
    
                // source file system validation
                Path ftpFsPath = new Path(ftpFilePath);
                String fileName = ftpFsPath.getName();
                log.info("ftp file path.getName: {}", fileName);
                FTPFileSystem ftpFs = new FTPFileSystem();
                ftpFs.setConf(conf);
                ftpFs.initialize(new URI(ftpUri), conf);
                FSDataInputStream fsDIn = ftpFs.open(ftpFsPath, 1000);
    
                // sink file system validation
                Path hdfsFileFolderPath = new Path(hdfsFileFolderPathStr);
                FileSystem hdfsFs = FileSystem.get(conf);
                if (!hdfsFs.exists(hdfsFileFolderPath))
                    hdfsFs.create(hdfsFileFolderPath);
                else if (!hdfsFs.isDirectory(hdfsFileFolderPath))
                    throw new Exception("HDFS file folder path is not a folder!");
                Path hdfsFilePath = new Path(hdfsFileFolderPathStr + "/" + fileName);
                OutputStream outputStream = hdfsFs.create(hdfsFilePath);
    
                IOUtils.copyBytes(fsDIn, outputStream, conf, true);
                return RepeatStatus.FINISHED;
            }
        }.retry(retryCount)).build();
    }
}
