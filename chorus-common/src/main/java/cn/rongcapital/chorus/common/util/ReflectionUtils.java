package cn.rongcapital.chorus.common.util;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {
	public static Object newInstance(String className) {
		try {
			Class<?> clazz = Class.forName(className);
			return newInstance(clazz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object newInstance(String className, Class<?> paramClass, Object paramObject) {
		try {
			Class<?> clazz = Class.forName(className);
			Constructor<?> constructor = clazz.getConstructor(paramClass);
			return constructor.newInstance(paramObject);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T newInstance(Class<T> clazz) {
		try {
			return (T) clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Attempt to find a {@link Method} on the supplied class with the supplied
	 * name and no parameters. Searches all superclasses up to
	 * <code>Object</code>.
	 * <p>
	 * Returns <code>null</code> if no {@link Method} can be found.
	 * 
	 * @param clazz the class to introspect
	 * @param name the name of the method
	 * @return the Method object, or <code>null</code> if none found
	 */
	public static Method findMethod(Class<?> clazz, String name) {
		return findMethod(clazz, name, new Class[0]);
	}

	/**
	 * Attempt to find a {@link Method} on the supplied class with the supplied
	 * name and parameter types. Searches all superclasses up to
	 * <code>Object</code>.
	 * <p>
	 * Returns <code>null</code> if no {@link Method} can be found.
	 * 
	 * @param clazz the class to introspect
	 * @param name the name of the method
	 * @param paramTypes the parameter types of the method (may be
	 *            <code>null</code> to indicate any signature)
	 * @return the Method object, or <code>null</code> if none found
	 */
	public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
		return MethodUtils.getAccessibleMethod(clazz, name, paramTypes);
	}

	/**
	 * Attempt to find a {@link Method} on the supplied class with the supplied
	 * name and parameter types. Searches all superclasses up to
	 * <code>Object</code>.
	 * <p>
	 * Returns <code>null</code> if no {@link Method} can be found.
	 * 
	 * @param clazz the class to introspect
	 * @param name the name of the method
	 *            <code>null</code> to indicate any signature)
	 * @return the Method object, or <code>null</code> if none found
	 */
	public static Method findSetMethod(Class<?> clazz, String name, Class<?> paramType) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(name, "Method name must not be null");
		Class<?> searchType = clazz;
		if (paramType != null) {
			return findMethod(clazz, name, paramType);
		} else {
			while (searchType != null) {
				Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType
						.getDeclaredMethods());
				for (Method method : methods) {
					if (name.equals(method.getName())) {
						if (method.getParameterTypes().length == 1) {
							return method;
						}
					}
				}
				searchType = searchType.getSuperclass();
			}
		}
		return null;
	}

	public static List<String> findBeanNames(Class<?> clazz) {
		List<String> beanNames = new ArrayList<String>();
		List<Method> getMethods = findGetMethods(clazz);
		if (getMethods != null) {
			for (Method getMethod : getMethods) {
				String methodName = getMethod.getName();
				if (methodName.startsWith("get")) {
					methodName = methodName.substring("get".length());
				} else if (methodName.startsWith("is")) {
					methodName = methodName.substring("is".length());
				}
				methodName = toLowerCase(methodName);
				beanNames.add(methodName);
			}
		}
		return beanNames;
	}

	/**
	 * Attempt to find a {@link Method} on the supplied class with the supplied
	 * name and parameter types. Searches all superclasses up to
	 * <code>Object</code>.
	 * <p>
	 * Returns <code>null</code> if no {@link Method} can be found.
	 * 
	 * @param clazz the class to introspect
	 *            <code>null</code> to indicate any signature)
	 * @return the Method object, or <code>null</code> if none found
	 */
	public static List<Method> findGetMethods(Class<?> clazz) {
		List<Method> getMethods = new ArrayList<Method>();
		Class<?> searchType = clazz;
		while (searchType != null) {
			Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
			for (Method method : methods) {
				String methodName = method.getName();
				Class<?>[] parameterTypes = method.getParameterTypes();
				if ((methodName.startsWith("get") || methodName.startsWith("is"))
						&& (parameterTypes == null || parameterTypes.length == 0)) {
					getMethods.add(method);
				}
			}
			searchType = searchType.getSuperclass();
		}
		return getMethods;
	}

	private static String toUpperCaseFirstChar(String str) {
		char[] chs = str.toCharArray();
		chs[0] = Character.toUpperCase(chs[0]);
		return String.valueOf(chs);
	}

	private static String toLowerCase(String str) {
		char[] chs = str.toCharArray();
		chs[0] = Character.toLowerCase(chs[0]);
		return String.valueOf(chs);
	}

	private static String toSetMethodName(String beanName) {
		return "set" + toUpperCaseFirstChar(beanName);
	}

	private static String toGetMethodName(String beanName) {
		return "get" + toUpperCaseFirstChar(beanName);
	}

	private static String toIsMethodName(String beanName) {
		return "is" + toUpperCaseFirstChar(beanName);
	}

	public static boolean hasGetMethod(Object target, String beanName) {
		String methodName = toGetMethodName(beanName);
		Method method = findMethod(target.getClass(), methodName);
		if (method == null) {
			methodName = toIsMethodName(beanName);
			method = findMethod(target.getClass(), methodName);
		}
		return method != null;
	}

	public static Method findGetMethod(Object target, String beanName) {
		String methodName = toGetMethodName(beanName);
		Method method = findMethod(target.getClass(), methodName);
		if (method == null) {
			methodName = toIsMethodName(beanName);
			method = findMethod(target.getClass(), methodName);
		}
		return method;
	}

	public static Object invokeGetMethod(Object target, String beanName) {
		Assert.notNull(beanName, "Bean name must not be null");
		String methodName = toGetMethodName(beanName);
		Method method = findMethod(target.getClass(), methodName);
		if (method == null) {
			methodName = toIsMethodName(beanName);
			method = findMethod(target.getClass(), methodName);
		}
		Assert.notNull(beanName, "Get method must not be null");
		return invokeMethod(method, target);
	}

	public static Object invokeSetMethod(Object target, String beanName, Object param) {
		String methodName = toSetMethodName(beanName);
		Class<?> paramTypes = (param != null) ? param.getClass() : null;
		Method method = findSetMethod(target.getClass(), methodName, paramTypes);
		return invokeMethod(method, target, param);
	}

	/**
	 * Invoke the specified {@link Method} against the supplied target object
	 * with no arguments. The target object can be <code>null</code> when
	 * invoking a static {@link Method}.
	 * <p>
	 * Thrown exceptions are handled via a call to
	 * {@link #handleReflectionException}.
	 * 
	 * @param method the method to invoke
	 * @param target the target object to invoke the method on
	 * @return the invocation result, if any
	 * @see #invokeMethod(Method, Object, Object[])
	 */
	public static Object invokeMethod(Method method, Object target) {
		return invokeMethod(method, target, new Object[0]);
	}

	/**
	 * Invoke the specified {@link Method} against the supplied target object
	 * with the supplied arguments. The target object can be <code>null</code>
	 * when invoking a static {@link Method}.
	 * <p>
	 * Thrown exceptions are handled via a call to
	 * {@link #handleReflectionException}.
	 * 
	 * @param method the method to invoke
	 * @param target the target object to invoke the method on
	 * @param args the invocation arguments (may be <code>null</code>)
	 * @return the invocation result, if any
	 */
	public static Object invokeMethod(Method method, Object target, Object... args) {
		try {
			return method.invoke(target, args);
		} catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	public static Object invokeMethod(Object target, String methodName, Class<?>[] argTypes, Object[] argValues) {
		Class<?> clazz = target.getClass();
		Method method = findMethod(clazz, methodName, argTypes);
		return invokeMethod(method, target, argValues);
	}

	public static Object invokeMethod(Object target, String methodName, String[] types, Object[] argValues) {
		try {
			Class<?> argTypes[] = new Class[types.length];
			for (int i = 0; i < types.length; i++) {
				argTypes[i] = Class.forName(types[i]);
			}
			return invokeMethod(target, methodName, argTypes, argValues);
		} catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	public static Field findField(Object entity, String fieldName) {
		return findField(entity.getClass(), fieldName);
	}

	public static Field findField(Class<?> clazz, String fieldName) {
		try {
			return clazz.getField(fieldName);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return null;
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Class<?> getFieldType(Object entity, String fieldName) {
		return getFieldType(entity.getClass(), fieldName);
	}

	public static Class<?> getFieldType(Class<?> clazz, String fieldName) {
		Field field = findField(clazz, fieldName);
		return (field != null) ? field.getType() : null;
	}

	public static void setFieldValue(Object entity, String name, Object value) {
		Field field = findField(entity, name);
		if (field == null) {
			throw new IllegalArgumentException(entity.getClass() + "中不包含字段" + name);
		}
		try {
			field.set(entity, value);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Handle the given reflection exception. Should only be called if no
	 * checked exception is expected to be thrown by the target method.
	 * <p>
	 * Throws the underlying RuntimeException or Error in case of an
	 * InvocationTargetException with such a root cause. Throws an
	 * IllegalStateException with an appropriate message else.
	 * 
	 * @param ex the reflection exception to handle
	 */
	public static void handleReflectionException(Exception ex) {
		if (ex instanceof NoSuchMethodException) {
			throw new IllegalStateException("Method not found: " + ex.getMessage());
		}
		if (ex instanceof IllegalAccessException) {
			throw new IllegalStateException("Could not access method: " + ex.getMessage());
		}
		if (ex instanceof InvocationTargetException) {
			handleInvocationTargetException((InvocationTargetException) ex);
		}
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		handleUnexpectedException(ex);
	}

	/**
	 * Handle the given invocation target exception. Should only be called if no
	 * checked exception is expected to be thrown by the target method.
	 * <p>
	 * Throws the underlying RuntimeException or Error in case of such a root
	 * cause. Throws an IllegalStateException else.
	 * 
	 * @param ex the invocation target exception to handle
	 */
	public static void handleInvocationTargetException(InvocationTargetException ex) {
		rethrowRuntimeException(ex.getTargetException());
	}

	/**
	 * Rethrow the given {@link Throwable exception}, which is presumably the
	 * <em>target exception</em> of an {@link InvocationTargetException}. Should
	 * only be called if no checked exception is expected to be thrown by the
	 * target method.
	 * <p>
	 * Rethrows the underlying exception cast to an {@link RuntimeException} or
	 * {@link Error} if appropriate; otherwise, throws an
	 * {@link IllegalStateException}.
	 * 
	 * @param ex the exception to rethrow
	 * @throws RuntimeException the rethrown exception
	 */
	public static void rethrowRuntimeException(Throwable ex) {
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		if (ex instanceof Error) {
			throw (Error) ex;
		}
		handleUnexpectedException(ex);
	}

	/**
	 * Throws an IllegalStateException with the given exception as root cause.
	 * 
	 * @param ex the unexpected exception
	 */
	private static void handleUnexpectedException(Throwable ex) {
		throw new IllegalStateException("Unexpected exception thrown", ex);
	}

}
