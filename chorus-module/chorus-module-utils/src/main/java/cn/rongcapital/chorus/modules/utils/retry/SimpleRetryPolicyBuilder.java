package cn.rongcapital.chorus.modules.utils.retry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.util.CollectionUtils;

/**
 * 简单步骤重试策略构造器
 * 
 * @author kevin.gong
 * @Time 2017年5月17日 下午2:25:45
 */
public class SimpleRetryPolicyBuilder extends AbstractStepRetryPolicyBuilder {

    /**
     * 构造器
     * 
     * @param retryCount
     *            重试次数
     */
    public SimpleRetryPolicyBuilder(int retryCount) {
        super(retryCount);
    }

    private List<Class<? extends Throwable>> retryPolicyException;

    public AbstractStepRetryPolicyBuilder retryPolicyException(List<Class<? extends Throwable>> retryPolicyException) {
        this.retryPolicyException = retryPolicyException;
        return this;
    }

    @Override
    public RetryPolicy build() {
        ExceptionClassifierRetryPolicy retryPolicy = new ExceptionClassifierRetryPolicy();
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(retryCount);
        Map<Class<? extends Throwable>, RetryPolicy> policyMap = new HashMap<Class<? extends Throwable>, RetryPolicy>();
        if (CollectionUtils.isEmpty(retryPolicyException)) {
            policyMap.put(Exception.class, simpleRetryPolicy);
        } else {
            for (Class<? extends Throwable> exceptionC : retryPolicyException) {
                policyMap.put(exceptionC, simpleRetryPolicy);
            }
        }
        retryPolicy.setPolicyMap(policyMap);
        return retryPolicy;
    }
}
