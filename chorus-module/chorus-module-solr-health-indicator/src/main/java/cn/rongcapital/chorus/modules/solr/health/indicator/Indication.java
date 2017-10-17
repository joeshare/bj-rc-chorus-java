package cn.rongcapital.chorus.modules.solr.health.indicator;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author yimin
 */
@Builder
@Getter
public class Indication implements Serializable {
    private static final long serialVersionUID = 3116680728558961141L;

    private final Object  expect;
    private final Object  actual;
    private final String  message;
    private final boolean status;

    @Override
    public String toString() {
        return "Indicator{" +
               "status=" + status +
               ", expect=" + expect +
               ", actual=" + actual +
               ", message='" + message + '\'' +
               '}';
    }
}
