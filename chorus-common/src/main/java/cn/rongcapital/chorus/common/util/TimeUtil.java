package cn.rongcapital.chorus.common.util;

import org.joda.time.DateTime;

/**
 * 时间相关工具类
 *
 * @author li.hzh
 * @date 16/2/26
 */
public class TimeUtil {

    private static final String DEFAULT_DISPLAY_PATTERN = "yyyy年MM月dd日 HH时mm分ss秒";

    /**
     * 获取当前时间的文本格式信息.
     * 如果{@code pattern} 为NULL, 则使用默认格式.
     *
     * @param pattern 时间格式
     * @return
     */
    public static String getCurrentTimeStr(String pattern) {
        if (pattern == null) {
            pattern = DEFAULT_DISPLAY_PATTERN;
        }
        DateTime dateTime = new DateTime(System.currentTimeMillis());
        return dateTime.toString(pattern);
    }

    /**
     * @return
     * @see TimeUtil#getCurrentTimeStr(String)
     */
    public static String getCurrentTimeStr() {
        return getCurrentTimeStr(null);
    }

}