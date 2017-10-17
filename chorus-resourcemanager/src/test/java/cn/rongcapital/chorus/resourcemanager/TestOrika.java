package cn.rongcapital.chorus.resourcemanager;

import cn.rongcapital.chorus.common.util.OrikaBeanMapper;
import cn.rongcapital.chorus.das.entity.HostInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by abiton on 05/12/2016.
 */
public class TestOrika {

    public static void main(String[] args) {

        List<HostInfo> origin = new ArrayList<>();
        HostInfo info1 = new HostInfo();
        info1.setCpu(1);
        info1.setCreateTime(new Date());
        info1.setHostName("info1");
        info1.setHostId(1l);
         HostInfo info2 = new HostInfo();
        info2.setCpu(1);
        info2.setCreateTime(new Date());
        info2.setHostName("info1");
        info2.setHostId(1l);
         HostInfo info3 = new HostInfo();
        info3.setCpu(1);
        info3.setCreateTime(new Date());
        info3.setHostName("info1");
        info3.setHostId(1l);
        origin.add(info1);
        origin.add(info2);
        origin.add(info3);
        List<String> sources = OrikaBeanMapper.INSTANCE.mapAsList(origin, String.class);
        sources.forEach(s ->
                System.out.println(s)
        );
    }
}
