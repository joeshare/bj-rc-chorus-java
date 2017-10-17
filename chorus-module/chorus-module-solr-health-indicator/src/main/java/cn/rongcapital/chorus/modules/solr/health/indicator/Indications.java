package cn.rongcapital.chorus.modules.solr.health.indicator;

import lombok.Getter;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yimin
 */
public class Indications implements Serializable {
    private static final long serialVersionUID = 3193103515994349467L;
    @Getter
    private final List<Indication> indications;

    public Indications() {
        indications = new ArrayList<>();
    }

    @Nonnull
    public Indications indication(@Nonnull Indication indication) {
        indications.add(indication);
        return this;
    }

    public boolean isNotEmpty() {
        return indications.size() > 0;
    }

}
