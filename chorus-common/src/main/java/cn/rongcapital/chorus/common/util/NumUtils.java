package cn.rongcapital.chorus.common.util;

import java.text.NumberFormat;

/**
 * Created by Athletics on 2017/7/19.
 */
public class NumUtils {

    public static String percent(Long numerator,Long denominator){
        NumberFormat numberFormat = NumberFormat.getInstance();
        //精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        return numberFormat.format((float)numerator/(float)denominator*100);
    }

    public static String byteToGb(Long numerator){
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(4);
        return numberFormat.format((float)numerator/(float)1024/(float)1024/(float)1024);
    }
}
