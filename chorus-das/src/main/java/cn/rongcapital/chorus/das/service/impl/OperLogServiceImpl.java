package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.dao.OperLogMapper;
import cn.rongcapital.chorus.das.service.OperLogService;
import cn.rongcapital.chorus.das.entity.OperLog;
import cn.rongcapital.chorus.das.entity.web.CommonCause;
import cn.rongcapital.chorus.das.entity.web.OperLogCause;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service(value = "OperLogService")
public class OperLogServiceImpl implements OperLogService {
	@Autowired(required = false)
	private OperLogMapper mapper;


    @Override
    public List<OperLog> getLogList(String id, int pageindex) {
    	OperLogCause logCause = new OperLogCause();
		logCause.setRecordKey(id);
		logCause.setPage(pageindex);
        return mapper.list(logCause);
    }

    @Override
    public void save(String fromTable, String operLogParam, String recordKey, String userId) {
        OperLog operLog = new OperLog();
        operLog.setOperId(UUID.randomUUID().toString());
        operLog.setFromTable(fromTable);
        operLog.setOperLog(operLogParam);
        //若
        if(StringUtils.isBlank(userId)){
        	operLog.setOperUserId("-1");
        } else {
        	 operLog.setOperUserId(userId);
        }
        Date date = new Date();
        operLog.setCreateTime(date);
        operLog.setUpdateTime(date);
        operLog.setRecordKey(recordKey);
        mapper.insert(operLog);
    }

    @Override
    public void saveWithoutSession(String fromTable, String operLogParam, String recordKey) {
        OperLog operLog = new OperLog();
        operLog.setOperId(UUID.randomUUID().toString());
        operLog.setFromTable(fromTable);
        operLog.setOperLog(operLogParam);
        operLog.setOperUserId("-1");
        Date date = new Date();
        operLog.setCreateTime(date);
        operLog.setUpdateTime(date);
        operLog.setRecordKey(recordKey);
        mapper.insert(operLog);
    }
    @Override
    public void saveSessionOrNot(String fromTable, String operLogParam, String recordKey, String userId) {
        OperLog operLog = new OperLog();
        operLog.setOperId(UUID.randomUUID().toString());
        operLog.setFromTable(fromTable);
        operLog.setOperLog(operLogParam);
        //若无session状态则设置-1
        if(StringUtils.isBlank(userId)){
            operLog.setOperUserId("-1");
        } else {
            operLog.setOperUserId(userId);
        }
        Date date = new Date();
        operLog.setCreateTime(date);
        operLog.setUpdateTime(date);
        operLog.setRecordKey(recordKey);
        mapper.insert(operLog);
    }
    @Override
	public int getLogCount(String id) {
		OperLogCause logCause = new OperLogCause();
		logCause.setRecordKey(id);
		int count = mapper.count(logCause);
		if (count % CommonCause.getRows() > 0) {
			return (count / CommonCause.getRows()) + 1;
		} else {
			return count / CommonCause.getRows();
		}
	}

	@Override
	public int getLogCount(OperLogCause logCause) {
		return mapper.count(logCause);
	}

	@Override
	public List<OperLog> getLogList(OperLogCause logCause) {
		return mapper.list(logCause);
	}

}
