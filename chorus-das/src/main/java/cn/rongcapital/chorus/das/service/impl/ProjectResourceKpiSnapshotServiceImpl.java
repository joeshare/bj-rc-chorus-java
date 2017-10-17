package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.util.DateUtils;
import cn.rongcapital.chorus.common.util.NumUtils;
import cn.rongcapital.chorus.das.dao.*;
import cn.rongcapital.chorus.das.entity.ProjectResourceKpiSnapshotVO;
import cn.rongcapital.chorus.das.entity.XDTaskNum;
import cn.rongcapital.chorus.das.service.ProjectResourceKpiSnapshotService;
import cn.rongcapital.chorus.das.xd.dao.XDMapper;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Athletics on 2017/7/18.
 */
@Component
@Slf4j
@Service("ProjectResourceKpiSnapshotService")
public class ProjectResourceKpiSnapshotServiceImpl implements ProjectResourceKpiSnapshotService{

    @Autowired
    private ProjectResourceKpiSnapshotMapper projectResourceKpiSnapshotMapper;
    @Autowired
    private ProjectInfoMapper projectInfoMapper;
    @Autowired
    private ResourceInnerDOMapper resourceInnerDOMapper;
    @Autowired
    private XDMapper xdMapper;
    @Autowired
    private ProjectResourceKpiSnapshotDOMapper projectResourceKpiSnapshotDOMapper;


    @Override
    public Map<String, Long> queryUseRate(){
        Map<String,Long> resultMap = null;
        Map<String, Object> map = resourceInnerDOMapper.queryPlatformResource(StatusCode.RESOURCE_APPROVE.getCode());
        if(map != null){
            resultMap = new HashMap<>();
            for(Map.Entry<String, Object> entry:map.entrySet()){
                resultMap.put(entry.getKey(),((BigDecimal)entry.getValue()).longValue());
            }
        }

        return resultMap;
    }

    @Override
    public Integer getTotalProjectNum() {
        return projectInfoMapper.queryCountNum();
    }

    @Override
    public Long getPlatformDataDailyIncr() {
        return projectResourceKpiSnapshotMapper.queryDataDailyIncrTotalNumByDate(DateUtils.getIntervalDay(new Date(), -1));
    }

    @Override
    public String getTaskSuccessRate() {
        XDTaskNum taskNum = xdMapper.getPlatformTaskSuccessNum();
        return taskNum == null ? "0" : NumUtils.percent(taskNum.getSuccessCount().longValue(),taskNum.getTotal().longValue());
    }

    @Override
    public List<Map<String ,Object>> getTrend() {
        return projectResourceKpiSnapshotMapper.getTrend(DateUtils.getIntervalDay(new Date(), -30));
    }

    @Override
    public List<ProjectResourceKpiSnapshotVO> selectByKpiDate(Date kpiDate, int pageNum, int pageSize) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        return projectResourceKpiSnapshotDOMapper.selectByKpiDate(kpiDate);

    }

    @Override
    public List<ProjectResourceKpiSnapshotVO> selectByKpiDateWithOrder(Map<String,String> paramMap, int pageNum, int pageSize) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        return projectResourceKpiSnapshotDOMapper.selectByKpiDateWithOrder(paramMap);

    }
}
