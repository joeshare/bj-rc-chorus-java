package cn.rongcapital.chorus.das.ha;

import cn.rongcapital.chorus.das.entity.HostInfo;
import cn.rongcapital.chorus.das.ha.dao.HADao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HaServiceImpl implements HaService {

    @Autowired
    private HADao haDao;

    @Override
    @Transactional
    public void insert(int i) {
        haDao.insert(i);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HostInfo> select() {
        return haDao.selectAll();
    }

}


