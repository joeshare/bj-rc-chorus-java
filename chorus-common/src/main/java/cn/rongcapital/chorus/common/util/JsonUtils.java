package cn.rongcapital.chorus.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * json与对象之前转换
 *
 * @author lipeng
 *         <p>
 *         2016年3月17日
 */
public class JsonUtils {
	/**
	 * 对象转化成json字符串
	 *
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static String convet2Json(Object obj) throws Exception {
		JSONObject res = (JSONObject) JSONObject.toJSON(obj);
		return res.toString();
	}

	/**
	 * json字符串转化成json对象
	 *
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public static JSONObject toJsonObject(String json) throws Exception {
		return (JSONObject) JSONObject.parseObject(json);
	}

	/**
	 * json字符串转化成对象
	 *
	 * @param json
	 * @param beanClass
	 * @return
	 * @throws Exception
	 */
	public static <T> T Json2Object(String json, Class<T> beanClass) throws Exception {
		return JSONObject.toJavaObject(JSONObject.parseObject(json), beanClass);
	}

	/**
	 * json字符串转化成对象
	 *
	 * @param json
	 * @param beanClass
	 * @return
	 * @throws Exception
	 */
	// public static Object Json2Object(String json, Class<?> beanClass, Map<String, Class<?>> classMap) throws
	// Exception {
	// return JSONObject.toJavaObject(JSONObject.parseObject(json), beanClass);
	// }

	/**
	 * 给指定对象的指定属性赋上指定属性(含get或is方法的属性)名作为值
	 *
	 * @param o
	 *            指定对象
	 * @return
	 */
	public static JSONObject toJSONObject(Object o) {
		Class<?> clazz = o.getClass();
		List<String> fields = ReflectionUtils.findBeanNames(clazz);
		return JsonUtils.toJSONObject(o, fields.toArray(new String[0]));
	}

	/**
	 * 给指定对象的指定属性(含get或is方法的属性)赋上指定属性名作为值
	 *
	 * @param o
	 *            指定对象
	 * @param fields
	 *            指定属性
	 * @return
	 */
	public static JSONObject toJSONObject(Object o, String[] fields) {
		return JsonUtils.toJSONObject(o, fields, fields);
	}

	/**
	 * 为指定对象内指定属性(含get或is方法的属性)赋指定值，返回一个属性与值对应的json对象
	 *
	 * @param o
	 *            指定对象
	 * @param objFields
	 *            指定属性
	 * @param jsonFields
	 *            指定值
	 * @return
	 */
	public static JSONObject toJSONObject(Object o, String[] objFields, String[] jsonFields) {
		if (null == o) {
			return null;
		}
		JSONObject jsonObject = new JSONObject();

		// 如果没有指定属性则获取制定对象内属性
		if (objFields == null) {
			List<String> fs = BeanUtils.getBeanFieldNames(o);
			objFields = fs.toArray(new String[0]);
		}

		// 如果没有指定值则指定值等于指定属性名称
		if (jsonFields == null) {
			jsonFields = objFields;
		}

		// 指定属性不为空则遍历指定属性并为其赋上对应的指定值并转换为json对象
		if (objFields != null) {
			for (int i = 0; i < objFields.length; i++) {
				String field = objFields[i];
				Object value = BeanUtils.getValue(o, field);
				element(jsonObject, jsonFields[i], value);
			}
		}

		return jsonObject;
	}

	public static void element(JSONObject jsonObject, String name, Object valueObject) {
		jsonObject.put(name, valueObject);
	}

	public static JSONArray toJsonArray(String str) throws Exception {
		return JSONArray.parseArray(str);
	}

	public static <T> List<T> array2List(String str, Class<T> beanClass) throws Exception {
		return JSON.parseArray(str, beanClass);
	}

	public static void main(String[] args) {
		// String json = "[{\"type\":\"11\"},{\"type\":\"12\"}]";
		// try {
		// JSONArray jsonArray = toJsonArray(json);
		// List<ServiceConfigInfo> collection = (List<ServiceConfigInfo>) JSONArray.toCollection(jsonArray,
		// ServiceConfigInfo.class);
		// // ServiceUrlInfo ServiceUrlInfo = (ServiceUrlInfo) Json2Object(json,ServiceUrlInfo.class);
		// System.out.println("");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}
}
