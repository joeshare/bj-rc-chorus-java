package cn.rongcapital.chorus.modules.utils.retry;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * 简单Tasklet接口实现类
 * 
 * @author kevin.gong
 * @Time 2017年5月19日 上午10:08:46
 */
public abstract class SimpleTasklet extends BaseRetryTasklet {

    /**
     * retry策略
     */
    private RetryPolicy retryPolicy;

    public abstract RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) throws Exception;

    /**
     * 设置重试策略
     * 
     * @param retryPolicy
     *            重试策略
     * @return 原实例
     */
    public SimpleTasklet retryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    /**
     * 设置重试次数
     * <p>
     * 使用通用方法构建重试策略。对Exception进行retry，如果要设置特殊策略，可使用retryPolicy方法
     * 
     * @param retryCount
     *            重试次数
     * @return 原实例
     */
    public SimpleTasklet retry(Integer retryCount) {
        if (retryCount != null) {
            this.retryPolicy = new SimpleRetryPolicyBuilder(retryCount < 0 ? 1 : retryCount + 1).build();
        }
        return this;
    }

    /**
     * 设置重试次数
     * <p>
     * 使用通用方法构建重试策略。如果要设置特殊策略，可使用retryPolicy方法
     * 
     * @param retryCount
     *            重试次数
     * @param retryPolicyException
     *            引发重试的异常集合
     * @return 原实例
     */
    public SimpleTasklet retry(Integer retryCount, List<Class<? extends Throwable>> retryPolicyException) {
        if (retryCount != null) {
            this.retryPolicy = new SimpleRetryPolicyBuilder(retryCount < 0 ? 1 : retryCount + 1)
                    .retryPolicyException(retryPolicyException).build();
        }
        return this;
    }

    @Override
    public void initRetryTemplate() throws Exception {
        if (retryPolicy != null) {
            retryTemplate = new RetryTemplate();
            retryTemplate.setBackOffPolicy(new FixedBackOffPolicy());
            retryTemplate.setRetryPolicy(retryPolicy);
        }
    }
}
