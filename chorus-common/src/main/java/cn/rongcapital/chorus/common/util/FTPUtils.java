package cn.rongcapital.chorus.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.net.SocketException;

/**
 * Created by Athletics on 2017/9/6.
 */
@Slf4j
public class FTPUtils {
    private static final String FTP_CONNECTION_URL = "ftp://%s:%s@%s:%s/%s"; // 连接地址 ftp://user:password@host:port/path

    public static boolean testFtpConnection(String host, String port, String userName, String password) {
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(host, Integer.valueOf(port));// 连接FTP服务器
            ftpClient.login(userName, password);// 登陆FTP服务器
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                log.info("未连接到FTP，用户名或密码错误。");
                ftpClient.disconnect();
                return false;
            } else {
                log.info("FTP连接成功。");
                ftpClient.logout();
                ftpClient.disconnect();
                return true;
            }
        } catch (SocketException e) {
            e.printStackTrace();
            log.info("FTP的IP地址可能错误，请正确配置。");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            log.info("FTP的端口错误,请正确配置。");
            return false;
        }
    }

    public static String getFtpConnectionUrl(String host, String port, String userName, String password, String path) {
        return String.format(FTP_CONNECTION_URL, userName, password, host, port, path.startsWith("/") ? path.substring(1) : path);
    }
}
