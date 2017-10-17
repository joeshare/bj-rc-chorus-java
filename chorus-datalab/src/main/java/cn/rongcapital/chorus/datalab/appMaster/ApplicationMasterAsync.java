package cn.rongcapital.chorus.datalab.appMaster;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.client.api.async.NMClientAsync;
import org.apache.hadoop.yarn.client.api.async.impl.NMClientAsyncImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by abiton on 01/03/2017.
 */
public class ApplicationMasterAsync {
    private static final Log log = LogFactory.getLog(ApplicationMasterAsync.class);
    private Configuration conf;
    private FileSystem fs;
    private String gitPath;
    private String gitBranch;
    private int memory;
    private int vcores;
    private String applicationId;
    private String zeppelinUri;
    private NMClientAsyncImpl nmClientAsync;
    private NMCallbackHandler containerListener;
    private AMRMClientAsync<ContainerRequest> amRMClient;
    private int numTotalContainers = 1;
    private AtomicInteger numAllocatedContainers = new AtomicInteger();
    private AtomicInteger numRequestedContainers = new AtomicInteger();
    private AtomicInteger numCompletedContainers = new AtomicInteger();
    // Count of failed containers
    private AtomicInteger numFailedContainers = new AtomicInteger();
    private List<Thread> launchThreads = new ArrayList<Thread>();
    private volatile boolean done;

    public static void main(String[] args) {
        boolean result = false;
        try {
            ApplicationMasterAsync appMaster = new ApplicationMasterAsync();
            appMaster.run(args);
            result = appMaster.finish();
        } catch (Exception e) {
            log.error("applicationmaster error", e);
        }
        if (result) {
            System.exit(0);
        } else {
            System.exit(2);
        }

    }

    private boolean finish() {
        while (!done) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {

            }
        }
        for (Thread launchThread : launchThreads) {
            try {
                launchThread.join(10000);
            } catch (InterruptedException e) {
                log.error("Exception thrown in thread join", e);
                e.printStackTrace();
            }
        }

        // When the application completes, it should stop all running containers
        log.info("Application completed. Stopping running containers");
        nmClientAsync.stop();

        // When the application completes, it should send a finish application
        // signal to the RM
        log.info("Application completed. Signalling finish to RM");

        FinalApplicationStatus appStatus;
        String appMessage = null;
        boolean success = true;
        if (numFailedContainers.get() == 0 &&
                numCompletedContainers.get() == numTotalContainers) {
            appStatus = FinalApplicationStatus.SUCCEEDED;
        } else {
            appStatus = FinalApplicationStatus.FAILED;
            appMessage = "Diagnostics." + ", total=" + numTotalContainers
                    + ", completed=" + numCompletedContainers.get() + ", allocated="
                    + numAllocatedContainers.get() + ", failed="
                    + numFailedContainers.get();
            log.info(appMessage);
            success = false;
        }
        try {
            amRMClient.unregisterApplicationMaster(appStatus, appMessage, null);
        } catch (YarnException ex) {
            log.error("Failed to unregister application", ex);
        } catch (IOException e) {
            log.error("Failed to unregister application", e);
        }

