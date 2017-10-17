package cn.rongcapital.chorus.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

//TODO by li.hzh 临时仅为编译通过保留，后续移入ApplicationContextFactory。
public class SpringBeanUtils {
    private static ApplicationContext applicationContext = null;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringBeanUtils.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> type) {
        return applicationContext.getBean(type);
    }

    /**
     * 获取对象
     *
     * @param id
     * @return Object 一个以所给名字注册的bean的实例
     * @throws BeansException
     */
    public static Object getBean(String id) {
        return applicationContext.getBean(id);
    }

    public static <T> T getBean(String id, Class<T> type) {
        return applicationContext.getBean(id, type);
    }

    public static Object invokeMethod(String beanId, String methodName, Class<?>[] argTypes, Object[] argValues) {
        return ReflectionUtils.invokeMethod(getBean(beanId), methodName, argTypes, argValues);
    }

    public static Object invokeMethod(String beanId, String method, String[] types, Object[] values) {
        return ReflectionUtils.invokeMethod(getBean(beanId), method, types, values);
    }

    /**
     * spring 发送消息
     *
     * @param event
     * @author yunzhong
     * @version
     * @since 2017年5月18日
     */
    public static void publish(ApplicationEvent event) {
        applicationContext.publishEvent(event);
    }
}
