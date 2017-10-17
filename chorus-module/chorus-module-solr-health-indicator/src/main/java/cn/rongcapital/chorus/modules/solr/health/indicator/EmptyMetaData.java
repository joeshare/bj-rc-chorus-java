package cn.rongcapital.chorus.modules.solr.health.indicator;

import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;

/**
 * Created by alan on 24/04/2017.
 */
@Mixin({})
public class EmptyMetaData {
    private String noCare;

    public String getNoCare() {
        return noCare;
    }

    @ModuleOption("Just for right")
    public void setNoCare(String noCare) {
        this.noCare = noCare;
    }
}

