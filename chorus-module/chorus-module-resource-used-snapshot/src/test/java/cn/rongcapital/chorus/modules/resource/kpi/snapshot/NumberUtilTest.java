package cn.rongcapital.chorus.modules.resource.kpi.snapshot;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by hhlfl on 2017-7-18.
 */
public class NumberUtilTest {
    @Test
    public void divide(){
        double r = NumberUtils.divide(1,3,2);
        Assert.assertEquals(0.33,r,0);
        r = NumberUtils.divide(1l,3l,2);
        Assert.assertEquals(0.33,r,0);
        r = NumberUtils.divide(1l,0l,2);
        Assert.assertEquals(0,r,0);

        r = NumberUtils.divide(1,0,2);
        Assert.assertEquals(0,r,0);
    }

    @Test
    public void keepPrecision(){
        double r = NumberUtils.keepPrecision(1.3456,2);
        Assert.assertEquals(1.35,r,0);
        r = NumberUtils.keepPrecision(1.3456,1);
        Assert.assertEquals(1.3,r,0);
        float r1 = NumberUtils.keepPrecision(1.3456f,2);
        Assert.assertEquals(1.35,r1,0.0001);
        r1 = NumberUtils.keepPrecision(1.3456f,1);
        Assert.assertEquals(1.3f,r1,0.0001);

        String str = NumberUtils.keepPrecision("1.3456",2);
        Assert.assertEquals("1.35",str);

        str = NumberUtils.keepPrecision("1.3456",1);
        Assert.assertEquals("1.3",str);

    }
}
