package cn.rongcapital.chorus.modules.resource.kpi.snapshot;

import cn.rongcapital.chorus.modules.resource.kpi.snapshot.bean.ProjectResourceKPISnapshot;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hhlfl on 2017-7-14.
 */
@Slf4j
@Component
public class ProjectResourceKPIStats {
    @Autowired
    private DataSource xdDataSource;
    @Autowired
    private DataSource chorusDataSource;

    public void snapshot()throws Exception{
        List<Map<String, Object>> projects = queryProjects();
        //key:projectId, value:ProjectResourceKPISnapshot
        Map<Long, ProjectResourceKPISnapshot> snapshotMap = new HashMap<>();
        //key:projectCode, value:projecctId
        Map<String, Long> projectCode2IdMap = new HashMap<>();
        for(Map<String, Object> project : projects){
            Long projectId = (Long)project.get("projectId");
            String projectCode = (String)project.get("projectCode");
            String projectName = (String)project.get("projectName");
            ProjectResourceKPISnapshot projectResourceKPISnapshot = new ProjectResourceKPISnapshot();
            init(projectResourceKPISnapshot);
            projectResourceKPISnapshot.setProjectId(projectId);
            projectResourceKPISnapshot.setProjectName(projectName);
            snapshotMap.put(projectId, projectResourceKPISnapshot);
            projectCode2IdMap.put(projectCode,projectId);
        }

        //统计cpu,memory,相关指标
        List<ProjectResourceKPISnapshot> cpuAndMemoryRecords = statsCpuAndMemory();
        for(ProjectResourceKPISnapshot cpuAndMemoryUsage : cpuAndMemoryRecords){
            ProjectResourceKPISnapshot usage = snapshotMap.get(cpuAndMemoryUsage.getProjectId());
            if(usage == null)
                continue;

            usage.setCpuTotal(cpuAndMemoryUsage.getCpuTotal());
            usage.setCpuUsed(cpuAndMemoryUsage.getCpuUsed());
            usage.setCpuUsage(cpuAndMemoryUsage.getCpuUsage());
            usage.setMemoryTotal(cpuAndMemoryUsage.getMemoryTotal());
            usage.setMemoryUsed(cpuAndMemoryUsage.getMemoryUsed());
            usage.setMemoryUsage(cpuAndMemoryUsage.getMemoryUsage());
            usage.setStorageTotal(cpuAndMemoryUsage.getStorageTotal());
        }

        //key:projectCode,value: 数据总量
        Map<String,Long> storageMap = statsStorage(projectCode2IdMap.keySet());
        for(String projectCode : storageMap.keySet()){
            long projectId = projectCode2IdMap.get(projectCode);
            ProjectResourceKPISnapshot usage = snapshotMap.get(projectId);
            if(usage == null)
               continue;

            long usedStorage = storageMap.get(projectCode);
            long totalStorage = usage.getStorageTotal();
            double storageUsage = NumberUtils.divide(usedStorage, totalStorage,Constant.precision);
            usage.setStorageUsage(storageUsage);
            usage.setStorageUsed(usedStorage);
        }

        //计算增量数据，从昨天的快照表中获取
        Map<Long, ProjectResourceKPISnapshot> yesterdaySnapshot = new HashMap<>();
        // latest kpi_date not include current date.
        Date latestKPIDate = queryLatestKPIDate();
        if(latestKPIDate != null) yesterdaySnapshot = querySnapshot(latestKPIDate);
        for(Long projectId: snapshotMap.keySet()){
            ProjectResourceKPISnapshot usage = snapshotMap.get(projectId);
            ProjectResourceKPISnapshot yestUsage = yesterdaySnapshot.get(projectId);
            long incr = usage.getStorageUsed();
            if(yestUsage != null)
                incr = incr - yestUsage.getStorageUsed();

            usage.setDataDailyIncr(incr);
        }
        yesterdaySnapshot.clear();

        //key:job_name, value:projectId
        Date currentDate = new Date();
        Map<Long, List<String>> projectJobMap = queryJobs(currentDate);
        Map<String,int[]> jobSuccessMap = statsJobSuccessRato(currentDate);
        for(Long projectId: snapshotMap.keySet()){
            int total = 0;
            int success = 0;
            if(projectJobMap.containsKey(projectId)){
                List<String> jobNames = projectJobMap.get(projectId);
                for(String jobName : jobNames){
                    int[] countArr = jobSuccessMap.get(jobName);
                    if(countArr != null){
                        total += countArr[1];
                        success += countArr[0];
                    }
                }
            }

            double successRato = 1.0;
            if(total >0)
                successRato=NumberUtils.divide(success,total,Constant.precision);
            ProjectResourceKPISnapshot usage = snapshotMap.get(projectId);
            usage.setTaskTotal(total);
            usage.setTaskSuccess(success);
            usage.setTaskSuccessRate(successRato);
        }
        projectJobMap.clear();
        jobSuccessMap.clear();

        //保存快照
        saveSnapshot(snapshotMap.values());

        storageMap.clear();
        cpuAndMemoryRecords.clear();
        snapshotMap.clear();
        projectCode2IdMap.clear();
    }

