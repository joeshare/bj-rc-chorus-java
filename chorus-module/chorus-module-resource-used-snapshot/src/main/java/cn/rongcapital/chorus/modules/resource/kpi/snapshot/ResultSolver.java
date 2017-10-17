package cn.rongcapital.chorus.modules.resource.kpi.snapshot;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by hhlfl on 2017-7-17.
 */
public interface ResultSolver<T> {
    public T result(ResultSet rs) throws SQLException;
}
