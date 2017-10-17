package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.ProjectResourceKpiSnapshotVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Athletics on 2017/7/18.
 */
public interface ProjectResourceKpiSnapshotService {

    /**
     * 查询平台项目已申请总资源
     * @return
     */
    Map<String, Long> queryUseRate();

    /**
     * 查询平台项目总数
     * @return
     */
    Integer getTotalProjectNum();

    /**
     * 查询平台日增数据量
     * @return
     */
    Long getPlatformDataDailyIncr();

    /**
     * 查询XD平台任务成功率
     * @return
     */
    String getTaskSuccessRate();

    /**
     * 获取平台30天内的走势
     */
    List<Map<String ,Object>> getTrend();

    List<ProjectResourceKpiSnapshotVO> selectByKpiDate(Date kpiDate, int pageNum, int pageSize)throws Exception;

    List<ProjectResourceKpiSnapshotVO> selectByKpiDateWithOrder(Map<String,String> paramMap, int pageNum, int pageSize)throws Exception;
}
