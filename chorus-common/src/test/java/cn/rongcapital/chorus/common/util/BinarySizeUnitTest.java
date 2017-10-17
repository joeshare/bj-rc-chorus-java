package cn.rongcapital.chorus.common.util;

import org.junit.Test;

import static cn.rongcapital.chorus.common.util.BinarySizeUnit.BYTES;
import static cn.rongcapital.chorus.common.util.BinarySizeUnit.C0;
import static cn.rongcapital.chorus.common.util.BinarySizeUnit.C1;
import static cn.rongcapital.chorus.common.util.BinarySizeUnit.C2;
import static cn.rongcapital.chorus.common.util.BinarySizeUnit.C3;
import static cn.rongcapital.chorus.common.util.BinarySizeUnit.C4;
import static cn.rongcapital.chorus.common.util.BinarySizeUnit.C5;
import static cn.rongcapital.chorus.common.util.BinarySizeUnit.C6;
import static cn.rongcapital.chorus.common.util.BinarySizeUnit.EXA;
import static cn.rongcapital.chorus.common.util.BinarySizeUnit.GIGA;
import static cn.rongcapital.chorus.common.util.BinarySizeUnit.KILO;
import static cn.rongcapital.chorus.common.util.BinarySizeUnit.MEGA;
import static cn.rongcapital.chorus.common.util.BinarySizeUnit.PETA;
import static cn.rongcapital.chorus.common.util.BinarySizeUnit.TERA;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author yimin
 */
public class BinarySizeUnitTest {

    @Test
    public void bytes() throws Exception {
        assertThat(BYTES.bytes(C0)).isEqualTo(1);
        assertThat(BYTES.kilo(C1)).isEqualTo(1);
        assertThat(BYTES.mega(C2)).isEqualTo(1);
        assertThat(BYTES.giga(C3)).isEqualTo(1);
        assertThat(BYTES.tera(C4)).isEqualTo(1);
        assertThat(BYTES.peta(C5)).isEqualTo(1);
        assertThat(BYTES.exa(C6)).isEqualTo(1);
        assertThat(BYTES.convert(1, EXA)).isEqualTo(C6);
    }

    @Test
    public void kilo() throws Exception {
        assertThat(KILO.bytes(C0)).isEqualTo(C1);
        assertThat(KILO.kilo(1)).isEqualTo(1);
        assertThat(KILO.mega(C1)).isEqualTo(1);
        assertThat(KILO.giga(C2)).isEqualTo(1);
        assertThat(KILO.tera(C3)).isEqualTo(1);
        assertThat(KILO.peta(C4)).isEqualTo(1);
        assertThat(KILO.exa(C5)).isEqualTo(1);
        assertThat(KILO.convert(1, EXA)).isEqualTo(C5);
    }

    @Test
    public void mega() throws Exception {
        assertThat(MEGA.bytes(C0)).isEqualTo(C2);
        assertThat(MEGA.kilo(C0)).isEqualTo(C1);
        assertThat(MEGA.mega(1)).isEqualTo(1);
        assertThat(MEGA.giga(C1)).isEqualTo(1);
        assertThat(MEGA.tera(C2)).isEqualTo(1);
        assertThat(MEGA.peta(C3)).isEqualTo(1);
        assertThat(MEGA.exa(C4)).isEqualTo(1);
        assertThat(MEGA.convert(1, EXA)).isEqualTo(C4);
    }

    @Test
    public void giga() throws Exception {
        assertThat(GIGA.bytes(C0)).isEqualTo(C3);
        assertThat(GIGA.kilo(C0)).isEqualTo(C2);
        assertThat(GIGA.mega(C0)).isEqualTo(C1);
        assertThat(GIGA.giga(1)).isEqualTo(1);
        assertThat(GIGA.tera(C1)).isEqualTo(1);
        assertThat(GIGA.peta(C2)).isEqualTo(1);
        assertThat(GIGA.exa(C3)).isEqualTo(1);
        assertThat(GIGA.convert(1, EXA)).isEqualTo(C3);
    }

    @Test
    public void tera() throws Exception {
        assertThat(TERA.bytes(C0)).isEqualTo(C4);
        assertThat(TERA.kilo(C0)).isEqualTo(C3);
        assertThat(TERA.mega(C0)).isEqualTo(C2);
        assertThat(TERA.giga(C0)).isEqualTo(C1);
        assertThat(TERA.tera(1)).isEqualTo(1);
        assertThat(TERA.peta(C1)).isEqualTo(1);
        assertThat(TERA.exa(C2)).isEqualTo(1);
        assertThat(TERA.convert(1, EXA)).isEqualTo(C2);
    }

    @Test
    public void peta() throws Exception {
        assertThat(PETA.bytes(C0)).isEqualTo(C5);
        assertThat(PETA.kilo(C0)).isEqualTo(C4);
        assertThat(PETA.mega(C0)).isEqualTo(C3);
        assertThat(PETA.giga(C0)).isEqualTo(C2);
        assertThat(PETA.tera(C0)).isEqualTo(C1);
        assertThat(PETA.peta(1)).isEqualTo(1);
        assertThat(PETA.exa(C1)).isEqualTo(1);
        assertThat(PETA.convert(1, EXA)).isEqualTo(C1);
    }

    @Test
    public void exa() throws Exception {
        assertThat(EXA.bytes(C0)).isEqualTo(C6);
        assertThat(EXA.kilo(C0)).isEqualTo(C5);
        assertThat(EXA.mega(C0)).isEqualTo(C4);
        assertThat(EXA.giga(C0)).isEqualTo(C3);
        assertThat(EXA.tera(C0)).isEqualTo(C2);
        assertThat(EXA.peta(C0)).isEqualTo(C1);
        assertThat(EXA.exa(1)).isEqualTo(1);
        assertThat(EXA.convert(1, EXA)).isEqualTo(1);
    }
}
