package cn.rongcapital.chorus.common.util;

import java.util.Date;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {
	public static List<String> getBeanFieldNames(Object bean) {
		Class<?> clazz = bean.getClass();
		return ReflectionUtils.findBeanNames(clazz);
	}

	public static Object getValue(Object bean, String fieldName) {
		String[] tokens = fieldName.split("\\.");
		return getValue(bean, tokens);
	}

	public static boolean contains(Object bean, String fieldName) {
		String[] fieldNames = fieldName.split("\\.");
		return contains(bean, fieldNames);
	}

	public static Object getValue(Object bean, String[] fieldNames) {
		return getValue(bean, fieldNames, 0);
	}

	public static Object getValue(Object bean, String[] fieldNames, int offset) {
		Object fieldValue = bean;
		for (int i = offset; i < fieldNames.length; i++) {
			fieldValue = ReflectionUtils.invokeGetMethod(fieldValue, fieldNames[i]);
			if (fieldValue == null) {
				break;
			}
		}
		return fieldValue;
	}

	public static boolean contains(Object object, String[] tokens) {
		boolean is = true;
		Object fieldValue = object;
		for (int i = 0; i < tokens.length; i++) {
			if (!ReflectionUtils.hasGetMethod(object, tokens[i])) {
				is = false;
				break;
			}

			fieldValue = ReflectionUtils.invokeGetMethod(fieldValue, tokens[i]);
			if (fieldValue == null) {
				break;
			}
		}
		return is;
	}
	
	public static Object toObject(String clazzPath, String jsonString) {
		Object object = ReflectionUtils.newInstance(clazzPath);
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Set<?> keys = jsonObject.keySet();
		if(keys != null){
			for(Object o : keys){
				String name = (String)o;
				Object value = jsonObject.get(name);
				ReflectionUtils.invokeSetMethod(object, name, value);
			}
		}

		return object;
	}
	
	public static JSONObject toJsonObject(Object o) {
		Class<?> clazz = o.getClass();
		List<String> fields = ReflectionUtils.findBeanNames(clazz);
		return toJsonObject(o, fields.toArray(new String[0]));
	}

	public static JSONObject toJsonObject(Object o, String[] fields) {
		return toJsonObject(o, fields, fields);
	}

	public static JSONObject toJsonObject(Object o, String[] objFields, String[] jsonFields) {
		JSONObject jsonObject = new JSONObject();

		if (objFields == null) {
			List<String> fs = BeanUtils.getBeanFieldNames(o);
			objFields = fs.toArray(new String[0]);
		}
		
		if(jsonFields == null){
			jsonFields = objFields;
		}

		if (objFields != null) {
			for (int i = 0;i < objFields.length;i++) {
				String field = objFields[i];
				// 处理日期型的数据与json的转换
				if (field.indexOf("|") > 0) {
					String strValue = null;
					String[] fieldArray = StringUtils.split(field, "|");
					Object value = getValue(o, fieldArray[0]);
					if (value instanceof Date) {
						strValue = DateUtils.format((Date) value, fieldArray[1]);
					} else {
						strValue = String.valueOf(value);
					}
					
					String[] jsonFieldArray = StringUtils.split(jsonFields[i], "|");
					jsonObject.element(jsonFieldArray[0], strValue);
				} else {
					Object value = getValue(o, field);
					jsonObject.element(jsonFields[i], value);
				}
			}
		}

		return jsonObject;
	}
}
