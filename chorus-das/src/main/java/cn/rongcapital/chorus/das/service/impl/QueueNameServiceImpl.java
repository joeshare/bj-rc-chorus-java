package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.service.QueueNameService;
import org.springframework.stereotype.Service;

/**
 * Created by abiton on 10/04/2017.
 */
@Service("QueueNameService")
public class QueueNameServiceImpl implements QueueNameService{
    @Override
    public String projectQueue(String projectCode) {
        if (StringUtils.isEmpty(projectCode)){
            throw new ServiceException(StatusCode.PARAM_ERROR);
        }
        return projectCode;
    }
}
