package cn.rongcapital.chorus.modules.file_sample;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by abiton on 15/06/2017.
 */
@Slf4j
@Data
public class FileSampleTasklet implements Tasklet {

    private String path;

    private String sampleType;

    private Integer sampleRate;

    private Integer sampleCount;

    private String outputPath;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        Configuration conf = new Configuration();
        FileSystem fileSystem = FileSystem.get(conf);
        try (
                FSDataInputStream pathInputStream = fileSystem.open(new Path(path));
                FSDataOutputStream fsDataOutputStream = fileSystem.create(new Path(outputPath));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fsDataOutputStream));
                BufferedReader reader = new BufferedReader(new InputStreamReader(pathInputStream))
        ) {
            Random random = new Random();
            if (SampleType.COUNT.name().equals(sampleType)) {
                int count = sampleCount;
                AtomicInteger left = new AtomicInteger(count + 1);
                String line;
                while (left.get() > 0 && (line = reader.readLine()) != null) {
                    if (random.nextFloat() >= Math.pow(1.0 / left.get(), 1.5)) {
                        left.decrementAndGet();
                        writer.write(line);
                        writer.newLine();
                    }
                }


            } else if (SampleType.RATE.name().equals(sampleType)) {
                int rate = sampleRate;
                String line;
                while ((line = reader.readLine()) != null) {
                    if (random.nextInt(100) <= rate) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            } else {
                throw new RuntimeException("do not support SampleType " + sampleType);
            }
        }
        return RepeatStatus.FINISHED;
    }
}
