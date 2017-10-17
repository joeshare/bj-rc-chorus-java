package cn.rongcapital.chorus.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import cn.rongcapital.chorus.common.util.SpringBeanUtils;
import cn.rongcapital.chorus.common.util.TimeUtil;
import cn.rongcapital.chorus.server.config.ApplicationConfig;

/**
 * Chorus服务启动器
 *
 * @author li.hzh
 * @date 16/2/26
 */
public class ChorusServerLauncher {

    private static Logger logger = LoggerFactory.getLogger(ChorusServerLauncher.class);

    public static void main(String[] args) {
        logger.info("开始启动Chorus服务.{}", TimeUtil.getCurrentTimeStr());
        long startTime = System.currentTimeMillis();
        ApplicationContext context = SpringApplication.run(ApplicationConfig.class, args);
        SpringBeanUtils.setApplicationContext(context);
        long endTime = System.currentTimeMillis();
        logger.info("Chorus服务启动完成, 耗时:{}ms.", (endTime - startTime));
    }

}
