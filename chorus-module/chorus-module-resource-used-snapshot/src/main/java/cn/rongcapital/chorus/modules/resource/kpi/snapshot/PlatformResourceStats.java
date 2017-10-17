package cn.rongcapital.chorus.modules.resource.kpi.snapshot;

import cn.rongcapital.chorus.modules.resource.kpi.snapshot.bean.PlatformResourceKPISnapshot;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by hhlfl on 2017-7-20.
 */
@Slf4j
@Component
public class PlatformResourceStats {
    @Autowired
    private DataSource chorusDataSource;
    @Autowired
    private YarnClient yarnClient;

    private FileSystem fileSystem;

    public PlatformResourceStats(){
        Configuration conf = new Configuration();
        try {
            fileSystem = FileSystem.get(conf);
        }catch (IOException ex){
            log.error("connect hdfs file system exception.", ex);
        }
    }

    public void setFileSystem(FileSystem fileSystem){
        this.fileSystem = fileSystem;
    }

    public void snapshot() throws Exception{
        PlatformResourceKPISnapshot kpiSnapshot = create();
        statsPlatformTotalResource(kpiSnapshot);
        statsPlatformTotalUsedResource(kpiSnapshot, kpiSnapshot.getSnapshotDate());
        saveSnapshot(kpiSnapshot);
    }

    private PlatformResourceKPISnapshot create(){
        PlatformResourceKPISnapshot kpiSnapshot = new PlatformResourceKPISnapshot();
        kpiSnapshot.setStorageUsed(0);
        kpiSnapshot.setMemoryUsed(0);
        kpiSnapshot.setCpuUsed(0);
        kpiSnapshot.setCpuTotal(0);
        kpiSnapshot.setMemoryTotal(0);
        kpiSnapshot.setStorageTotal(0);
        kpiSnapshot.setSnapshotDate(new java.sql.Date(System.currentTimeMillis()));
        kpiSnapshot.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return kpiSnapshot;
    }

    public void saveSnapshot(PlatformResourceKPISnapshot kpiSnapshot) throws SQLException{
        String sql = "REPLACE INTO platform_resource_snapshot " +
                "(snapshot_date, applied_cpu, platform_cpu, applied_memory, platform_memory, applied_storage, platform_storage, create_time)" +
                "VALUES(?,?,?,?,?,?,?,?)";
        
        SQLUtil.execute(chorusDataSource, sql, new Function<PlatformResourceKPISnapshot>() {
            @Override
            public void prepared(PlatformResourceKPISnapshot record, PreparedStatement pst) throws SQLException {
                pst.setDate(1,record.getSnapshotDate());
                pst.setInt(2,record.getCpuUsed());
                pst.setInt(3,record.getCpuTotal());
                pst.setInt(4,record.getMemoryUsed());
                pst.setInt(5,record.getMemoryTotal());
                pst.setInt(6,(int)(record.getStorageUsed()/Constant.G));
                pst.setInt(7,(int)(record.getStorageTotal()/Constant.G));
                pst.setTimestamp(8,record.getCreateTime());
                pst.executeUpdate();
            }
        },kpiSnapshot);
    }

    public void statsPlatformTotalUsedResource(final PlatformResourceKPISnapshot snapshot, Date date)throws SQLException{
        String sql = "select sum(cpu_total)as totalCPU, sum(memory_total)as totalMemory, sum(storage_total)as totalStorage from project_resource_kpi_snapshot \n" +
                "where kpi_date='%s'";

        sql = String.format(sql,new SimpleDateFormat("yyyy-MM-dd").format(date));
        log.debug("stats platform total used resource SQL:{}",sql);
        SQLUtil.query(chorusDataSource, new ResultSolver<Boolean>() {
            @Override
            public Boolean result(ResultSet rs) throws SQLException {
                if(rs.next()) {
                    snapshot.setCpuUsed(rs.getInt("totalCPU"));
                    snapshot.setMemoryUsed(rs.getInt("totalMemory"));
                    snapshot.setStorageUsed(rs.getLong("totalStorage"));
                }

                return true;
            }
        },sql);
    }

    public void statsPlatformTotalResource(PlatformResourceKPISnapshot snapshot) throws Exception{
        List<NodeReport> nodeReportList = getNodeReport();
        int cpuTotal=0;
        int memoryTotal=0;
        for(NodeReport nodeReport : nodeReportList){
            cpuTotal += nodeReport.getCapability().getVirtualCores();
            memoryTotal += nodeReport.getCapability().getMemory();
        }

        QueueInfo queueInfo = getQueueInfo(Constant.CHORUS_QUEUE);
        float capacity = 1.0f;
        if(queueInfo != null)
            capacity = queueInfo.getCapacity();

        snapshot.setCpuTotal((int) Math.floor(cpuTotal*capacity));
        snapshot.setMemoryTotal((int)Math.floor(memoryTotal*capacity/1024));
        snapshot.setStorageTotal(getTotalCapacity());
    }

    private  List<NodeReport> getNodeReport() throws Exception{
        try {
            return yarnClient.getNodeReports(NodeState.RUNNING);
        } catch (YarnException | IOException e) {
            log.error("yarn exception ", e);
            throw e;
        }

    }

    private QueueInfo getQueueInfo(String queueName) throws IOException, YarnException {
        return yarnClient.getQueueInfo(queueName);
    }

    private long getTotalCapacity() throws IOException{
        try {
            return  fileSystem.getStatus().getCapacity();
        } catch (IOException e) {
            log.error("get capacity from hdfs error ",e);
            throw e;
        }
    }

}
