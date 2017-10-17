package cn.rongcapital.chorus.common.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.rongcapital.chorus.common.datastructure.ListMap;

public class CollectionUtils extends org.apache.commons.collections.CollectionUtils {
	public static <T> Map<T, T> toMap(List<T> list) {
		Map<T, T> map = new HashMap<T, T>();
		if (list != null) {
			for (T o : list) {
				map.put(o, o);
			}
		}
		return map;
	}

	public static ListMap toListMap(List list) {
		ListMap listMap = new ListMap();
		if (list != null) {
			for (Object o : list) {
				listMap.put(o, o);
			}
		}
		return listMap;
	}

	public static String join(Collection<?> c, String split) {
		return join(c, null, split);
	}

	public static String join(Collection<?> c, String property, String split) {
		StringBuffer sb = new StringBuffer();
		int index = 0;
		Iterator<?> iterator = c.iterator();
		while (iterator.hasNext()) {
			if (index != 0) {
				sb.append(split);
			}
			Object o = iterator.next();
			if (StringUtils.isNotEmpty(property)) {
				String[] tokens = property.split("\\.");
				sb.append(BeanUtils.getValue(o, tokens));
			} else {
				sb.append(o);
			}

			index++;
		}
		return sb.toString();
	}

	public static Collection<?> removeEmpty(Collection<?> c) {
		if(c == null ){
			return null;
		}
		Iterator<?> ite = c.iterator();
		while (ite.hasNext()) {
			if (ite.next() == null) {
				ite.remove();
			}
		}
		return c;
	}
}
