package cn.rongcapital.chorus.datalab.service.impl;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by abiton on 10/03/2017.
 */
public class LabServiceImplTest {

    @Test
    public void testCreateLab() throws Exception {
        CommandLine commandLine = new CommandLine("He");

        commandLine.addArgument("-p ",true);

        System.out.println(commandLine);



    }

    @Test
    public void testDeleteLab() throws Exception {

    }

    @Test
    public void testCommmandLine() throws IOException, InterruptedException {
        CommandLine commandLine = new CommandLine("ls");
        commandLine.addArgument("-a ",false);
        System.out.println(commandLine);

        DefaultExecutor defaultExecutor = new DefaultExecutor();
        File workingDirectory = defaultExecutor.getWorkingDirectory();
        System.out.println(workingDirectory.getAbsolutePath());

        CountDownLatch latch = new CountDownLatch(1);



        defaultExecutor.execute(commandLine, new ExecuteResultHandler() {
            @Override
            public void onProcessComplete(int i) {
                System.out.println(i);
                latch.countDown();
            }

            @Override
            public void onProcessFailed(ExecuteException e) {
                System.out.println(e);
                latch.countDown();
            }
        });

        latch.await();
    }

    @Test
    public void testApplicationId(){
        ApplicationId applicationId = ApplicationId.newInstance(13029191L, 12);
        System.out.println(applicationId);

    }

}