package cn.rongcapital.chorus.modules.email_notify;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSolver<T> {
    public T result(ResultSet rs) throws SQLException;
}
