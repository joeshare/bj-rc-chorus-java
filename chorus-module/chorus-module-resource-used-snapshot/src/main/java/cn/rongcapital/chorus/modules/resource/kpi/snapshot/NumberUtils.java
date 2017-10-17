package cn.rongcapital.chorus.modules.resource.kpi.snapshot;

import java.math.BigDecimal;

/**
 * Created by hhlfl on 2017-7-17.
 */
public class NumberUtils {
    public static String keepPrecision(String number, int precision) {
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(precision, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    public static String keepPrecision(Number number, int precision) {
        return keepPrecision(String.valueOf(number), precision);
    }

    public static double keepPrecision(double number, int precision) {
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static float keepPrecision(float number, int precision) {
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(precision, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static double divide(int numerator , int denominator, int precision){
        if(denominator == 0)
            return 0.0;

        BigDecimal num1 = new BigDecimal(numerator);
        BigDecimal num2 = new BigDecimal(denominator);
        return num1.divide(num2,precision,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double divide(long numerator , long denominator, int precision){
        if(denominator == 0)
            return 0.0;

        BigDecimal num1 = new BigDecimal(numerator);
        BigDecimal num2 = new BigDecimal(denominator);
        return num1.divide(num2,precision,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
