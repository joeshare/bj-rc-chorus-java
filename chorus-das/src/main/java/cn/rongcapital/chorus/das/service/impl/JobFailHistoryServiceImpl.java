package cn.rongcapital.chorus.das.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rongcapital.chorus.das.dao.JobFailHistoryMapper;
import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.das.service.JobFailHistoryService;

@Service
public class JobFailHistoryServiceImpl implements JobFailHistoryService {

    @Autowired
    private JobFailHistoryMapper jobFailHistoryDao;

    @Override
    public Set<String> selectExecutionIds(List<XDExecution> executions) {
        return jobFailHistoryDao.selectExecutionIds(executions);
    }

    @Override
    public int insert(XDExecution xdExecution) {
        return jobFailHistoryDao.insert(xdExecution);
    }
}
