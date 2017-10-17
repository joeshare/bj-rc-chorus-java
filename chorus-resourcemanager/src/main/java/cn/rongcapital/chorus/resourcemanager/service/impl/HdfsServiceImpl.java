package cn.rongcapital.chorus.resourcemanager.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.resourcemanager.service.HdfsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by abiton on 12/01/2017.
 */
@Slf4j
@Service("hdfsService")
public class HdfsServiceImpl implements HdfsService {

    @Autowired
    private FileSystem fileSystem;
    @Override
    public int getTotalCapacity() {
        try {
            return (int) (fileSystem.getStatus().getCapacity()/1024/1024/1024);
        } catch (IOException e) {
            log.error("get capacity from hdfs error ",e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        }

    }
}
