package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.TableAuthorityDO;
import cn.rongcapital.chorus.das.entity.TableAuthorityWithTableDO;

import java.util.List;

/**
 * Created by fuxiangli on 2016-11-30.
 */
public interface TableAuthorityService {
    List<TableAuthorityDO> selectByUserId(String userId);

    List<TableAuthorityWithTableDO> selectTableByUserId(String userId);

    List<Long> selectByUserIdAndTableInfoId(String userId, Long tableInfoId);
}
