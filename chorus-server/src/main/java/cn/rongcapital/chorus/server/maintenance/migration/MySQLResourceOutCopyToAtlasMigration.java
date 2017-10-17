package cn.rongcapital.chorus.server.maintenance.migration;

import cn.rongcapital.chorus.das.entity.ResourceOut;
import cn.rongcapital.chorus.das.service.ResourceOutService;
import cn.rongcapital.chorus.server.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yimin
 */
@Slf4j
@RestController
@RequestMapping(value = "/maintenance/migration")
public class MySQLResourceOutCopyToAtlasMigration {

    @Autowired
    private ResourceOutService resourceOutService;
    private final int MYSQL_STORAGE_TYPE = 1;


    @RequestMapping(value = "/mysql-to-atlas")
    public ResultVO<List<ResourceOut>> mysqlTypeToAtlas() {
        final List<ResourceOut> unSyncedMySQLResource = resourceOutService.getUnSyncedMySQLResource(MYSQL_STORAGE_TYPE);
        if (CollectionUtils.isNotEmpty(unSyncedMySQLResource)) {
            final List<ResourceOut> synced = unSyncedMySQLResource.stream()
                                                                  .filter(Objects::nonNull)
                                                                  .map(resourceOut -> resourceOutService.syncToMetaDataCenter(resourceOut))
                                                                  .collect(Collectors.toList());
            return ResultVO.success(synced);
        }
        return ResultVO.success();
    }

}
