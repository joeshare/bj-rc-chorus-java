package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.DatalabInfoDOMapper;
import cn.rongcapital.chorus.das.dao.DatalabInfoMapper;
import cn.rongcapital.chorus.das.entity.DatalabInfo;
import cn.rongcapital.chorus.das.service.DatalabInfoService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by abiton on 16/03/2017.
 */
@Service("DatalabInfoService")
public class DatalabInfoServiceImpl implements DatalabInfoService {
    @Autowired
    private DatalabInfoMapper mapper;
    @Autowired
    private DatalabInfoDOMapper infoDOMapper;

    @Override
    public void create(DatalabInfo lab) {
        lab.setCreateTime(new Date());
        mapper.insertSelective(lab);
    }

    @Override
    public void delete(String projectCode, String labCode) {
        infoDOMapper.deleteByProjectCodeAndLabCode(projectCode, labCode);
    }

    @Override
    public DatalabInfo get(String projectCode, String labCode) {
        return infoDOMapper.query(projectCode, labCode);
    }

    @Override
    public List<DatalabInfo> get(String projectCode) {
        return infoDOMapper.queryByProjectCode(projectCode);
    }

    @Override
    public List<DatalabInfo> get(String projectCode, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        return infoDOMapper.queryByProjectCode(projectCode);
    }

}
