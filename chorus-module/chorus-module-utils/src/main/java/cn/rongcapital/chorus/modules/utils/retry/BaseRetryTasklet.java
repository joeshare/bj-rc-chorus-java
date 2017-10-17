package cn.rongcapital.chorus.modules.utils.retry;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * 重试机制Tasklet基类
 * 
 * @author kevin.gong
 * @Time 2017年5月19日 下午3:05:54
 */
@Slf4j
public abstract class BaseRetryTasklet implements Tasklet {

    protected RetryTemplate retryTemplate;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        initRetryTemplate();
        if (retryTemplate != null) {
            try {
                return retryTemplate.execute(new RetryCallback<RepeatStatus, Throwable>() {
                    @Override
                    public RepeatStatus doWithRetry(RetryContext retryContext) throws Throwable {
                        return execute(chunkContext, contribution);
                    }
                });
            } catch (Throwable e) {
                log.error("tasklet execute exception", e);
                if (e instanceof Exception) {
                    throw (Exception) e;
                } else {
                    throw new Exception("tasklet execute exception", e);
                }
            }
        } else {
            return execute(chunkContext, contribution);
        }
    }

    /**
     * 初始化retryTemplate
     * 
     * @return retryTemplate实例
     * @throws Exception
     */
    public abstract void initRetryTemplate() throws Exception;

    /**
     * 任务执行过程方法
     * 
     * @param chunkContext
     *            attributes shared between invocations but not between restarts
     * @param contribution
     *            mutable state to be passed back to update the current step
     *            execution
     * @return an RepeatStatus indicating whether processing is continuable.
     */
    public abstract RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) throws Exception;

}
