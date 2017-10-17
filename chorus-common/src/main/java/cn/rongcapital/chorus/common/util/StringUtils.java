package cn.rongcapital.chorus.common.util;

import java.util.ArrayList;
import java.util.List;

import cn.rongcapital.chorus.common.datastructure.ListMap;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static List<String> toList(String valueString) {
        List<String> values = new ArrayList<String>();
        if (StringUtils.isNotEmpty(valueString)) {
            String[] tokens = valueString.split(",");
            if (tokens != null) {
                for (String token : tokens) {
                    values.add(token);
                }
            }
        }

        return values;
    }

    public static ListMap<String, String> toListMap(String valueString) {
        ListMap<String, String> listMap = new ListMap<String, String>();
        String[] tokens = valueString.split(",");
        if (tokens != null) {
            for (String token : tokens) {
                String[] subTokens = token.split("=");
                if (subTokens.length == 1) {
                    listMap.put(subTokens[0], subTokens[0]);
                } else if (subTokens.length == 2) {
                    listMap.put(subTokens[0], subTokens[1]);
                }
            }
        }
        return listMap;
    }

    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        // 全角的unicode 12288
        str = str.replace((char) 12288, ' ');
        if ("".equals(str.trim())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean notEquals(String str1, String str2) {
        return !equals(str1, str2);
    }
}
