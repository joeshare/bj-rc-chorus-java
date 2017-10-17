package cn.rongcapital.chorus.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//TODO 融入TimeUtil，利用jodatime重写 by li.hzh
public class DateUtils {
    private static Map<String, DateFormat> formaters = null;
    /**
	 * 时间格式化对象(yyyy-MM-dd HH:mm:ss)
	 */
	public static final String FORMATER_SECOND = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMATER_DAY = "yyyy-MM-dd";
	
    private static DateFormat getDateFormater(String format) {
        if (formaters == null) {
            formaters = new HashMap<String, DateFormat>();
        }
        DateFormat formater = formaters.get(format);
        if (formater == null) {
            formater = new SimpleDateFormat(format);
            formaters.put(format, formater);
        }
        return formater;
    }

    public static Date parse(String strDate, String format) {
        try {
            return getDateFormater(format).parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String format(Date date, String format) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        return getDateFormater(format).format(date);
    }

    public static String format(String strDate, String srcFormat, String destFormat) {
        Date date = parse(strDate, srcFormat);
        return format(date, destFormat);
    }

    public static java.sql.Date createSqlDate(int year, int month, int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);
        return new java.sql.Date(calendar.getTimeInMillis());
    }

    public static Date createSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    public static Date toStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date toEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date toDayTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTime();
    }

    public static boolean compareDate(Date date1, Date date2) {
        int n = date1.compareTo(date2);
        if (n < 0) {
            return true;
        } else {
            return false;
        }
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }
    
	public static String convertDateFromLong(long time) {

		Date d = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(d);
	}

    /**
     * 获取今天间隔数的日期，正数为今天以后，负数为今天之前
     * @param date
     * @param intervalDate
     * @return
     */
    public static String getIntervalDay(Date date,int intervalDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendar.DATE,intervalDate);
        return new SimpleDateFormat(FORMATER_DAY).format(calendar.getTime());
    }

    public static List<String> getDateList(){
        List<String> list = new ArrayList<>();
        Date date = new Date();
        for(int i = -30; i <= -1; i++ ){
            list.add(getIntervalDay(date, i));
        }
        return list;
    }
}
