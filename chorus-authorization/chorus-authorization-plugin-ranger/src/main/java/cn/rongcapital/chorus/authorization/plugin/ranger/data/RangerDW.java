package cn.rongcapital.chorus.authorization.plugin.ranger.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by shicheng on 2017/3/22.
 */
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RangerDW extends RangerBase {

    private String databases; // 数据库
    private String tables; // 数据表
    private String columns; // 数据列
    private String tableType; // 数据表类型
    private String columnType; // 数据列类型
    private boolean isEnabled; // 授权是否启用

    public String getDatabases() {
        return databases;
    }

    public void setDatabases(String databases) {
        this.databases = databases;
    }

    public String getTables() {
        return tables;
    }

    public void setTables(String tables) {
        this.tables = tables;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
