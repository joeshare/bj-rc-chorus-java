package cn.rongcapital.chorus.common.xd.service.impl;

import cn.rongcapital.chorus.common.xd.XDClient;
import cn.rongcapital.chorus.common.xd.service.DslGraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author li.hzh
 * @date 2016-11-15 15:20
 */
@Service
public class DslGraphServiceImpl implements DslGraphService {

    private static Logger logger = LoggerFactory.getLogger(DslGraphServiceImpl.class);

    @Autowired
    private XDClient xdClient;

    @Override
    public String fromDSLToGraph(String graphText) {
        logger.debug("From D to G, Input DSL Text: {}.", graphText);
        return xdClient.dslToGraph(graphText);
    }

    @Override
    public String fromGraphToDSL(String dslText) {
        logger.debug("From G to D, Input Graph Text: {}.", dslText);
        return xdClient.graphToDsl(dslText);
    }

}
