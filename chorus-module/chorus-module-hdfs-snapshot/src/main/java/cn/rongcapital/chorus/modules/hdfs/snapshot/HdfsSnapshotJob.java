package cn.rongcapital.chorus.modules.hdfs.snapshot;

import java.security.PrivilegedExceptionAction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.SnapshottableDirectoryStatus;
import org.apache.hadoop.security.UserGroupInformation;
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

import cn.rongcapital.chorus.common.util.DateUtils;
import cn.rongcapital.chorus.modules.utils.retry.SimpleTasklet;
import cn.rongcapital.log.JobListenerForLog;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lovett
 */
@Slf4j
@org.springframework.context.annotation.Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "cn.rongcapital.chorus.modules.hdfs.snapshot")
public class HdfsSnapshotJob {
    private final static String HDFS_USER = "hdfs";
    private final static String HDFS_SNAPSHOT_DEFAULT_LOCATION = "/.snapshot";
    private final static String SNAPSHOT_DATE_FORMAT = "yyyyMMdd-HHmmss";

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    public JobExecutionListener logListener() {
        return new JobListenerForLog();
    }

    @Bean
    public Job job(@Qualifier("createSnapshotStep1") Step step1,
                   @Qualifier("logListener") JobExecutionListener logListener) {
        return jobs.get("createSnapshot").listener(logListener).start(step1).build();
    }

    @Bean
    public Step createSnapshotStep1(
            @Value("${dir}") String dir,
            @Value("${hdfsUrl}") String hdfsUrl,
            @Value("${remainDays}") Integer remainDays,
            @Value("${retryCount:}") Integer retryCount
    ) {
        return steps.get("createSnapshotStep").tasklet(new SimpleTasklet(){

            @Override
            public RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) {
                try {
                    UserGroupInformation ugi = UserGroupInformation.createRemoteUser(HDFS_USER);

                    ugi.doAs(new PrivilegedExceptionAction<Void>() {

                        public Void run() throws Exception {

                            Configuration conf = new Configuration();
                            conf.set("fs.defaultFS", hdfsUrl);
                            conf.set("hadoop.job.ugi", HDFS_USER);

                            List<Path> snapshotPathList = new ArrayList<>();
                            FileSystem fileSystem = FileSystem.get(conf);

                            if (fileSystem instanceof DistributedFileSystem) {
                                DistributedFileSystem fs = (DistributedFileSystem) fileSystem;

                                SnapshottableDirectoryStatus[] snapshottableDirListing = fs
                                        .getSnapshottableDirListing();
                                // create snapshot
                                log.info("Start create snapshot for directory ==== " + dir);

                                for (SnapshottableDirectoryStatus sds : snapshottableDirListing) {
                                    Path path = sds.getFullPath();

                                    if (path.toString().startsWith(dir)) {
                                        String snapshotName = DateUtils.format(new Date(), SNAPSHOT_DATE_FORMAT);
                                        fs.createSnapshot(path, snapshotName);
                                        snapshotPathList.add(path);
                                    }
                                }
                                log.info("End create snapshot for directory ==== " + dir);

                                // clear expired snapshot
                                Date currentDate = new Date();
                                Date expirationDate = getLastNDate(currentDate, remainDays);

                                log.info("Start clear expired snapshot before expiration time "
                                        + expirationDate.toString());

                                for (Path p : snapshotPathList) {
                                    Path sp = new Path(p.toString() + HDFS_SNAPSHOT_DEFAULT_LOCATION);
                                    RemoteIterator<LocatedFileStatus> snapshotDir = fs.listLocatedStatus(sp);

                                    while (snapshotDir.hasNext()) {
                                        LocatedFileStatus next = snapshotDir.next();
                                        String snapshotName = next.getPath().getName();
                                        Date createDate = DateUtils.parse(snapshotName, SNAPSHOT_DATE_FORMAT);
                                        if (createDate != null && expirationDate.after(createDate)) {
                                            fs.deleteSnapshot(p, snapshotName);
                                        }
                                    }
                                }

                                log.info("End of clear expired snapshot ");
                            } else {
                                log.error("Can not get distributedfileSystem .");
                            }

                            return null;
                        }
                    });
                } catch (Exception e) {
                    log.info("hdsf snapshot error", e);
                }
                return null;
            }
            }.retry(retryCount)).build();
        }

    private Date getLastNDate(Date date, int n){
        Date lastDate = date;
        if(n != 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_YEAR, -n);
            lastDate = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String ldate = sdf.format(lastDate);
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
            try {
                lastDate = sdf.parse(ldate + " 00:00:00");
            }catch (Exception ex){
                log.error(ex.getMessage(), ex);
            }
        }
        return lastDate;
    }
}
