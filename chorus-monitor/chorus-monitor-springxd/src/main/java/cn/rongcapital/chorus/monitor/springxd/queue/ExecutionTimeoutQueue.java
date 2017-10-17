package cn.rongcapital.chorus.monitor.springxd.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import cn.rongcapital.chorus.das.entity.TaskExecutionTimeout;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExecutionTimeoutQueue {

    private static final ArrayBlockingQueue<TaskExecutionTimeout> queue = new ArrayBlockingQueue<>(10000);

    private static final Long DEFAULT_TIME_OUT = 1000L;

    /**
     * 
     * @return
     * @author yunzhong
     * @version 
     * @since 2017年5月25日
     */
    public static TaskExecutionTimeout pollTimeout() {
        try {
            return queue.poll(DEFAULT_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.warn("Interrupted when poll xdexcution from queue", e);
        }
        return null;
    }

    /**
     * @param xdExu
     * @return
     * @author yunzhong
     * @version 
     * @since 2017年5月25日
     */
    public static boolean offerTimeout(TaskExecutionTimeout xdExu) {
        try {
            return queue.offer(xdExu, DEFAULT_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.warn("Interruped when waiting to insert execution [" + xdExu.getTaskExecutionId() + "]  into queue", e);
        }
        return false;
    }
}
