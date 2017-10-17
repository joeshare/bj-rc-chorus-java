package cn.rongcapital.chorus.server.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.rongcapital.chorus.server.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;

/**
 * api方法拦截器
 * @author kevin.gong
 * @Time 2017年6月26日 下午3:19:34
 */
@Slf4j
@Aspect
@Component
public class ApiMethodAspect {

    @Pointcut("execution(public * cn.rongcapital.chorus.server.dashboard.controller.*Controller.*(..))")
    public void method(){}

    @Around("method()")  
    public Object methodInterceptor(ProceedingJoinPoint pjp) throws Throwable{  
        Object result = null;
        try {
            result = pjp.proceed(); 
        } catch (Exception e) {
            result = ResultVO.error();
            
            StringBuilder errorLog = new StringBuilder();
            errorLog.append("---------EXCEPTION---------");
            errorLog.append("\n");
            errorLog.append("CLASS：");
            String className = pjp.getTarget().toString();
            errorLog.append(className.substring(0, className.indexOf("@")));
            errorLog.append("\n");
            errorLog.append("METHOD：");
            errorLog.append(pjp.getSignature().getName());
            errorLog.append("(");
            Object[] param = pjp.getArgs();
            if(param!=null && param.length>0){
                errorLog.append(JSON.toJSONString(param[0]));
                for (int i = 1; i < param.length; i++) {
                    errorLog.append(", ");
                    errorLog.append(JSON.toJSONString(param[i]));
                }
            }
            errorLog.append(")");
            log.error(errorLog.toString(), e);
        }
        return result;  
    }  
}
