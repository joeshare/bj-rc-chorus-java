package cn.rongcapital.chorus.modules.table_sample;

import cn.rongcapital.chorus.common.util.ProjectUtil;
import cn.rongcapital.chorus.hive.jdbc.HiveClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by abiton on 19/06/2017.
 */
@Data
@Slf4j
public class TableSampleTasklet implements Tasklet {
    private String dwDbName;
    private String table;
    private String outputTable;
    private String sampleType;
    private Integer sampleRate;
    private Integer sampleCount;
    private String where;
    private String dwConnectUrl;
    private String dwUserName;
    private boolean coverBefore;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        String queueName = dwDbName.substring(dwDbName.indexOf("_") + 1);
        String projectCode = queueName;
        String createHqlTemplate = "create table if not exists %s like %s location '" + ProjectUtil.DEFAULT_HIVE_TABLE_LOCATION_TEMPLATE + "'";
        String createHql = String.format(createHqlTemplate, outputTable, table, projectCode, outputTable);
        final long[] count = new long[1];

        if (SampleType.COUNT.name().equals(sampleType)) {
            count[0] = sampleCount;
        } else if (SampleType.RATE.name().equals(sampleType)) {
            final String[] countSql = {"select count(*) c from " + table};
            Long num = HiveClient.getInstance().executeHiveSql(dwConnectUrl, dwUserName, queueName, "",
                    dwDbName, "", countSql[0], null, new HiveClient.ResultSetConsumer<Long>() {
                        @Override
                        public Long apply(ResultSet rs) throws SQLException {
                            rs.next();
                            long c = rs.getLong(1);
                            if (c > 0) {
                                return c * sampleRate / 100;
                            }

                            return 0l;
                        }
                    });

            if(num == null)throw new RuntimeException(String.format("execute SQL Error.SQL:%s",countSql[0]));
            count[0]=num;
//            HiveConnector.executeHiveSql(dwConnectUrl, dwDbName, dwUserName,
//                    countSql[0], null, queueName, rs -> {
//                        rs.next();
//                        long c = rs.getLong(1);
//                        if (c > 0) {
//                            count[0] = c * sampleRate / 100;
//                        }
//                    });
            if (count[0] == 0) {
                return RepeatStatus.FINISHED;
            }
        } else {
            throw new RuntimeException("does not support sample type " + sampleType);
        }
        String overwrite = "overwrite";
        if (!coverBefore) {
            overwrite = "into";
        }
        String whereClause = "";
        if (!StringUtils.isEmpty(where)) {
            whereClause = " where " + where;
            //TODO where clause syntax check
        }

        boolean f = HiveClient.getInstance().executeHiveSqlNoRes(dwConnectUrl,dwUserName,queueName,"",dwDbName,"",createHql,null);
//        HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName, createHql, null, queueName);
        if(!f)throw new RuntimeException(String.format("execute SQL Error. SQL:%s", createHql));

        List<String> columns = new ArrayList<>();
        List<String> partitionColumns = new ArrayList<>();
        String descSQL = "desc " + outputTable;
        Boolean r = HiveClient.getInstance().executeHiveSql(dwConnectUrl, dwUserName, queueName, "", dwDbName, "", descSQL
                , null, new HiveClient.ResultSetConsumer<Boolean>() {
                    @Override
                    public Boolean apply(ResultSet rs) throws SQLException {
                        int split = 0;
                        while (rs.next()) {
                            String col_name = rs.getString("col_name");
                            if (StringUtils.isEmpty(col_name)) {
                                split++;
                            }
                            if (split == 0){
                                columns.add(col_name);
                            }
                            if (split == 2 && !StringUtils.isEmpty(col_name)) {
                                partitionColumns.add(col_name);
                            }
                        }

                        return true;
                    }
                });

        if(r == null)throw new RuntimeException(String.format("query column info error. SQL:", descSQL));

//        HiveConnector.executeHiveSql(dwConnectUrl,
//                dwDbName, dwUserName, "desc " + outputTable, null, queueName,
//                rs -> {
//                    int split = 0;
//                    while (rs.next()) {
//                        String col_name = rs.getString("col_name");
//                        if (StringUtils.isEmpty(col_name)) {
//                            split++;
//                        }
//                        if (split == 0){
//                           columns.add(col_name);
//                        }
//                        if (split == 2 && !StringUtils.isEmpty(col_name)) {
//                            partitionColumns.add(col_name);
//                        }
//                    }
//                }
//        );
        partitionColumns.remove("p_date");
        columns.remove("p_date");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        String columStr = String.join(",",columns);
        String partitionColumnStr = "p_date='" + date + "'";
        if (!partitionColumns.isEmpty()) {
            partitionColumnStr += "," + String.join(",", partitionColumns);
        }
        String sampleHqlTemplate = "insert %s table %s partition(%s)" +
                " select %s from %s %s  distribute by rand() sort by rand() limit %s";
        String sampleHql = String.format(sampleHqlTemplate, overwrite, outputTable, partitionColumnStr,
        columStr,table,whereClause,count[0]);
        log.info("sample sql is {}", sampleHql);
        System.out.println(sampleHql);
        f = HiveClient.getInstance().executeHiveSqlNoRes(dwConnectUrl,dwUserName,queueName,"",dwDbName,"",sampleHql,null);
        if(!f)throw new RuntimeException(String.format("execute SQL Error. SQL:%s", sampleHql));
//        HiveConnector.executeHiveSqlNoRes(dwConnectUrl, dwDbName, dwUserName, sampleHql, null, queueName);

        return RepeatStatus.FINISHED;
    }
}