    private void init(ProjectResourceKPISnapshot usage){
        if(usage == null)
            return ;

        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        usage.setCpuUsed(0);
        usage.setCpuTotal(0);
        usage.setCpuUsage(0.0);
        usage.setTaskTotal(0);
        usage.setTaskSuccess(0);
        usage.setTaskSuccessRate(100);
        usage.setCreateTime(new Timestamp(date.getTime()));
        usage.setKpiDate(date);
        usage.setMemoryTotal(0);
        usage.setMemoryUsed(0);
        usage.setMemoryUsage(0.0);
        usage.setStorageTotal(0l);
        usage.setStorageUsed(0l);
        usage.setStorageUsage(0.0);
        usage.setDataDailyIncr(0);
    }

    public void saveSnapshot(Collection<ProjectResourceKPISnapshot> projectResourceKPISnapshots) throws Exception{
        String sql = "replace into project_resource_kpi_snapshot (project_id, project_name,kpi_date," +
                "cpu_total,cpu_used,cpu_usage," +
                "memory_total,memory_used,memory_usage,storage_total,storage_used,storage_usage,data_daily_incr,task_total," +
                "task_success,task_success_rate,score,create_time)" +
                "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try{
            SQLUtil.execute(this.chorusDataSource, sql, new Function<Collection<ProjectResourceKPISnapshot>>() {
                @Override
                public void prepared(Collection<ProjectResourceKPISnapshot> records, PreparedStatement pst) throws SQLException {
                    int batchSize = 5000;
                    int i=0;
                    for(ProjectResourceKPISnapshot record : records){
                        pst.setLong(1,record.getProjectId());
                        pst.setString(2,record.getProjectName());
                        pst.setDate(3,record.getKpiDate());
                        pst.setInt(4,record.getCpuTotal());
                        pst.setInt(5,record.getCpuUsed());
                        pst.setDouble(6,record.getCpuUsage());
                        pst.setInt(7,record.getMemoryTotal());
                        pst.setInt(8,record.getMemoryUsed());
                        pst.setDouble(9,record.getMemoryUsage());
                        pst.setLong(10,record.getStorageTotal());
                        pst.setLong(11,record.getStorageUsed());
                        pst.setDouble(12,record.getStorageUsage());
                        pst.setLong(13,record.getDataDailyIncr());
                        pst.setInt(14,record.getTaskTotal());
                        pst.setInt(15, record.getTaskSuccess());
                        pst.setDouble(16, record.getTaskSuccessRate());
                        pst.setInt(17, record.getScore());
                        pst.setTimestamp(18, record.getCreateTime());
                        pst.addBatch();
                        if(++i%batchSize == 0)
                            pst.executeBatch();

                    }

                    if(i%batchSize != 0)
                        pst.executeBatch();
                }
            }, projectResourceKPISnapshots);
        }catch (SQLException ex){
            log.error("save snapshot exception", ex);
            throw ex;
        }
    }

    public Map<Long, List<String>> queryJobs(Date date) throws Exception{
        String sql = "select project_id as projectId, job_name as jobName from job " +
                " where status='DEPLOY' or (update_time>='%s')";
        sql = String.format(sql, format(date,"yyyy-MM-dd"));
        try{
            if(log.isDebugEnabled())
                log.debug(String.format(" query jobs SQL:%s",sql));

            return SQLUtil.query(this.chorusDataSource, new ResultSolver<Map<Long, List<String>>>() {
                @Override
                public Map<Long, List<String>> result(ResultSet rs) throws SQLException {
                    Map<Long, List<String>> resultMap = new HashMap<>();
                    while(rs.next()){
                        long projectId = rs.getLong("projectId");
                        List<String> jobNames = resultMap.get(projectId);
                        if(jobNames == null){
                            jobNames = new ArrayList<>();
                            resultMap.put(projectId, jobNames);
                        }

                        jobNames.add(rs.getString("jobName"));
                    }

                    return resultMap;
                }
            },sql);
        }catch (SQLException ex){
            log.error("query jobName Exception.", ex);
            throw ex;
        }
    }

    public Map<Long, ProjectResourceKPISnapshot> querySnapshot(Date kpiDate) throws Exception{
        String sql = "select id, project_id , project_name,kpi_date," +
                "cpu_total,cpu_used,cpu_usage," +
                "memory_total,memory_used,memory_usage,storage_total,storage_used,storage_usage,data_daily_incr,task_total," +
                "task_success,task_success_rate,score,create_time from project_resource_kpi_snapshot" +
                " where kpi_date='%s'";
        try{
            sql = String.format(sql, format(kpiDate,"yyyy-MM-dd"));
            if(log.isDebugEnabled())
                log.debug(String.format("query snapshot SQL:%s",sql));

            return SQLUtil.query(this.chorusDataSource, new ResultSolver<Map<Long, ProjectResourceKPISnapshot>>() {
                @Override
                public Map<Long, ProjectResourceKPISnapshot> result(ResultSet rs) throws SQLException {
                    Map<Long, ProjectResourceKPISnapshot> usages = new HashMap<>();
                    while(rs.next()){
                        ProjectResourceKPISnapshot usage = new ProjectResourceKPISnapshot();
                        usage.setId(rs.getLong("id"));
                        usage.setProjectId(rs.getLong("project_id"));
                        usage.setProjectName(rs.getString("project_name"));
                        usage.setCpuUsed(rs.getInt("cpu_used"));
                        usage.setCpuTotal(rs.getInt("cpu_total"));
                        usage.setCpuUsage(rs.getDouble("cpu_usage"));
                        usage.setTaskTotal(rs.getInt("task_total"));
                        usage.setTaskSuccess(rs.getInt("task_success"));
                        usage.setTaskSuccessRate(rs.getDouble("task_success_rate"));
                        usage.setCreateTime(rs.getTimestamp("create_time"));
                        usage.setKpiDate(rs.getDate("kpi_date"));
                        usage.setMemoryTotal(rs.getInt("memory_total"));
                        usage.setMemoryUsed(rs.getInt("memory_used"));
                        usage.setMemoryUsage(rs.getDouble("memory_usage"));
                        usage.setStorageTotal(rs.getLong("storage_total"));
                        usage.setStorageUsed(rs.getLong("storage_used"));
                        usage.setStorageUsage(rs.getDouble("storage_usage"));
                        usage.setDataDailyIncr(rs.getLong("data_daily_incr"));
                        usage.setScore(rs.getInt("score"));
                        usages.put(usage.getProjectId(), usage);
                    }

                    return usages;
                }
            }, sql);
        }catch (SQLException ex){
            log.error("query table project_resource_kpi_snapshot Exception.");
            throw ex;
        }

    }

    private Date queryLatestKPIDate() throws SQLException{
        String sql = "select max(kpi_date) as latestDate from project_resource_kpi_snapshot where kpi_date<'%s'";
        sql = String.format(sql, format(new Date(),"yyyy-MM-dd"));
        if(log.isDebugEnabled())
            log.debug(String.format("query latest kpi_date SQL:%s", sql));

        return SQLUtil.query(this.chorusDataSource, new ResultSolver<Date>() {
            @Override
            public Date result(ResultSet rs) throws SQLException {
                if(rs.next()){
                    return rs.getDate("latestDate");
                }

                return null;
            }
        }, sql);
    }

    public List<Map<String, Object>> queryProjects() throws Exception{
        String sql = "select project_id as projectId, project_code as projectCode, project_name as projectName from project_info ";
        try{
            if(log.isDebugEnabled())
                log.debug(String.format("query all project SQL:%s", sql));

            return SQLUtil.query(this.chorusDataSource, new ResultSolver<List<Map<String, Object>>>() {
                @Override
                public List<Map<String, Object>> result(ResultSet rs) throws SQLException {
                    List<Map<String,Object>> records = new ArrayList<>();
                    while(rs.next()){
                        Map<String,Object> project = new HashMap<String, Object>();
                        project.put("projectId",rs.getLong("projectId"));
                        project.put("projectCode",rs.getString("projectCode"));
                        project.put("projectName", rs.getString("projectName"));
                        records.add(project);
                    }

                    return records;
                }
            }, sql);

        }catch (SQLException ex){
            log.error("query all project exception.");
            throw ex;
        }
    }


    /***
     *
     *
     * @return
     */
    public List<ProjectResourceKPISnapshot> statsCpuAndMemory() throws Exception{
        //stats sql of cpu and memory.
        String sql = "select t2.project_id as projectId,t2.resource_cpu as resourceCPU, t2.resource_memory as resourceMemory, t2.resource_storage as resourceStorage," +
                "    sum(t4.usedCPU) as usedCPU, sum(t4.usedMemory) as usedMemory" +
                "    from resource_inner t2 left outer join " +
                "    " +
                "    (select t1.resource_inner_id, t1.instance_size*t3.resource_cpu as usedCPU, t1.instance_size*t3.resource_memory as usedMemory from instance_info t1" +
                "    inner join resource_template t3" +
                "    on t3.resource_template_id=t1.resource_template_id" +
                "    where" +
                "    t1.status_code in(2101,2102,2103)" +
                "    ) as t4" +
                "    on t2.resource_inner_id=t4.resource_inner_id" +
                "    where" +
                "    t2.status_code in(2003)" +
                "    group by t2.project_id, t2.resource_cpu, t2.resource_memory , t2.resource_storage" +
                "    order by t2.project_id";

        try{
            if(log.isDebugEnabled())
                log.debug(String.format("stats cpu and memory SQL:%s",sql));

            return SQLUtil.query(this.chorusDataSource, new ResultSolver<List<ProjectResourceKPISnapshot>>() {
                @Override
                public List<ProjectResourceKPISnapshot> result(ResultSet rs) throws  SQLException {
                    List<ProjectResourceKPISnapshot> resultList = new ArrayList<ProjectResourceKPISnapshot>();
                    while(rs.next()){
                        ProjectResourceKPISnapshot usage = new ProjectResourceKPISnapshot();
                        long projectId = rs.getLong("projectId");
                        int totalCPU = rs.getInt("resourceCPU");
                        int usedCPU = rs.getInt("usedCPU");
                        int totalMem = rs.getInt("resourceMemory");
                        int usedMem = rs.getInt("usedMemory");
                        int storage = rs.getInt("resourceStorage");
                        double cpuUsage = NumberUtils.divide(usedCPU,totalCPU,Constant.precision);
                        double memUsage = NumberUtils.divide(usedMem,totalMem,Constant.precision);
                        usage.setProjectId(projectId);
                        usage.setCpuTotal(totalCPU);
                        usage.setCpuUsed(usedCPU);
                        usage.setCpuUsage(cpuUsage);
                        usage.setMemoryTotal(totalMem);
                        usage.setMemoryUsed(usedMem);
                        usage.setMemoryUsage(memUsage);
                        usage.setStorageTotal(storage*Constant.G);
                        resultList.add(usage);
                    }
                    return resultList;
                }
            }, sql);

        }catch (Exception ex){
            log.error("stats CPU and Memory Usage Exception", ex);
            throw ex;
        }
    }

    /***
     *
     * @param projects 项目code 集合
     * @return key:projectCode, value:该目录下的数据容量
     * @throws IOException 如访问hdfs失败，则抛出IOException
     */
    public Map<String, Long> statsStorage(Set<String> projects) throws IOException, InterruptedException {
        String baseDir = "/chorus/project/%s/hive";
        Map<String, Long> result = new HashMap<>();
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(FileSystem.getDefaultUri(conf),conf,"hdfs");
        for(String projectCode : projects){
            long size = 0;
            Path path = new Path(String.format(baseDir, projectCode));
            if(fs.exists(path)) {
                size = fs.getContentSummary(path).getLength();
            }
            result.put(projectCode,size);
        }

        return result;
    }


    public Map<String,int[]> statsJobSuccessRato(Date date) throws Exception{
        String sql = "select ins.job_name,sum(if(bje.`STATUS`='COMPLETED', 1, 0))as successCount, count(1) total \n" +
                " from BATCH_JOB_EXECUTION bje inner join\n" +
                " BATCH_JOB_INSTANCE ins on bje.JOB_INSTANCE_ID = ins.JOB_INSTANCE_ID\n" +
                " where" +
                " bje.START_TIME>='%s'" +
                " and bje.START_TIME<='%s'" +
                " group by ins.JOB_NAME";

        try{
            String dt = format(date,"yyyy-MM-dd");
            sql = String.format(sql,String.format("%s 00:00:00", dt), String.format("%s 23:59:59", dt));
            if(log.isDebugEnabled())
                log.debug(String.format("stats job success rato SQL:%s",sql));
            return SQLUtil.query(this.xdDataSource, new ResultSolver<Map<String, int[]>>() {
                @Override
                public Map<String, int[]> result(ResultSet rs) throws SQLException {
                    Map<String,int[]> resultMap = new HashMap<>();
                    while(rs.next()){
                        String jobName = rs.getString(1);
                        int[] counts = new int[2];
                        counts[0]=rs.getInt(2);
                        counts[1]=rs.getInt(3);
                        resultMap.put(jobName,counts);
                    }
                    return resultMap;
                }
            },sql);
        }catch (Exception ex){
            log.error("stats Job Success Rato Exception", ex);
            throw ex;
        }

    }


    private String format(Date dt, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(dt);
    }

}
