package cn.rongcapital.chorus.das.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.rongcapital.chorus.common.util.CollectionUtils;
import cn.rongcapital.chorus.das.dao.JobExecStatisticMapper;
import cn.rongcapital.chorus.das.dao.ProjectInfoMapper;
import cn.rongcapital.chorus.das.entity.JobExecStatistic;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.service.JobExecStatisticService;

/**
 * 任务执行统计Service实现类
 * @author kevin.gong
 * @Time 2017年6月20日 上午10:14:05
 */
@Service(value = "JobExecStatisticService")
@Transactional
public class JobExecStatisticServiceImpl implements JobExecStatisticService {
    private static Logger logger = LoggerFactory.getLogger(JobExecStatisticServiceImpl.class);
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired(required = false)
    private JobExecStatisticMapper jobExecStatisticMapper;
    
    @Autowired(required = false)
    private ProjectInfoMapper projectInfoMapper;
    
    @Override
    public List<Map<String, Object>> getJobExecDist(long projectId, int recentDayNum) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -recentDayNum);
        List<Map<String, Object>> list = jobExecStatisticMapper.getJobExecDist(projectId, df.format(cal.getTime()));
        if(CollectionUtils.isNotEmpty(list)){
            //数据数量等于天数，不需要补数据
            if(list.size() == recentDayNum){
                return list;
            } 
            
            //数据数量不等于天数，可能需要补数据（项目创建之前的数据不需要补）。
            //项目创建日期
            String proCDate = getProjectCreateDate(projectId);
            int flag = 0;
            for (int i = 0; i < recentDayNum; i++) {
                //应插入数据日期
                String date = df.format(cal.getTime());
                cal.add(Calendar.DATE, 1);
                //遍历到数据日期(后几天没取到数据的情况，直接置为null)
                String dbdDate = (list.size()>flag?df.format(list.get(flag).get("date")):null);
                
                if(StringUtils.isEmpty(proCDate) || proCDate.compareTo(date) > 0) {
                    continue;
                }
                
                if(!date.equals(dbdDate)) {
                    if(dbdDate != null) {
                        list.add(flag, getNoCountStatisticInfo(date));
                    } else {
                        list.add(getNoCountStatisticInfo(date));
                    }
                }
                flag ++;
            }
        } else {
            //未取到数据，填充这几天数据（创建时间之后）。
            list = new ArrayList<>();
            String proCDate = getProjectCreateDate(projectId);
            for (int i = 0; i < recentDayNum; i++) {
                String date = df.format(cal.getTime());
                if(StringUtils.isNotEmpty(proCDate) && proCDate.compareTo(date)<=0) {
                    list.add(getNoCountStatisticInfo(date));
                }
                cal.add(Calendar.DATE, 1);
            }
        }
        return list;
    }
    
    /**
     * 无数据日期信息填充
     * @param date 日期
     * @return 当日无状态发生信息
     */
    private Map<String, Object> getNoCountStatisticInfo(String date) {
        Map<String, Object> map = new HashMap<>();
        map.put("date", date);
        map.put("completedNum", 0);
        map.put("failedNum", 0);
        map.put("runningNum", 0);
        map.put("runNumAtStatTime", 0);
        return map;
    }
    
    /**
     * 获取项目创建时间
     * @param projectId 项目编号
     * @return 项目创建时间
     */
    private String getProjectCreateDate(long projectId){
        ProjectInfo pro = projectInfoMapper.selectByPrimaryKey(projectId);
        if (pro != null && pro.getCreateTime() != null) {
            return df.format(pro.getCreateTime());
        } else {
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> getLongestExecTimeJob(long projectId, int size) {
        return jobExecStatisticMapper.getLongestExecTimeJob(projectId, size);
    }
    
    @Override
    public boolean addBatchJobExecStatistic(JobExecStatistic jobExecStatistic) {
        return jobExecStatisticMapper.add(jobExecStatistic) > 0;
    }

    @Override
    public boolean updateDuration(JobExecStatistic jobExecStatistic) {
        return jobExecStatisticMapper.updateDuration(jobExecStatistic) > 0;
    }

}
