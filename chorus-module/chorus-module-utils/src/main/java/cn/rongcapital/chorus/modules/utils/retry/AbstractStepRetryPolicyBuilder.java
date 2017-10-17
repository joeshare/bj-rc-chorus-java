package cn.rongcapital.chorus.modules.utils.retry;

import org.springframework.retry.RetryPolicy;

/**
 * 步骤重试策略构造器基类
 * 
 * @author kevin.gong
 * @Time 2017年5月17日 下午2:23:30
 */
public abstract class AbstractStepRetryPolicyBuilder {

    /**
     * 重试次数
     */
    protected int retryCount;

    /**
     * 构造器
     * 
     * @param retryCount
     *            重试次数
     */
    public AbstractStepRetryPolicyBuilder(int retryCount) {
        this.retryCount = retryCount;
    }

    /**
     * RetryPolicy实例构造器
     * 
     * @return RetryPolicy实例
     */
    public abstract RetryPolicy build();
}
