package cn.rongcapital.chorus.modules.table.stats;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Function<T>{
    void prepared(T records, PreparedStatement pst)throws SQLException;
}
