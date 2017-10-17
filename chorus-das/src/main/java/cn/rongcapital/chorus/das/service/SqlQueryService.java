package cn.rongcapital.chorus.das.service;

import java.util.LinkedHashMap;
import java.util.List;

import cn.rongcapital.chorus.das.entity.CalculateSQL;
import cn.rongcapital.chorus.das.entity.ExecuteHistory;
import cn.rongcapital.chorus.das.entity.web.ExecuteHistoryCause;

public interface SqlQueryService {

	/**
	 * 最大查询数量
	 */
	int QUERY_LIMIT_DEFAULT = 100;
	int QUERY_LIMIT_MAX   = 1000;

	/**
	 * 语句执行
	 * @param calculateSQL
	 * @param userId
	 * @return
	 */
	List<LinkedHashMap<String, String>> calculate(CalculateSQL calculateSQL, String userId) throws Exception;
	
    /**
     * 根据条件查询数据条数
	 * @param cause
     */
    int count(ExecuteHistoryCause cause);
    
    /**
     * 获取执行历史信息
     *
     * @param cause
     * @return 执行历史列表
     */
    List<ExecuteHistory> list(ExecuteHistoryCause cause);
}
