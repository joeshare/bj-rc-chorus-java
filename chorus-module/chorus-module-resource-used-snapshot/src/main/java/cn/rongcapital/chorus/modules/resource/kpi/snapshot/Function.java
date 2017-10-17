package cn.rongcapital.chorus.modules.resource.kpi.snapshot;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by hhlfl on 2017-7-18.
 */
public interface Function<T>{
    void prepared(T records, PreparedStatement pst)throws SQLException;
}
