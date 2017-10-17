package cn.rongcapital.chorus.common.util;

import cn.rongcapital.chorus.common.crypto.encrypt.Md5Encoder;

/**
 * 密码相关工具类
 *
 * @author li.hzh
 * @date 2016-03-04 15:55
 */
public class PasswordUtil {

    /**
     * 给密码加密
     *
     * @param rawPassword
     * @return
     */
    public static String encrypt(String rawPassword) {
        return Md5Encoder.encode(rawPassword);
    }
}