        amRMClient.stop();
        return success;
    }

    public void run(String[] args) throws IOException, YarnException {
        gitPath = args[0];
        gitBranch = args[1];
        memory = Integer.parseInt(args[2]);
        vcores = Integer.parseInt(args[3]);
        zeppelinUri = args[4];
        applicationId = args[5];
        // Initialize clients to ResourceManager and NodeManagers
        conf = new YarnConfiguration();
        conf.addResource("hdfs-site.xml");
        fs = FileSystem.get(conf);

        AMRMClientAsync.CallbackHandler allocListener = new RMCallbackHandler();
        amRMClient = AMRMClientAsync.createAMRMClientAsync(1000, allocListener);
        amRMClient.init(conf);
        amRMClient.start();

        containerListener = new NMCallbackHandler(this);
        nmClientAsync = new NMClientAsyncImpl(containerListener);
        nmClientAsync.init(conf);
        nmClientAsync.start();

        String appMasterHostname = NetUtils.getHostname();
        int appMasterRpcPort = -1;

        String appMasterTrackingUrl = "";
        RegisterApplicationMasterResponse response = amRMClient
                .registerApplicationMaster(appMasterHostname, appMasterRpcPort,
                        appMasterTrackingUrl);

        List<Container> runningContainers = response.getContainersFromPreviousAttempts();
        log.info("running Container number is " + runningContainers.size());

        int numTotalContainersToRequest =
                numTotalContainers - runningContainers.size();

        for (int i = 0; i < numTotalContainersToRequest; ++i) {
            ContainerRequest containerAsk = setupContainerAskForRM();
            amRMClient.addContainerRequest(containerAsk);
        }

        numRequestedContainers.set(numTotalContainers);

    }

    private ContainerRequest setupContainerAskForRM() {
        // setup requirements for hosts
        // using * as any host will do for the distributed shell app
        // set the priority for the request
        int requestPriority = 0;
        Priority pri = Priority.newInstance(requestPriority);

        // Set up resource type requirements
        // For now, memory and CPU are supported so we set memory and cpu requirements
        Resource capability = Resource.newInstance(memory, vcores);

        ContainerRequest request = new ContainerRequest(capability, null, null, pri);
        log.info("Requested container ask: " + request.toString());
        return request;
    }


    class NMCallbackHandler implements NMClientAsync.CallbackHandler {

        private ConcurrentMap<ContainerId, Container> containers =
                new ConcurrentHashMap<ContainerId, Container>();
        private final ApplicationMasterAsync applicationMaster;

        public NMCallbackHandler(ApplicationMasterAsync applicationMaster) {
            this.applicationMaster = applicationMaster;
        }

        public void addContainer(ContainerId containerId, Container container) {
            containers.putIfAbsent(containerId, container);
        }


        @Override
        public void onContainerStarted(ContainerId containerId, Map<String, ByteBuffer> allServiceResponse) {
            log.info("nmcallbackhandler oncontainerstart");
            log.info(containerId);
            Container container = containers.get(containerId);
            if (container != null) {
                applicationMaster.nmClientAsync.getContainerStatusAsync(containerId, container.getNodeId());
            }
        }

        @Override
        public void onContainerStatusReceived(ContainerId containerId, ContainerStatus containerStatus) {
            log.info("nmcallbackhandler oncontainerstatusreceived");
            log.info(containerId + ": " + containerStatus);
            log.info("Container Status: id=" + containerId + ", status=" +
                    containerStatus);
        }

        @Override
        public void onContainerStopped(ContainerId containerId) {
            log.info("nmcallbackhandler onContainerstoped");
            log.info(containerId);
            containers.remove(containerId);
        }

        @Override
        public void onStartContainerError(ContainerId containerId, Throwable t) {
            log.info("nmcallbackhandler onstartcontainererror");
            log.info(containerId);
            log.error(t);
            containers.remove(containerId);
            applicationMaster.numCompletedContainers.incrementAndGet();
            applicationMaster.numFailedContainers.incrementAndGet();
            if (numCompletedContainers.get() == numTotalContainers) {
                done = true;
            }
        }

        @Override
        public void onGetContainerStatusError(ContainerId containerId, Throwable t) {
            log.info("nmcallbackhandler ongetcontainerstatuserror");
            log.info(containerId);
            log.error("container status error", t);
        }

        @Override
        public void onStopContainerError(ContainerId containerId, Throwable t) {
            log.info("nmcallbackhandler onstopcontainererror");
            log.info(containerId);
            log.error("stop contaienr error", t);
            containers.remove(containerId);
        }
    }

    class RMCallbackHandler implements AMRMClientAsync.CallbackHandler {

        @Override
        public void onContainersCompleted(List<ContainerStatus> statuses) {
            log.info("rmcallbackhandler oncontainerscompleted");
            log.debug(statuses);
            for (ContainerStatus status : statuses) {
                int exitStatus = status.getExitStatus();
                if (exitStatus != 0) {
                    numRequestedContainers.decrementAndGet();
                    numAllocatedContainers.decrementAndGet();
                    numFailedContainers.incrementAndGet();
                }
                numCompletedContainers.incrementAndGet();
            }

            if (numCompletedContainers.get() == numTotalContainers) {
                done = true;
            }
        }

        @Override
        public void onContainersAllocated(List<Container> containers) {
            log.info("rmcallbackhandler onconainerallocated");
            log.debug(containers);
            for (Container container : containers) {
                LaunchContainerRunnable containerRunnable = new LaunchContainerRunnable(container, containerListener);
                Thread launchThread = new Thread(containerRunnable);
                launchThreads.add(launchThread);
                launchThread.start();
                numAllocatedContainers.incrementAndGet();
            }

        }

        @Override
        public void onShutdownRequest() {
            done = true;
        }

        @Override
        public void onNodesUpdated(List<NodeReport> updatedNodes) {

        }

        @Override
        public float getProgress() {
            return 1.0f;
        }

        @Override
        public void onError(Throwable e) {
            done = true;
            amRMClient.stop();
        }
    }

    private class LaunchContainerRunnable implements Runnable {
        Container container;
        NMCallbackHandler containerListener;

        public LaunchContainerRunnable(Container container, NMCallbackHandler containerListener) {
            this.container = container;
            this.containerListener = containerListener;
        }

        @Override
        public void run() {
            log.info("launch container for id " + container.getId());

            ContainerLaunchContext ctx =
                    Records.newRecord(ContainerLaunchContext.class);
            Map<String, LocalResource> localResources = new HashMap<>();

            Path path = new Path(fs.getHomeDirectory(), zeppelinUri);
            FileStatus fileStatus;
            try {
                fileStatus = fs.getFileStatus(path);
            } catch (IOException e) {
                log.error(e);
                throw new RuntimeException(e);
            }
            LocalResource localResource = LocalResource.newInstance(ConverterUtils.getYarnUrlFromPath(path)
                    , LocalResourceType.ARCHIVE, LocalResourceVisibility.APPLICATION
                    , fileStatus.getLen(), fileStatus.getModificationTime());
            localResources.put("zeppelin", localResource);
            Map<String, String> env = new HashMap<>();
            env.put("SPARK_SUBMIT_OPTIONS","--queue " + gitBranch.split("-")[0]);

            ctx.setEnvironment(env);
            ctx.setLocalResources(localResources);
            ctx.setCommands(
                    Collections.singletonList(
                            "chmod -R +w ./zeppelin/resources && " +
                                    "cd zeppelin/conf && " +
                                    "rm -f interpreter.json && " +
                                    "rm -rf ../resources/notebook && mkdir ../resources/notebook && " +
                                    "ln -s ../resources/notebook/interpreter.json interpreter.json && " +
                                    "../bin/zeppelin.sh " + gitPath + " " + gitBranch +
                                    " " + applicationId +
                                    " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" +
                                    " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr"
                    ));

            log.info("Launching container " + container.getId());
            containerListener.addContainer(container.getId(), container);
            nmClientAsync.startContainerAsync(container, ctx);

        }
    }
}

