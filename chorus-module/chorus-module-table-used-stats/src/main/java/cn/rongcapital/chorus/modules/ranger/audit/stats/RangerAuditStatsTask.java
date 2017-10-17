package cn.rongcapital.chorus.modules.ranger.audit.stats;

import cn.rongcapital.chorus.common.solr.SolrManager;
import cn.rongcapital.chorus.common.solr.SolrUtil;
import cn.rongcapital.chorus.common.solr.query.SearchCriteria;
import cn.rongcapital.chorus.common.solr.query.SearchField;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.modules.ranger.audit.stats.bean.ResourceUsedStats;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by hhlfl on 2017-6-19.
 */
@Slf4j
public class RangerAuditStatsTask {
    private String driveClassName="com.mysql.jdbc.Driver";
    private String url;
    private String userName;
    private String password;
    private String zkHosts;
    private String collection;
    private SolrManager solrMgr;
    private SolrUtil solrUtil;

    private static final int failedRetries=2;
    public RangerAuditStatsTask(String url, String userName, String password, String zkHosts, String collection) {
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.zkHosts = zkHosts;
        this.collection = collection;
        solrMgr = new SolrManager(zkHosts,collection);
        solrUtil = new SolrUtil();
    }

    /***
     *
     *
     * @param days 时间跨度
     * @param repos ranger中注册的资源名称，多一个用","分隔
     */
    public void doWork(int days, String repos) throws Exception{
        int i=0;
        log.info("=========>>资源使用度统计开始.");
        do{
            try {
                if(i>0)
                    log.info(String.format("资源使用度统计重试开始，第[%s]次重试。", i));

                statsAndSave(days, repos);
                break;
            } catch (Exception ex) {
                log.error("资源使用度统计失败。", ex);
                if(i==failedRetries)
                    throw ex;

                try {
                    Thread.sleep(5000l);
                } catch (InterruptedException e) {
                }
            }
        }while (i++<failedRetries);

        log.info("资源使用度统计结束<<==========");
    }

    private void statsAndSave(int days, String repos) throws Exception{
        SolrClient solrClient = solrMgr.getSolrClient();
        //policy,resType,resource,repo,event_count,evtTime
        //查询solr审计数据获得统计结果
        //按repo,和时间过滤
        List<SearchField> searchFields = new ArrayList<SearchField>();
        searchFields.add(new SearchField("repo","repo", SearchField.DATA_TYPE.STRING, SearchField.SEARCH_TYPE.FULL));
        searchFields.add(new SearchField("startDate", "evtTime",SearchField.DATA_TYPE.DATE, SearchField.SEARCH_TYPE.GREATER_EQUAL_THAN));
        searchFields.add(new SearchField("endDate", "evtTime", SearchField.DATA_TYPE.DATE,SearchField.SEARCH_TYPE.LESS_EQUAL_THAN));

        List<String> facetFields = new ArrayList<String>();
        facetFields.add("repo,policy,resType,resource");

        SearchCriteria criteria = new SearchCriteria();
        if(!StringUtils.isEmpty(repos)) {
            String[]repoArr = repos.split(",");
            criteria.addParam("repo", Arrays.asList(repoArr));
        }
        Date endDate = new Date();
        criteria.addParam("startDate",getLastNDate(endDate,days));
        criteria.addParam("endDate",endDate);
        criteria.setMaxRows(0);

        QueryResponse response = solrUtil.searchResources(criteria,searchFields,null,facetFields,solrClient);
        if(response == null){
            log.error("query resource used data from solr audit log is error.");
            throw new Exception("Error running query from solr server.");
        }

        //批量更新到resource_used_stats
        List<ResourceUsedStats> records=convertResult(response);
        batchInsertWitchOverride(records);
//        resourceUsedStatsService.batchInsertWitchOverride(records);
        records.clear();
    }

    private List<ResourceUsedStats> convertResult(QueryResponse response){
        List<ResourceUsedStats> records = new ArrayList<ResourceUsedStats>();
        if(response != null) {
            Map<String, Integer> gMap = new HashMap<String, Integer>();
            NamedList<List<PivotField>> facetPivot = response.getFacetPivot();
            List<PivotField> pivots  = facetPivot.getVal(0);
            group(pivots,"",gMap,new StringBuffer());
            Date date = new Date();
            for(Map.Entry<String, Integer> entry : gMap.entrySet()){
                String key = entry.getKey();
                Integer value = entry.getValue();
                ResourceUsedStats resourceUsedStats = new ResourceUsedStats();
                String[] fields = key.split(",");
                if(fields.length>3) {
                    resourceUsedStats.setServiceName(fields[0]);
                    resourceUsedStats.setPolicyId(fields[1]);
                    resourceUsedStats.setResourceType(fields[2]);
                    resourceUsedStats.setResourceName(fields[3]);
                    resourceUsedStats.setUsedCount(value.longValue());
                    resourceUsedStats.setUpdateTime(date);
                    records.add(resourceUsedStats);
                }else{
                    log.info(String.format("invalid data key[%s], count[%s].", key, value));
                }
            }
        }
        return records;
    }

