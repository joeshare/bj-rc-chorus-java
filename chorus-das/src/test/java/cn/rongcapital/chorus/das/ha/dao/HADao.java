package cn.rongcapital.chorus.das.ha.dao;

import cn.rongcapital.chorus.das.entity.HostInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface HADao {

    @Insert("Insert into t_user_sub(user_sub_id, user_id) values(#{id},#{id})")
    void insert(int id);

    @Select("SELECT * FROM host_info")
    List<HostInfo> selectAll();
}
