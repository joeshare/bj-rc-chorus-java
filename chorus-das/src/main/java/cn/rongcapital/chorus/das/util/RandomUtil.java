package cn.rongcapital.chorus.das.util;

import java.util.Random;

/**
 * Created by Athletics on 2017/8/30.
 */
public class RandomUtil {

    private static final Random r = new Random();

    public static long getQualifiedName() {
        int bound = 100000;
        int v = r.nextInt(bound);
        return System.currentTimeMillis() * bound + v;
    }
}
