package cn.rongcapital.chorus.modules.table.stats;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.atlas.AtlasClientV2;
import org.apache.atlas.model.discovery.AtlasSearchResult;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasEntityHeader;
import org.apache.commons.collections.MapUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import cn.rongcapital.chorus.modules.table.stats.bean.HiveTableMonitorData;
import cn.rongcapital.chorus.modules.table.stats.bean.HiveTableParam;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yunzhong
 *
 * @date 2017年8月9日上午11:23:38
 */
@Slf4j
@Component
public class HiveTableStats {
    public static final String TYPES_CHOR_HIVE_TABLE = "chor_hive_table";
    public static final String HIVE_TABLE_MONITOR_PARAM_ROWS = "numRows";
   public static final String DEFAULT_HIVE_TABLE_LOCATION_TEMPLATE = "/chorus/project/%s/hive/%s";

    @Autowired
    @Qualifier("hiveDataSource")
    private DataSource hiveDataSource;

    @Autowired
    @Qualifier("chorusDataSource")
    private DataSource chorusDataSource;

    @Autowired
    private AtlasClientV2 atlasClient;

    private FileSystem fileSystem;

    private Date currentDate;

    public HiveTableStats() throws IOException, InterruptedException {
        initFileSystem();
    }

    public void statistics() throws Exception {
        try {
            log.info("start monitor Hadoop Tables.");
            int pageNum = 1;
            int pageSize = 100;
            currentDate = new Date();
            Set<Long> activeProjects = listProjects();
            if(activeProjects.isEmpty()){
                log.warn("There are no active projects.stop statistics.");
                return;
            }
            while (true) {
                List<HiveTableMonitorData> tables = listAllTables(pageNum, pageSize, activeProjects);
                log.info("get page {} info ,result count {}.", pageNum, tables.size());
                pageNum++;
                if (CollectionUtils.isEmpty(tables)) {
                    log.info("monitor stop at page {}", pageNum);
                    break;
                }
                for (HiveTableMonitorData table : tables) {
                    String hiveDBName = generateHiveDBName((String) table.getProjectCode());
                    assembleRow(table, hiveDBName, table.getTableName());
                    assembleStorage(table, table.getProjectCode(), table.getTableName());
                }
                batchInsert(tables);
            }
            log.info("end monitor hadoop tables.");
        } catch (Exception e) {
            log.error("Failed to monitor hadoop table info.", e);
            throw e;
        }
    }

    public Set<Long> listProjects() throws SQLException {
        log.info("get all active projects");
        String activeProjectSql = "select project_id from project_info where status_code = 1205";

        Set<Long> projectIdSet = SQLUtil.query(chorusDataSource, new ResultSolver<Set<Long>>() {
            @Override
            public Set<Long> result(ResultSet rs) throws SQLException {
                Set<Long> projectIds = new HashSet<>();
                while (rs.next()) {
                    projectIds.add(rs.getLong("project_id"));
                }
                return projectIds;
            }
        }, activeProjectSql);
        log.info("get project id count: {}",projectIdSet.size());
        return projectIdSet;
    }

    private void initFileSystem() throws IOException, InterruptedException {
        log.info("init file system.");
        Configuration conf = new Configuration();
        conf.addResource("hdfs-site.xml");
        this.fileSystem = FileSystem.get(FileSystem.getDefaultUri(conf), conf, "hdfs");
    }

    public String generateHiveDBName(String projectCode) {
        return String.format("chorus_%s", projectCode);
    }

    private int offset(int pageNum, int pageSize) {
        return (pageNum - 1) * pageSize;
    }

    /**
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     * @author yunzhong
     * @time 2017年8月9日下午2:09:29
     */
    public List<HiveTableMonitorData> listAllTables(int pageNum, int pageSize,Set<Long> projectSets) throws Exception {
        log.info("want to get tables page {},count {}", pageNum, pageSize);
        List<HiveTableMonitorData> tables = new LinkedList<>();
        Collection<AtlasEntity> allTableEntities = null;
        String template = "%s where statusCode='1311'";
        String query = String.format(template, TYPES_CHOR_HIVE_TABLE);
        try {
            final AtlasSearchResult atlasSearchResult = atlasClient.dslSearchWithParams(query, pageSize,
                    offset(pageNum, pageSize));
            if (atlasSearchResult != null) {
                final List<AtlasEntityHeader> entities = atlasSearchResult.getEntities();
                if (!CollectionUtils.isEmpty(entities)) {
                    final List<String> guids = entities.parallelStream().map(AtlasEntityHeader::getGuid)
                            .collect(Collectors.toList());
                    allTableEntities = atlasClient.getEntitiesByGuids(guids).getEntities();
                } else {
                    log.info("Entity list is empty with page {}.", pageNum);
                }
            } else {
                log.warn("AtlasSearchResult is null for ");
            }
            if (CollectionUtils.isEmpty(allTableEntities)) {
                log.info("table entity list is empty of page {}", pageNum);
                return new ArrayList<>();
            } else {
                log.info("table entity list's length {} of page {}", allTableEntities.size(), pageNum);
                for (AtlasEntity entity : allTableEntities) {
                    HiveTableMonitorData table = new HiveTableMonitorData();
                    table.setMonitorDate(currentDate);
                    table.setProjectCode(MapUtils.getString(entity.getAttributes(), "project"));
                    table.setProjectId(MapUtils.getLongValue(entity.getAttributes(), "projectId"));
                    table.setTableInfoId(entity.getGuid());
                    table.setTableName(MapUtils.getString(entity.getAttributes(), "name"));
                    if (!projectSets.contains(table.getProjectId())) {
                        log.info("deleted project table {} {}", table.getProjectCode(), table.getTableName());
                        continue;
                    }
                    tables.add(table);
                }
                return tables;
            }
        } catch (Exception e) {
            log.error("Failed to get page [" + pageNum + "] data.", e);
            throw e;
        }
    }

