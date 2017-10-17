package cn.rongcapital.chorus.modules.solr.health.indicator;


import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yimin
 */
@Getter
public class Shard implements Serializable {
    private static final long serialVersionUID = -5778943861950791799L;
    public final String name;
    public final String state;
    public final List<Replica> replicas = new ArrayList<>();

    public Shard(String name, String state) {
        this.name = name;
        this.state = state;
    }

    public Shard add(Replica one) {
        replicas.add(one);
        return this;
    }
}
