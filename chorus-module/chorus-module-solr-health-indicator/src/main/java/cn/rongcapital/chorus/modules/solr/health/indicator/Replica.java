package cn.rongcapital.chorus.modules.solr.health.indicator;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * @author yimin
 */
@Builder
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Replica implements Serializable {
    private static final long serialVersionUID = -1291719444504777076L;
    public final String  name;
    public final String  core;
    public final String  base_url;
    public final String  node_name;
    public final String  state;
    public final boolean leader;
}
