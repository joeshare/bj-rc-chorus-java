package cn.rongcapital.chorus.modules.email_notify;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Function<T>{
    void prepared(T records, PreparedStatement pst)throws SQLException;
}
