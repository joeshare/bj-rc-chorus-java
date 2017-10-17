package cn.rongcapital.chorus.das.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rongcapital.chorus.common.hadoop.HadoopClient;
import cn.rongcapital.chorus.das.service.HdfsSnapshotService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lovett
 */
@Slf4j
@Service
public class HdfsSnapshotServiceImpl implements HdfsSnapshotService {
    @Autowired
    private HadoopClient hadoopClient;

    @Override
    public Boolean allowSnapshot(String dir) {
        return hadoopClient.allowSnapshot(dir);
    }

}
