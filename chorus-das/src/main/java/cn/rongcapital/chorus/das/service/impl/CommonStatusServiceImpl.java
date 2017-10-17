package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.CommonStatusMapper;
import cn.rongcapital.chorus.das.entity.CommonStatus;
import cn.rongcapital.chorus.das.service.CommonStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by alan on 12/2/16.
 */
@Slf4j
@Service
public class CommonStatusServiceImpl implements CommonStatusService {
    @Autowired
    private CommonStatusMapper commonStatusMapper;

    @Override
    public CommonStatus getById(Long commonStatusId) {
        return commonStatusMapper.selectByPrimaryKey(commonStatusId);
    }
}
