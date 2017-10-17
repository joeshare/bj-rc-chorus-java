package cn.rongcapital.chorus.das.hadoop;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.rongcapital.chorus.common.hadoop.HadoopClient;
import cn.rongcapital.chorus.das.util.BasicSpringTest;

public class HadoopClientTest extends BasicSpringTest{

    @Autowired
    private HadoopClient hadoopClient;
    
    /**
     * 
     * @author yunzhong
     * @time 2017年6月14日上午11:28:30
     */
    @Test
    public void testMkdir(){
        boolean mkdir = hadoopClient.mkdir("/chorus/project/yunzhongcheck2", "wangyunzhong");
        assertTrue(mkdir);
    }
    
    /**
     * 
     * @author yunzhong
     * @time 2017年6月14日上午11:28:28
     */
    @Test
    public void testSetQuota() {
        boolean result = hadoopClient.setSpaceQuota("/chorus/project/yunzhongcheck1", 10, "G");
        assertTrue(result);
    }
}
