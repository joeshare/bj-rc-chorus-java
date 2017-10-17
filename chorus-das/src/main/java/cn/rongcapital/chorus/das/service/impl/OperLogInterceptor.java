package cn.rongcapital.chorus.das.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.rongcapital.chorus.das.service.OperLogService;

/**
 * 操作日志记录
 */
@Aspect
@Component
public class OperLogInterceptor {
	private final static Map<String,ArrayList<String>> LOG_PERMISSION = new HashMap<String,ArrayList<String>>();
	@Autowired(required = false)
	private OperLogService operLogService;
	
	static{
		ArrayList<String> methods = new ArrayList<String>();
		methods.add("save");
		LOG_PERMISSION.put("FuncServiceImpl", methods);
	}
	// 配置切入点,该方法无方法体,主要为方便同类中其他方法使用此处配置的切入点
		@Pointcut("execution(* cn.rongcapital.chorus.core.*.service..*(..))")
		public void aspect() {
		}

		/*
		 * 配置前置通知,使用在方法aspect()上注册的切入点 同时接受JoinPoint切入点对象,可以没有该参数
		 */
//		@Before("aspect()")
//	    public void log() {
//	        System.out.println("*************Log*******************");
//	    }
//	    //有参无返回值的方法
//		@After("aspect()")
//	    public void logArg(JoinPoint point) {
//	        //此方法返回的是一个数组，数组中包括request以及ActionCofig等类对象
//	        Object[] args = point.getArgs();
//	        if (args != null) {
//	        	String className = point.getTarget().getClass().toString();// 获取目标类名
//	    		className = className.substring(className.indexOf("cn"));
//	    		if (LOG_PERMISSION.containsKey(className)) {
//	    			String signature = point.getSignature().toString();// 获取目标方法签名
//	    			String methodName = signature.substring(signature.lastIndexOf(".") + 1, signature.indexOf("("));
//	    			if (LOG_PERMISSION.get(className).contains(methodName)) {
//	    				operLogService.save((String)args[0], (String)args[1], (String)args[2]);	
//	    			}
//	    			// System.out.println(methodName + "执行结果是：" + returnObj);
//	    		}
//	    		
//	            for (Object obj : args) {
//	            	
//	            }
//	        }
//	    }
	    //有参并有返回值的方法
		@AfterReturning(value="aspect()", returning="returnObj")
		public void logArgAndReturn(JoinPoint point, Object returnObj) {
			// 此方法返回的是一个数组，数组中包括request以及ActionCofig等类对象
			String className = point.getTarget().getClass().toString();// 获取目标类名
			className = className.substring(className.indexOf("cn"));
//			if (LOG_PERMISSION.containsKey(className)) {
//				String signature = point.getSignature().toString();// 获取目标方法签名
//				String methodName = signature.substring(signature.lastIndexOf(".") + 1, signature.indexOf("("));
//				if (LOG_PERMISSION.get(className).contains(methodName)) {
//					R r = (R) returnObj;
//					List<OperLogInfo> logs = r.getLogInfos();
//					for (OperLogInfo log : logs) {
//						operLogService.save(log.getTable(), log.getLog(), log.getRecord_key());	
//					}
//					
//				}
//				// System.out.println(methodName + "执行结果是：" + returnObj);
//			}
		}

}