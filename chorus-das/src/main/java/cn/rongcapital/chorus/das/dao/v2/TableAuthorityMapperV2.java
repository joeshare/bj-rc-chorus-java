package cn.rongcapital.chorus.das.dao.v2;

import cn.rongcapital.chorus.das.entity.TableAuthorityV2;

public interface TableAuthorityMapperV2 {
    int deleteByPrimaryKey(Long tableAuthorityId);

    int insert(TableAuthorityV2 record);

    int insertSelective(TableAuthorityV2 record);

    TableAuthorityV2 selectByPrimaryKey(Long tableAuthorityId);

    int updateByPrimaryKeySelective(TableAuthorityV2 record);

    int updateByPrimaryKey(TableAuthorityV2 record);


}
