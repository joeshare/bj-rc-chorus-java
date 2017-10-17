package cn.rongcapital.chorus.monitor.agent.util;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class ConfigCenter {

    private static final String CONFIG_PATH_INCLASSPATH = "config/application.properties";
    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(ClassLoader.getSystemResourceAsStream(CONFIG_PATH_INCLASSPATH));
        } catch (IOException e) {
            log.error("Fatal error!!!!", e);
        }
    }

    public static final String getBrokers() {
        return properties.getProperty("kafka.brokers");
    }

    public static final double getThresHold() {
        return Double.parseDouble(properties.getProperty("network.out.threshold"));
    }

    public static final int getMinIntevrval() {
        return Integer.parseInt(properties.getProperty("over.threshold.notify.min.interval.minute"));
    }
}