    /**
     * @param monitorInfo
     * @param hiveDBName
     * @param hiveTable
     * @author yunzhong
     * @throws SQLException
     * @time 2017年8月8日上午9:25:31
     */
    public void assembleRow(HiveTableMonitorData monitorInfo, String hiveDBName, String hiveTable) throws SQLException {
        log.info("assemble rows of table {} under db {}", hiveTable, hiveDBName);
        String hiveTableMonitorSql = "SELECT tbp.PARAM_KEY, tbp.PARAM_VALUE from DBS as db "
                + " INNER JOIN TBLS tb ON db.DB_ID = tb.DB_ID AND db.`NAME` = '%s' "
                + "left JOIN TABLE_PARAMS tbp ON tb.TBL_ID = tbp.TBL_ID "
                + "WHERE tb.TBL_NAME = '%s' AND tbp.PARAM_KEY ='numRows'";
        String hivePartitionMonitorSql = "SELECT pp.PARAM_KEY,sum(pp.PARAM_VALUE) as PARAM_VALUE " + "from DBS as db "
                + "INNER JOIN TBLS tb ON db.DB_ID = tb.DB_ID AND db.`NAME` = '%s' "
                + "left JOIN `PARTITIONS` p ON tb.TBL_ID = p.TBL_ID "
                + "LEFT JOIN PARTITION_PARAMS pp ON p.PART_ID = pp.PART_ID "
                + "WHERE tb.TBL_NAME = '%s' AND pp.PARAM_KEY = 'numRows' " + "GROUP BY pp.PARAM_KEY";
        String tableQuery = String.format(hiveTableMonitorSql, hiveDBName, hiveTable);
        String partitionQuery = String.format(hivePartitionMonitorSql, hiveDBName, hiveTable);

        List<HiveTableParam> hiveTableParams = SQLUtil.query(hiveDataSource, new ResultSolver<List<HiveTableParam>>() {
            @Override
            public List<HiveTableParam> result(ResultSet rs) throws SQLException {
                List<HiveTableParam> params = new ArrayList<>();
                while (rs.next()) {
                    HiveTableParam param = new HiveTableParam();
                    param.setParamKey(rs.getString("PARAM_KEY"));
                    param.setParamValue(rs.getString("PARAM_VALUE"));
                    params.add(param);
                }
                return params;
            }
        }, tableQuery);

        if (CollectionUtils.isEmpty(hiveTableParams)) { // 未分区的统计分析没有获得数据，按照分区的方式统计
            hiveTableParams = SQLUtil.query(hiveDataSource, new ResultSolver<List<HiveTableParam>>() {
                @Override
                public List<HiveTableParam> result(ResultSet rs) throws SQLException {
                    List<HiveTableParam> params = new ArrayList<>();
                    while (rs.next()) {
                        HiveTableParam param = new HiveTableParam();
                        param.setParamKey(rs.getString("PARAM_KEY"));
                        param.setParamValue(rs.getString("PARAM_VALUE"));
                        params.add(param);
                    }
                    return params;
                }
            }, partitionQuery);
        }
        for (HiveTableParam param : hiveTableParams) {
            if (HIVE_TABLE_MONITOR_PARAM_ROWS.equals(param.getParamKey())) {
                monitorInfo.setRows(convertToLong(param.getParamValue()));
            }
        }
    }

    private Long convertToLong(String value) {
        if (StringUtils.isEmpty(value)) {
            return 0L;
        }
        return Long.valueOf(value);
    }

    /**
     * @param monitorInfo
     * @param hiveDBName
     * @param hiveTable
     * @author yunzhong
     * @time 2017年8月8日上午9:25:28
     */
    public void assembleStorage(HiveTableMonitorData monitorInfo, String hiveDBName, String hiveTable) {
        log.info("assemble table {} of db {}", hiveTable, hiveDBName);
        long size = 0;
        Path path = new Path(String.format(DEFAULT_HIVE_TABLE_LOCATION_TEMPLATE, hiveDBName, hiveTable));
        try {
            if (fileSystem.exists(path)) {
                size = fileSystem.getContentSummary(path).getLength();
            }
        } catch (IOException e) {
            log.warn("Failed to get path [" + path + "] storage info", e);
        }
        monitorInfo.setStorageSize(size);
    }

    public void batchInsert(Collection<HiveTableMonitorData> tableData) throws Exception {
        log.info("batch insert {}", tableData.size());
        String sql = "replace into table_monitor_v2 (project_id, table_info_id, table_name, monitor_date, rows, storage_size) "
                + "values(?,?,?,?,?,?)";

        try {
            SQLUtil.execute(this.chorusDataSource, sql, new Function<Collection<HiveTableMonitorData>>() {
                @Override
                public void prepared(Collection<HiveTableMonitorData> records, PreparedStatement pst)
                        throws SQLException {
                    int batchSize = 500;
                    int i = 0;
                    for (HiveTableMonitorData record : records) {
                        pst.setLong(1, record.getProjectId());
                        pst.setString(2, record.getTableInfoId());
                        pst.setString(3, record.getTableName());
                        pst.setDate(4, new java.sql.Date(record.getMonitorDate().getTime()));
                        pst.setLong(5, record.getRows());
                        pst.setLong(6, record.getStorageSize());
                        pst.addBatch();
                        if (++i % batchSize == 0)
                            pst.executeBatch();

                    }

                    if (i % batchSize != 0)
                        pst.executeBatch();
                }
            }, tableData);
        } catch (SQLException ex) {
            log.error("Failed to save datas.", ex);
            throw ex;
        }
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

}