    private void group(List<PivotField> pivotFields, Object pvalue, Map<String, Integer> gMap, StringBuffer key){
            if(pivotFields == null || pivotFields.isEmpty())
                return;

            for(PivotField pivotField : pivotFields){
                List<PivotField> subPivotFields = pivotField.getPivot();
                String name = pivotField.getField();
                int count = pivotField.getCount();
                Object value = pivotField.getValue();

                if("resType".equalsIgnoreCase(name) && value != null){
                    String val = (String)value;
                    if(val.startsWith("@")) {
                        value = val.substring(1);
                    }
                }

                if(subPivotFields == null || subPivotFields.isEmpty()){
                    if(!"resource".equalsIgnoreCase(name)){
                        //无resource列的数据，直接剔除
                        return;
                    }

                    String val = (String) value;
                    if("column".equalsIgnoreCase(pvalue.toString())) {
                        int index = val.lastIndexOf("/");
                        if (index != -1) {
                            val = val.substring(0, index);
                        }
                    }
                    val = val.replaceAll("/",".");

                    String k = key.toString()+val;
                    Integer sum = gMap.get(k);
                    sum = sum==null?count:sum+count;
                    gMap.put(k,sum);

                }else{
                    String val = value.toString();
                    key.append(val).append(",");
                    group(subPivotFields,value,gMap,key);
                    key.delete(key.length()-(val.length()+1), key.length());
                }
            }

    }

    private Date getLastNDate(Date date, int n){
        Date lastDate = date;
        if(n != 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_YEAR, -n);
            lastDate = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String ldate = sdf.format(lastDate);
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
            try {
                lastDate = sdf.parse(ldate + " 00:00:00");
            }catch (Exception ex){
                log.error(ex.getMessage(), ex);
            }
        }
        return lastDate;
    }


    public void batchInsertWitchOverride(List<ResourceUsedStats> records) throws Exception{

        Connection conn = null;
        Statement st = null;

        try{
            String sql = "delete from resource_used_stats";
            conn = JdbcConnector.getConnection(driveClassName,url,userName,password);
            conn.setAutoCommit(false);
            executeSQLNoResult(conn,sql);
            sql = "insert into resource_used_stats (policy_id, resource_name, service_name," +
                    "      resource_type, used_count, update_time" +
                    "      )" +
                    "    values (?,?,?,?,?,?)";
            batchInsert(conn,sql,records);
            conn.commit();
        }catch(Exception ex){
            log.error(String.format("save resource used stats data error."), ex);
            try {
                if(conn != null) {
                    conn.rollback();
                    log.info("data rollback.");
                }

            } catch (SQLException e) {

            }

            throw ex;

        }finally {
            if(conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {

                }
            }

        }

    }


    private void executeSQLNoResult(Connection conn, String sql) throws SQLException{
        Statement st = null;
        try{
            log.info(String.format("execute SQL:%s", sql));
            st = conn.createStatement();
            st.execute(sql);
        }finally {
           if(st != null){
               st.close();
           }
        }
    }

    private void batchInsert(Connection conn, String sql, List<ResourceUsedStats> records) throws SQLException{
        PreparedStatement pst = null;
        try{
            log.info(String.format("execute SQL:%s", sql));
            pst = conn.prepareStatement(sql);

            int batchSize=5000;
            int count = 0;
            for(ResourceUsedStats record: records){
                count++;
                pst.setString(1,record.getPolicyId());
                pst.setString(2,record.getResourceName());
                pst.setString(3,record.getServiceName());
                pst.setString(4,record.getResourceType());
                pst.setLong(5,record.getUsedCount()==null?0:record.getUsedCount());

                Date udate = record.getUpdateTime();
                if(udate == null)
                    udate = new Date();
                Timestamp ts = new Timestamp(udate.getTime());
                pst.setTimestamp(6, ts);
                pst.addBatch();

                if(count%batchSize == 0){
                    pst.executeBatch();
                }
            }

            if(count % batchSize != 0){
                pst.executeBatch();
            }

        }finally{
            if(pst != null)
                pst.close();
        }

    }


}
