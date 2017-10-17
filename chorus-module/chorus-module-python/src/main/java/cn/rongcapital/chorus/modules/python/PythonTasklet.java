package cn.rongcapital.chorus.modules.python;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;

/**
 * Created by abiton on 08/06/2017.
 */
@Slf4j
public class PythonTasklet implements Tasklet {

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    private String scriptPath;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        Configuration conf = new Configuration();
        FileSystem fileSystem = FileSystem.get(conf);
        FSDataInputStream fsDataInputStream = fileSystem.open(new Path(scriptPath));

        InputStreamReader inputStreamReader = new InputStreamReader(fsDataInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        Optional<String> cmd = bufferedReader.lines().reduce((a, b) -> a + "\n" + b);
        log.info("script is {}", cmd.orElse(""));
        bufferedReader.close();
        PythonProcess python = new PythonProcess();
        python.open();
        String result = python.sendAndGetResult(cmd.orElse(""));
        log.info("python script output {}", result);
        python.close();
        return RepeatStatus.FINISHED;
    }
}
