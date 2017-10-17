package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.HostEnv;

public interface HostEnvMapper {
    int deleteByPrimaryKey(Long id);

    int insert(HostEnv record);

    int insertSelective(HostEnv record);

    HostEnv selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(HostEnv record);

    int updateByPrimaryKey(HostEnv record);
}