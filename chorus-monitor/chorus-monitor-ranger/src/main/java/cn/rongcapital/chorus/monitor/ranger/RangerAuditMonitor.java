package cn.rongcapital.chorus.monitor.ranger;

import cn.rongcapital.chorus.common.solr.SolrManager;
import cn.rongcapital.chorus.common.solr.SolrUtil;
import cn.rongcapital.chorus.common.solr.query.SearchCriteria;
import cn.rongcapital.chorus.common.solr.query.SearchField;
import cn.rongcapital.chorus.common.solr.query.SortField;
import cn.rongcapital.chorus.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CursorMarkParams;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Ranger审计日志 定时轮询功能
 *
 * @author li.hzh
 */
@Slf4j
@Component
public class RangerAuditMonitor implements InitializingBean {

    @Value("${zookeeper.address}")
    private String zkHosts;
    private final String collection = "ranger_audits";
    private SolrManager solrMgr;
    private SolrUtil solrUtil;
    private Date lastEventDate;
    private static final int ROW_COUNT = 100;
    @Autowired
    private RangerAuditDeniedHandler rangerAuditDeniedHandler;

    @Scheduled(cron = "${monitor.ranger.audit.task.cron}")
    public void run() {
        SolrClient solrClient = solrMgr.getSolrClient();
        List<SearchField> searchFields = new ArrayList<SearchField>();
        searchFields.add(new SearchField("result", "result", SearchField.DATA_TYPE.STRING, SearchField.SEARCH_TYPE.FULL));
        searchFields.add(new SearchField("startDate", "evtTime", SearchField.DATA_TYPE.DATE, SearchField.SEARCH_TYPE.GREATER_EQUAL_THAN));
        searchFields.add(new SearchField("endDate", "evtTime", SearchField.DATA_TYPE.DATE, SearchField.SEARCH_TYPE.LESS_EQUAL_THAN));

        SearchCriteria criteria = new SearchCriteria();
        // 需要转换成UTC时间查询
        Date end = getEndDate();
        Date start = getStartDate(end, 1);
        criteria.addParam("startDate", start);
        criteria.addParam("endDate", end);
        criteria.addParam("result", 0);
        criteria.setSortBy("id");
        criteria.setMaxRows(ROW_COUNT);
        String cursorMark = CursorMarkParams.CURSOR_MARK_START;
        List<SortField> sortFields = new ArrayList<>();
        sortFields.add(new SortField("id", "id"));
        boolean done = false;
        while (!done) {
            criteria.setCursorMark(cursorMark);
            QueryResponse response = solrUtil.searchResources(criteria, searchFields, sortFields, null, solrClient);
            if (response == null) {
                done = true;
                continue;
            }
            rangerAuditDeniedHandler.handleResponse(response);
            String nextCursorMark = response.getNextCursorMark();
            if (cursorMark.equals(nextCursorMark)) {
                done = true;
            }
            cursorMark = nextCursorMark;
        }
        lastEventDate = end;
    }

    private Date getEndDate() {
        ZonedDateTime dateTime = ZonedDateTime.now(ZoneOffset.ofHours(8));
        return new Date(dateTime.minusHours(8).toInstant().toEpochMilli());
    }

    private Date getStartDate(Date end, int minute) {
        if (lastEventDate == null) {
            ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(end.getTime()), ZoneOffset.ofHours(8));
            lastEventDate = new Date(dateTime.minusMinutes(minute).toInstant().toEpochMilli());
        }
        return lastEventDate;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        solrMgr = new SolrManager(zkHosts + "/" + collection, collection);
        solrUtil = new SolrUtil();
    }

}
